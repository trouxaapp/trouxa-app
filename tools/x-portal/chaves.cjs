// Gera e captura as chaves do app no console do X. Os valores aparecem uma
// vez só, então tudo é salvo em out/chaves-*.json imediatamente.
//
// Uso: node chaves.cjs <consumer|token|bearer>

const fs = require("fs");
const path = require("path");
const { abrir } = require("./pw.cjs");
const cfg = JSON.parse(fs.readFileSync(path.join(__dirname, "config.json"), "utf8"));
const SAIDA = path.join(__dirname, "out");
fs.mkdirSync(SAIDA, { recursive: true });

const APP = "https://console.x.com/accounts/2120217077688811569/apps/33627632";
const MODOS = {
  consumer: { marcador: "Consumer Key", rotulo: "regenerate" },
  // "Access Token" aparece duas vezes (OAuth 1.0 e OAuth 2.0); o texto
  // "For @trouxaapp" é exclusivo do token do OAuth 1.0, que é o que usamos.
  token: { marcador: "For @trouxaapp", rotulo: "generate" },
  bearer: { marcador: "Bearer Token", rotulo: "generate" },
};


/** Clica num botão que esteja no mesmo bloco do marcador (ex: "Consumer Key"). */
async function clicarPerto(page, marcador, rotulo) {
  return page.evaluate(
    ([marc, rot]) => {
      const iguais = Array.from(document.querySelectorAll("div,span,h3,h2,label,p")).filter(
        (e) => (e.innerText || "").trim() === marc
      );
      let node = iguais[iguais.length - 1];
      for (let i = 0; i < 8 && node; i++) {
        const b = Array.from(node.querySelectorAll("button")).find((x) =>
          (x.innerText || "").trim().toLowerCase().includes(rot)
        );
        if (b) {
          b.click();
          return (b.innerText || "").trim();
        }
        node = node.parentElement;
      }
      return "";
    },
    [marcador, rotulo]
  );
}

/** Clica o botão de confirmação DENTRO do diálogo (senão pega botão de fundo). */
async function confirmar(page) {
  return page.evaluate(() => {
    const dial = Array.from(document.querySelectorAll('[role="dialog"], dialog, [aria-modal="true"]'));
    for (const d of dial) {
      const b = Array.from(d.querySelectorAll("button")).find((x) =>
        /^(yes[, ]|sim[, ]?|confirmar)/i.test((x.innerText || "").trim())
      );
      if (b) {
        b.click();
        return (b.innerText || "").trim();
      }
    }
    return "";
  });
}

const PARECE_CHAVE = /^[A-Za-z0-9_-]{18,80}$/;

async function capturar(page, etiqueta) {
  const dados = await page.evaluate(
    (regex) => {
      const re = new RegExp(regex);
      const dial = Array.from(document.querySelectorAll('[role="dialog"], dialog, [aria-modal="true"]'));
      const escopo = dial.length ? dial[dial.length - 1] : document.body;
      const achados = [];
      escopo.querySelectorAll("input,textarea").forEach((i) => {
        if (i.value && re.test(i.value.trim())) {
          achados.push({ onde: "campo", id: i.id || i.name || "", valor: i.value.trim() });
        }
      });
      escopo.querySelectorAll("span,code,p,div,h4,label").forEach((e) => {
        const t = (e.innerText || "").trim();
        if (t && t.length <= 90 && re.test(t)) achados.push({ onde: e.tagName.toLowerCase(), valor: t });
      });
      const vistos = new Set();
      const unicos = achados.filter((a) => !vistos.has(a.valor) && vistos.add(a.valor));
      return {
        valores: unicos.slice(0, 25),
        dialogo: dial.length ? (dial[dial.length - 1].innerText || "").slice(0, 1200) : "",
        texto: (document.body.innerText || "").slice(0, 2000),
      };
    },
    PARECE_CHAVE.source
  );
  fs.writeFileSync(path.join(SAIDA, `chaves-${etiqueta}.json`), JSON.stringify(dados, null, 2), "utf8");
  return dados;
}

(async () => {
  const modo = MODOS[process.argv[2]];
  const ctx = await abrir(cfg);
  const page = ctx.pages()[0] || (await ctx.newPage());
  const saida = {};

  try {
    if (!modo) throw new Error("modo: consumer | token | bearer");
    await page.goto(APP, { waitUntil: "domcontentloaded", timeout: 60000 });
    await page.waitForTimeout(8000);

    saida.clicou = await clicarPerto(page, modo.marcador, modo.rotulo);
    await page.waitForTimeout(4000);
    saida.dialogo = (await page.locator("body").innerText()).slice(0, 700);

    if (/regenerate|generate/i.test(saida.dialogo) && saida.clicou) {
      saida.confirmou = await confirmar(page);
      await page.waitForTimeout(6000);
    }

    const dados = await capturar(page, process.argv[2]);
    saida.valores = dados.valores.map((v) => `${v.valor.slice(0, 6)}…${v.valor.slice(-4)}`);
    saida.dump = `out/chaves-${process.argv[2]}.json`;
    saida.textoFinal = dados.texto.slice(0, 900);
  } catch (e) {
    saida.erro = String(e.message || e).slice(0, 300);
  }

  await page.screenshot({ path: path.join(SAIDA, `chaves-${process.argv[2] || "x"}.png`) }).catch(() => {});
  console.log(JSON.stringify(saida, null, 2));
  await ctx.close();
})();

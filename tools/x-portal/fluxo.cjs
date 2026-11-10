// Fluxo completo: entrar no X (que manda código por e-mail), ler o código no
// Proton pelo mesmo browser e confirmar. Um run por vez, porque a janela do
// código é curta.
//
// Uso: node fluxo.cjs [--manter]

const fs = require("fs");
const path = require("path");
const { abrir } = require("./pw.cjs");
const cfg = JSON.parse(fs.readFileSync(path.join(__dirname, "config.json"), "utf8"));
const SAIDA = path.join(__dirname, "out");
fs.mkdirSync(SAIDA, { recursive: true });

const LOGIN_X =
  "https://x.com/i/jf/onboarding/web?mode=login&redirect_after_login=https%3A%2F%2Fdeveloper.x.com%2Fen%2Fportal%2Fdashboard";

const log = (...a) => console.log("·", ...a);
const recorte = (s, n = 1200) => (s || "").replace(/\n{3,}/g, "\n").slice(0, n);

function salvar(nome, texto) {
  fs.writeFileSync(path.join(SAIDA, nome), texto, "utf8");
}


async function passoX(x) {
  await x.goto(LOGIN_X, { waitUntil: "domcontentloaded", timeout: 60000 });
  await x.waitForTimeout(6000);

  if (await x.locator('input[name="username_or_email"]').count()) {
    // O campo é controlado por React; o setter nativo + eventos é o que o app
    // realmente escuta (fill() sozinho deixa o estado interno vazio e a tela
    // responde "E-mail inválido").
    await x.evaluate(
      ([email, senha]) => {
        const set = (sel, valor) => {
          const el = document.querySelector(sel);
          if (!el) return;
          const d = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(el), "value").set;
          d.call(el, valor);
          el.dispatchEvent(new Event("input", { bubbles: true }));
          el.dispatchEvent(new Event("change", { bubbles: true }));
        };
        set('input[name="username_or_email"]', email);
        set('input[name="password"]', senha);
      },
      [cfg.email, cfg.senha]
    );
    await x.waitForTimeout(700);
    // o submit certo é o último botão do bloco que contém a senha
    await x.evaluate(() => {
      const p = document.querySelector('input[name="password"]');
      let node = p;
      for (let i = 0; i < 6 && node; i++) node = node.parentElement;
      const botoes = node ? Array.from(node.querySelectorAll('button[type="submit"]')) : [];
      const alvo = botoes[botoes.length - 1] || document.querySelector('button[type="submit"]');
      if (alvo) alvo.click();
    });
    await x.waitForTimeout(10000);
  }

  const estado = {
    url: x.url(),
    texto: recorte(await x.locator("body").innerText().catch(() => ""), 400),
    inputs: await x.evaluate(() =>
      Array.from(document.querySelectorAll("input")).map((e) => ({
        name: e.name,
        type: e.type,
        ac: e.getAttribute("autocomplete"),
        vazio: !e.value,
        visivel: !!(e.offsetWidth || e.offsetHeight),
      }))
    ),
  };
  return estado;
}

async function passoProton(p) {
  await p.goto("https://mail.proton.me/u/0/inbox", { waitUntil: "domcontentloaded", timeout: 60000 });
  await p.waitForTimeout(8000);

  if (!/mail\.proton\.me/.test(p.url())) {
    await p.goto("https://account.proton.me/login", { waitUntil: "domcontentloaded" });
    await p.waitForTimeout(6000);
    await p.fill("#username", cfg.email).catch(() => {});
    await p.locator('button:has-text("Continuar")').last().click().catch(() => {});
    await p.waitForTimeout(7000);
    const senha = p.locator("#password, input[type=password]").first();
    if (await senha.count()) {
      await senha.fill(cfg.senhaProton);
      await p
        .locator('button:has-text("Iniciar"), button:has-text("Entrar"), button[type="submit"]')
        .last()
        .click()
        .catch(() => {});
      await p.waitForTimeout(20000);
    }
    await p.goto("https://mail.proton.me/u/0/inbox", { waitUntil: "domcontentloaded" }).catch(() => {});
    await p.waitForTimeout(15000);
  }

  const url = p.url();
  const texto = recorte(await p.locator("body").innerText().catch(() => ""), 1500);
  salvar("proton-inbox.txt", texto);
  return { url, texto };
}

/** Procura o código no texto do e-mail mais recente do X. */
function extrairCodigo(texto) {
  const m = texto.match(/\b(\d{6})\b/);
  if (m) return m[1];
  const m2 = texto.match(/(?:c[óo]digo|code)[^\dA-Z]{0,20}([A-Z0-9]{6,8})/i);
  return m2 ? m2[1] : "";
}

(async () => {
  const ctx = await abrir(cfg);
  const x = ctx.pages()[0] || (await ctx.newPage());
  const proton = await ctx.newPage();
  const saida = {};

  try {
    log("passo 1: login no X");
    saida.x = await passoX(x);
    salvar("x-login.txt", JSON.stringify(saida.x, null, 2));
    log("url X:", saida.x.url);

    const pedeCodigo = /verify_code|Verification code/i.test(saida.x.url + saida.x.texto);
    log("pediu código?", pedeCodigo);
    if (!pedeCodigo) {
      saida.fim = "login não pediu código (talvez já esteja logado)";
    } else {
      log("passo 2: abrir Proton e ler o e-mail");
      saida.proton = await passoProton(proton);
      log("url Proton:", saida.proton.url);

      const textoEmail = saida.proton.texto;
      // clica na mensagem do X, se a lista aparecer
      const linha = proton
        .locator('[data-testid="message-item"], [data-testid="message-list-item"], li')
        .filter({ hasText: /(^|\s)(X|Twitter)/i })
        .first();
      if (await linha.count()) {
        await linha.click().catch(() => {});
        await proton.waitForTimeout(6000);
        const corpo = await proton.locator("body").innerText().catch(() => "");
        salvar("proton-email.txt", recorte(corpo, 3000));
        saida.codigo = extrairCodigo(corpo);
      }
      if (!saida.codigo) {
        const alvo = await proton.locator("body").innerText().catch(() => "");
        saida.codigo = extrairCodigo(alvo) || extrairCodigo(textoEmail);
      }
      log("código encontrado?", saida.codigo || "(nada)");

      if (saida.codigo) {
        log("passo 3: confirmar o código no X");
        await x.bringToFront();
        const campo = x
          .locator('input[name="verification_code"], input[autocomplete="one-time-code"], input[type="text"]')
          .first();
        await campo.fill(saida.codigo).catch(() => {});
        await x.waitForTimeout(500);
        await x.locator('button[type="submit"]').last().click().catch(() => x.keyboard.press("Enter"));
        await x.waitForTimeout(12000);
        await x.goto(cfg.portal, { waitUntil: "domcontentloaded" }).catch(() => {});
        await x.waitForTimeout(8000);
        saida.depois = { url: x.url(), texto: recorte(await x.locator("body").innerText().catch(() => ""), 800) };
      }
    }
  } catch (e) {
    saida.erro = String(e.message || e).slice(0, 400);
  }

  await x.screenshot({ path: path.join(SAIDA, "fim-x.png") }).catch(() => {});
  await proton.screenshot({ path: path.join(SAIDA, "fim-proton.png") }).catch(() => {});
  console.log(JSON.stringify(saida, null, 2));
  await ctx.close();
})();

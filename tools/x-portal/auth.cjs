// Configura as permissões do app (Read and write + tipo) e as URLs exigidas.
// Sem isso o Access Token nasce só com leitura e nenhum post sai.
//
// Uso: node auth.cjs [url-do-app]

const fs = require("fs");
const path = require("path");
const { abrir } = require("./pw.cjs");
const cfg = JSON.parse(fs.readFileSync(path.join(__dirname, "config.json"), "utf8"));
const SAIDA = path.join(__dirname, "out");
fs.mkdirSync(SAIDA, { recursive: true });

const SITE = "https://trouxaapp.github.io/trouxa-app/";
const SETTINGS =
  "https://console.x.com/accounts/2120217077688811569/apps/settings?appId=33627632";

const recorte = (s, n = 1500) => (s || "").replace(/\n{3,}/g, "\n").slice(0, n);


/** Clique real (mouse) no elemento cujo texto é exatamente esse. */
async function clicarExato(page, texto) {
  const alvo = page.getByText(texto, { exact: true });
  const n = await alvo.count();
  const tentativas = [];
  for (let i = n - 1; i >= 0 && i >= n - 4; i--) {
    try {
      await alvo.nth(i).click({ timeout: 6000 });
      tentativas.push(`ok #${i}`);
      break;
    } catch (e) {
      tentativas.push(`falha #${i}: ${String(e.message).slice(0, 40)}`);
    }
  }
  return tentativas;
}

async function preencher(page, sel, valor) {
  return page.evaluate(
    ([s, v]) => {
      const el = document.querySelector(s);
      if (!el) return false;
      const d = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(el), "value").set;
      d.call(el, v);
      el.dispatchEvent(new Event("input", { bubbles: true }));
      el.dispatchEvent(new Event("change", { bubbles: true }));
      el.dispatchEvent(new Event("blur", { bubbles: true }));
      return true;
    },
    [sel, valor]
  );
}

(async () => {
  const ctx = await abrir(cfg);
  const page = ctx.pages()[0] || (await ctx.newPage());
  const saida = {};

  try {
    await page.goto(SETTINGS, { waitUntil: "domcontentloaded", timeout: 60000 });
    await page.waitForTimeout(7000);

    saida.cliques = {
      permissao: await clicarExato(page, "Read and write"),
      tipo: await clicarExato(page, "Web App, Automated App or Bot"),
    };
    await page.waitForTimeout(1200);

    saida.callback = await preencher(page, "#callback-url-0", SITE);
    saida.website = await preencher(page, "#website-url", SITE);
    await page.waitForTimeout(1200);

    saida.antesDeSalvar = recorte(await page.locator("body").innerText().catch(() => ""), 600);
    fs.writeFileSync(path.join(SAIDA, "auth-antes.txt"), saida.antesDeSalvar, "utf8");

    await page.getByRole("button", { name: "Save Changes" }).click({ timeout: 15000 }).catch(() => {});
    await page.waitForTimeout(9000);
    saida.depois = { url: page.url(), texto: recorte(await page.locator("body").innerText().catch(() => ""), 900) };

    // recarrega para conferir o que ficou gravado
    const url = page.url();
    await page.goto(url, { waitUntil: "domcontentloaded" }).catch(() => {});
    await page.waitForTimeout(8000);
    saida.persistido = await page.evaluate(() => ({
      callback: (document.querySelector("#callback-url-0") || {}).value || "",
      website: (document.querySelector("#website-url") || {}).value || "",
      texto: (document.body.innerText || "").slice(0, 1200),
    }));
  } catch (e) {
    saida.erro = String(e.message || e).slice(0, 400);
  }

  await page.screenshot({ path: path.join(SAIDA, "auth.png") }).catch(() => {});
  console.log(JSON.stringify(saida, null, 2));
  await ctx.close();
})();

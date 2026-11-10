// Harness de browser para o portal de desenvolvedor do X.
// Mantém um perfil persistente (cookies/login guardados) e oferece comandos
// pequenos, para dar um passo por vez e ler o resultado sem precisar de visão.
//
// Uso:
//   node x.cjs dump [url]            -> url, título e texto visível da página
//   node x.cjs links [url]           -> todos os links (texto + href)
//   node x.cjs click "<texto>"       -> clica no primeiro elemento com esse texto
//   node x.cjs fill "<sel>" "<valor>"-> preenche um seletor
//   node x.cjs eval "<js>" [url]     -> roda JS na página e imprime o retorno
//   node x.cjs login                 -> fluxo de login do X
//   node x.cjs snap <nome>           -> screenshot em out/

const fs = require("fs");
const path = require("path");

const { abrir } = require("./pw.cjs");

const cfg = JSON.parse(fs.readFileSync(path.join(__dirname, "config.json"), "utf8"));
const SAIDA = path.join(__dirname, "out");
fs.mkdirSync(SAIDA, { recursive: true });


const recortar = (s, n = 3500) => (s || "").replace(/\n{3,}/g, "\n\n").slice(0, n);

const ARQ_ULTIMO = path.join(SAIDA, "last.json");
const ultimaUrl = () => {
  try {
    return JSON.parse(fs.readFileSync(ARQ_ULTIMO, "utf8")).url;
  } catch {
    return "";
  }
};

async function estado(page) {
  fs.writeFileSync(ARQ_ULTIMO, JSON.stringify({ url: page.url() }), "utf8");
  return {
    url: page.url(),
    titulo: await page.title().catch(() => ""),
    texto: recortar(await page.locator("body").innerText().catch(() => "")),
  };
}

async function ir(page, url, espera = 3500) {
  if (url) await page.goto(url, { waitUntil: "domcontentloaded", timeout: 60000 });
  await page.waitForTimeout(espera);
}

async function clicarPorTexto(page, texto) {
  const alvos = [
    page.getByRole("button", { name: texto, exact: false }),
    page.getByRole("link", { name: texto, exact: false }),
    page.locator(`text=${texto}`),
  ];
  for (const alvo of alvos) {
    const n = await alvo.count().catch(() => 0);
    for (let i = 0; i < Math.min(n, 5); i++) {
      const el = alvo.nth(i);
      if (await el.isVisible().catch(() => false)) {
        await el.click({ timeout: 15000 }).catch(() => {});
        return true;
      }
    }
  }
  return false;
}

async function login(page) {
  await ir(page, "https://x.com/i/flow/login", 6000);
  const passos = [];

  // 1. usuário
  const campo = page.locator('input[autocomplete="username"], input[name="text"]').first();
  if (await campo.count()) {
    await campo.fill(cfg.email).catch(() => {});
    await page.keyboard.press("Enter");
    await page.waitForTimeout(4000);
    passos.push("email preenchido");
  }

  // 2. às vezes o X pede o @usuario aqui
  const extra = page.locator('input[name="text"]').first();
  if (await extra.count()) {
    const v = await extra.inputValue().catch(() => "x");
    if (!v) {
      await extra.fill(cfg.usuario).catch(() => {});
      await page.keyboard.press("Enter");
      await page.waitForTimeout(3500);
      passos.push("usuario preenchido");
    }
  }

  // 3. senha
  const senha = page.locator('input[name="password"], input[type="password"]').first();
  if (await senha.count()) {
    await senha.fill(cfg.senha).catch(() => {});
    await page.keyboard.press("Enter");
    await page.waitForTimeout(8000);
    passos.push("senha preenchida");
  }

  await ir(page, cfg.portal, 6000);
  return passos;
}

(async () => {
  const [cmd, a, b, c] = process.argv.slice(2);
  const ctx = await abrir(cfg);
  const page = ctx.pages()[0] || (await ctx.newPage());
  page.setDefaultTimeout(30000);
  let saida;

  try {
    switch (cmd) {
      case "dump":
        await ir(page, a || cfg.portal);
        saida = await estado(page);
        break;
      case "links": {
        await ir(page, a || cfg.portal);
        saida = await page.evaluate(() =>
          Array.from(document.querySelectorAll("a[href]"))
            .map((x) => ({ texto: (x.innerText || "").trim().slice(0, 60), href: x.href }))
            .filter((x) => x.texto)
            .slice(0, 120)
        );
        break;
      }
      case "click":
        if (b) await ir(page, b);
        saida = { clicou: await clicarPorTexto(page, a) };
        await page.waitForTimeout(4000);
        saida.estado = await estado(page);
        break;
      case "fill":
        if (c) await ir(page, c);
        await page.locator(a).first().fill(b);
        saida = { ok: true, estado: await estado(page) };
        break;
      case "eval":
        await ir(page, b || ultimaUrl());
        saida = await page.evaluate(a);
        break;
      case "entrar": {
        const url =
          "https://x.com/i/jf/onboarding/web?mode=login&redirect_after_login=https%3A%2F%2Fdeveloper.x.com%2Fen%2Fportal%2Fdashboard";
        await ir(page, url, 6000);
        await page.fill('input[name="username_or_email"]', cfg.email);
        await page.waitForTimeout(500);
        await page.fill('input[name="password"]', cfg.senha);
        await page.waitForTimeout(500);
        const botao = page
          .locator('button[type="submit"], button:has-text("Continuar"), button:has-text("Entrar")')
          .last();
        if (await botao.count()) {
          await botao.click().catch(() => page.keyboard.press("Enter"));
        } else {
          await page.keyboard.press("Enter");
        }
        await page.waitForTimeout(10000);
        await ir(page, cfg.portal, 5000);
        saida = await estado(page);
        break;
      }
      case "snap":
        await page.screenshot({ path: path.join(SAIDA, `${a || "pagina"}.png`), fullPage: false });
        saida = { ok: true, arquivo: path.join(SAIDA, `${a || "pagina"}.png`) };
        break;
      case "login":
        saida = { passos: await login(page), ...(await estado(page)) };
        break;
      default:
        saida = { ajuda: "dump|links|click|fill|eval|snap|login" };
    }
  } catch (e) {
    saida = { erro: String(e.message || e).slice(0, 500), url: page.url() };
  }

  console.log(JSON.stringify(saida, null, 2));
  await page.screenshot({ path: path.join(SAIDA, "ultimo.png") }).catch(() => {});
  await ctx.close();
})();

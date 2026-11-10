// Compra de credito no console do X (Stripe Checkout embutido).
// O valor do endereco sai do CEP via ViaCEP, para o usuario so informar o CEP.
//
// Uso:
//   node pagar.cjs --cartao=4111111111111111 --validade=12/30 --cvc=123 \
//     --nome="FULANO DE TAL" --cep=01001000 [--numero=123] [--valor=$5.00] [--espera=180]

const fs = require("fs");
const path = require("path");
const { abrir } = require("./pw.cjs");
const cfg = JSON.parse(fs.readFileSync(path.join(__dirname, "config.json"), "utf8"));
const SAIDA = path.join(__dirname, "out");

const arg = (nome, padrao = "") =>
  (process.argv.find((a) => a.startsWith(`--${nome}=`)) || "").split("=").slice(1).join("=") || padrao;

const CARTAO = arg("cartao").replace(/\s+/g, "");
const VALIDADE = arg("validade");
const CVC = arg("cvc");
const NOME = arg("nome");
const CEP = arg("cep").replace(/\D/g, "");
const NUMERO = arg("numero");
const VALOR = arg("valor", "$5.00");
const ESPERA = Number(arg("espera", "180"));

const recorte = (s, n = 1500) => (s || "").replace(/\n{3,}/g, "\n").slice(0, n);

async function viaCep(cep) {
  const r = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
  const j = await r.json();
  if (j.erro) throw new Error("CEP não encontrado");
  return j; // {logradouro, bairro, localidade, uf}
}

async function frameCheckout(page) {
  for (let i = 0; i < 30; i++) {
    const f = page.frames().find((x) => x.url().includes("embedded-checkout-inner"));
    if (f) {
      const n = await f.locator("#cardNumber").count().catch(() => 0);
      if (n) return f;
    }
    await page.waitForTimeout(2000);
  }
  return null;
}

(async () => {
  const ctx = await abrir(cfg);
  const page = ctx.pages()[0] || (await ctx.newPage());
  const saida = {};

  try {
    const end = await viaCep(CEP);
    saida.endereco = `${end.logradouro}, ${end.bairro} - ${end.localidade}/${end.uf}`;

    await page.goto("https://console.x.com/accounts/2120217077688811569/billing/credits", {
      waitUntil: "domcontentloaded",
      timeout: 60000,
    });
    await page.waitForTimeout(8000);
    await page.getByText("Buy credits", { exact: true }).first().click();
    await page.waitForTimeout(2500);
    await page.getByText(VALOR, { exact: true }).first().click();
    await page.waitForTimeout(1200);
    await page.getByRole("button", { name: /continue to payment/i }).click();
    await page.waitForTimeout(8000);

    const f = await frameCheckout(page);
    if (!f) throw new Error("não achei o formulário do Stripe");
    saida.achouFormulario = true;

    await f.fill("#email", cfg.email).catch(() => {});
    await f.fill("#cardNumber", CARTAO);
    await f.fill("#cardExpiry", VALIDADE);
    await f.fill("#cardCvc", CVC);
    await f.fill("#billingName", NOME);
    await f.selectOption("#billingCountry", "BR").catch(() => {});
    await page.waitForTimeout(1500);
    await f.fill("#billingAddressLine1", `${end.logradouro}${NUMERO ? ", " + NUMERO : ""}`);
    await f.fill("#billingAddressLine2", end.bairro || "");
    await f.fill("#billingPostalCode", CEP);
    await f.fill("#billingLocality", end.localidade);
    await page.waitForTimeout(1000);

    saida.preenchido = await f.evaluate(() =>
      ["email", "cardNumber", "cardExpiry", "cardCvc", "billingName", "billingPostalCode", "billingLocality"].map(
        (id) => {
          const el = document.getElementById(id);
          return `${id}=${el && el.value ? (id === "cardNumber" ? "…" + el.value.slice(-4) : el.value) : "VAZIO"}`;
        }
      )
    );

    const pagar = f.getByRole("button", { name: /pagar|pay/i });
    saida.clicouPagar = await pagar.click({ timeout: 20000 }).then(() => true).catch(() => false);

    // espera o processamento (e eventual 3DS, que o usuario resolve na janela)
    let concluido = false;
    for (let i = 0; i < ESPERA / 5; i++) {
      await page.waitForTimeout(5000);
      const textoPagina = await page.locator("body").innerText().catch(() => "");
      if (/sucesso|success|obrigad|thank/i.test(textoPagina) || (await page.frames().some((x) => x.url().includes("embedded-checkout")))) {
        const aindaAberto = (await page.frames().filter((x) => x.url().includes("embedded-checkout-inner")).length) > 0;
        if (!aindaAberto) {
          concluido = true;
          break;
        }
      }
      if (i % 6 === 5) saida[`aguardando_${i}`] = recorte(await page.locator("body").innerText().catch(() => ""), 200);
    }
    saida.concluido = concluido;
    saida.textoFinal = recorte(await page.locator("body").innerText().catch(() => ""), 900);
  } catch (e) {
    saida.erro = String(e.message || e).slice(0, 300);
  }

  fs.writeFileSync(path.join(SAIDA, "pagar.json"), JSON.stringify(saida, null, 2), "utf8");
  await page.screenshot({ path: path.join(SAIDA, "pagar.png") }).catch(() => {});
  console.log(JSON.stringify(saida, null, 2).slice(0, 3500));
  await ctx.close();
})();

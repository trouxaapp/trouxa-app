// Resolve o Playwright e o perfil do browser sem depender do usuário da
// máquina (o hook do repo reclama de caminho absoluto de usuário).
//
// Ordem de busca: PW_PATH > módulo global do npm > playwright instalado aqui.
// Perfil: o que estiver em config.json (com ~ expandido) ou ~/.trouxa-x.

const os = require("os");
const path = require("path");

function moduloPlaywright() {
  const candidatos = [
    process.env.PW_PATH,
    path.join(os.homedir(), "AppData/Roaming/npm/node_modules/@playwright/mcp/node_modules/playwright"),
    path.join(os.homedir(), ".npm-global/lib/node_modules/playwright"),
    "playwright",
  ].filter(Boolean);
  for (const c of candidatos) {
    try {
      return require(c);
    } catch {
      /* tenta o próximo */
    }
  }
  throw new Error("Playwright não encontrado; defina PW_PATH com o caminho do módulo");
}

const { chromium } = moduloPlaywright();

const perfil = (cfg = {}) => {
  const p = String(cfg.profile || "").replace(/^~/, os.homedir());
  return p || path.join(os.homedir(), ".trouxa-x");
};

/** Abre o Chrome com perfil persistente; cai para o chromium do Playwright. */
async function abrir(cfg = {}) {
  const opcoes = {
    headless: process.env.HEADLESS === "1",
    viewport: { width: 1360, height: 950 },
    locale: "pt-BR",
    args: ["--disable-blink-features=AutomationControlled"],
  };
  try {
    return await chromium.launchPersistentContext(perfil(cfg), { channel: "chrome", ...opcoes });
  } catch {
    return await chromium.launchPersistentContext(perfil(cfg), opcoes);
  }
}

module.exports = { chromium, abrir, perfil };

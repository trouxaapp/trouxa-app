// Fundo do cartaz por IA, sem censura de prompt.
//
// Dois provedores, escolhidos por env IMAGE_PROVIDER:
//   pollinations (padrão) — grátis, sem chave, modelo Flux
//   venice                — api.venice.ai, precisa de VENICE_API_KEY
//   nenhum                — só o mar procedural (funciona offline)
//
// Prompt pedido é curto e sem texto: texto na imagem atrapalha porque a
// fonte do provedor erra acento.

const PROMPT_PADRAO =
  "flat illustration of a sea of brown mud waves, cartoon stylized, " +
  "thick bold shapes, dark background, no text, no letters, poster art";

const SEMENTE = () => Math.floor(Math.random() * 1_000_000);
const espera = (ms) => new Promise((r) => setTimeout(r, ms));

async function pollinations(prompt, { width, height }) {
  // O tier anônimo do pollinations às vezes responde 402/429 (fila cheia).
  // Com POLLINATIONS_TOKEN a fila é sua; sem ele, três tentativas espaçadas.
  const token = process.env.POLLINATIONS_TOKEN || "";
  const url =
    `https://image.pollinations.ai/prompt/${encodeURIComponent(prompt)}` +
    `?width=${width}&height=${height}&nologo=true&model=flux&seed=${SEMENTE()}` +
    (token ? `&token=${encodeURIComponent(token)}` : "");

  let ultimo = "";
  for (let i = 0; i < 3; i++) {
    const resp = await fetch(url, { redirect: "follow" });
    if (resp.ok) return Buffer.from(await resp.arrayBuffer());
    ultimo = `HTTP ${resp.status}`;
    const retentavel = resp.status === 402 || resp.status === 429 || resp.status >= 500;
    if (!retentavel) break;
    await espera(1500 * (i + 1));
  }
  throw new Error(`pollinations ${ultimo}`);
}

async function venice(prompt, { width, height, apiKey }) {
  if (!apiKey) throw new Error("VENICE_API_KEY não definida");
  const resp = await fetch("https://api.venice.ai/api/v1/image/generate", {
    method: "POST",
    headers: {
      "content-type": "application/json",
      authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      model: "flux-dev",
      prompt,
      width,
      height,
      format: "png",
      hide_watermark: true,
      safe_mode: false, // sem censura de prompt
    }),
  });
  if (!resp.ok) throw new Error(`venice HTTP ${resp.status}: ${await resp.text()}`);
  const json = await resp.json();
  const b64 = json?.images?.[0];
  if (!b64) throw new Error("venice respondeu sem imagem");
  return Buffer.from(b64, "base64");
}

/**
 * Devolve um Buffer da arte de fundo, ou null quando o provedor é "nenhum"
 * ou quando tudo falha (o cartaz então usa as ondas procedurais).
 */
export async function fundoIA({
  prompt = "",
  provider = process.env.IMAGE_PROVIDER || "pollinations",
  width = 1080,
  height = 1350,
  apiKey = process.env.VENICE_API_KEY || "",
  silencioso = false,
} = {}) {
  const texto = (prompt || PROMPT_PADRAO).trim();
  const tentativas =
    provider === "nenhum" ? [] : provider === "venice" ? [venice, pollinations] : [pollinations, venice];
  for (const tentar of tentativas) {
    try {
      return await tentar(texto, { width, height, apiKey });
    } catch (e) {
      if (!silencioso) console.error(`fundo IA falhou (${tentar.name}): ${e.message}`);
    }
  }
  return null;
}

export { PROMPT_PADRAO };

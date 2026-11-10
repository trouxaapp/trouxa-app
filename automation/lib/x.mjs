// Cliente mínimo do X (Twitter) com OAuth 1.0a, sem SDK.
//
// Por que OAuth 1.0a e não Bearer: o endpoint de upload de mídia (v1.1)
// só aceita OAuth 1.0a de usuário. Com ele o app publica como @trouxaapp.
//
// Só node:crypto, pra rodar no runner do GitHub sem instalar nada além do sharp.

import crypto from "node:crypto";

const pct = (s) =>
  encodeURIComponent(String(s)).replace(/[!*'()]/g, (c) => "%" + c.charCodeAt(0).toString(16).toUpperCase());

const credenciais = (env = process.env) => {
  const c = {
    key: env.X_API_KEY,
    secret: env.X_API_SECRET,
    token: env.X_ACCESS_TOKEN,
    tokenSecret: env.X_ACCESS_SECRET,
  };
  const faltando = Object.entries(c).filter(([, v]) => !v).map(([k]) => k);
  if (faltando.length) throw new Error(`faltam credenciais: ${faltando.join(", ")}`);
  return c;
};

/** Base string do OAuth 1.0a: método + url + TODOS os parâmetros assinados. */
function baseString(method, url, params) {
  const normalizados = Object.keys(params)
    .sort()
    .map((k) => `${pct(k)}=${pct(params[k])}`)
    .join("&");
  return `${method.toUpperCase()}&${pct(url)}&${pct(normalizados)}`;
}

/**
 * Cabeçalho Authorization.
 *
 * A query string da URL entra na assinatura junto com os parâmetros extras: a
 * base string usa só origin+path e TODOS os parâmetros (query inclusa) em
 * ordem alfabética. Sem isso, um GET com query assina errado e volta 401.
 *
 * @param params parâmetros extras que também entram na assinatura.
 *               Corpo JSON não entra; corpo form-encoded entra.
 */
export function cabecalhoOAuth({ method, url, params = {}, creds }) {
  const alvo = new URL(url);
  const daQuery = {};
  alvo.searchParams.forEach((valor, chave) => {
    daQuery[chave] = valor;
  });
  const semQuery = `${alvo.origin}${alvo.pathname}`;

  const oauth = {
    oauth_consumer_key: creds.key,
    oauth_nonce: crypto.randomBytes(16).toString("hex"),
    oauth_signature_method: "HMAC-SHA1",
    oauth_timestamp: Math.floor(Date.now() / 1000).toString(),
    oauth_token: creds.token,
    oauth_version: "1.0",
  };
  const assinados = { ...daQuery, ...params, ...oauth };
  const chave = `${pct(creds.secret)}&${pct(creds.tokenSecret)}`;
  oauth.oauth_signature = crypto
    .createHmac("sha1", chave)
    .update(baseString(method, semQuery, assinados))
    .digest("base64");

  return (
    "OAuth " +
    Object.keys(oauth)
      .sort()
      .map((k) => `${pct(k)}="${pct(oauth[k])}"`)
      .join(", ")
  );
}

const espera = (ms) => new Promise((r) => setTimeout(r, ms));

/**
 * Repete em 429 (fila) e 5xx (erro do lado deles). Publicar post não é
 * idempotente, então um 5xx pode duplicar; 429 não cria nada e é o caso comum
 * de cron.
 */
async function comRetentativa(acao, { tentativas = 3, esperaMs = 20000 } = {}) {
  let ultimo;
  for (let i = 1; i <= tentativas; i++) {
    try {
      return await acao();
    } catch (e) {
      ultimo = e;
      const status = Number((String(e.message).match(/\((\d{3})\)/) || [])[1] || 0);
      if ((status !== 429 && status < 500) || i === tentativas) break;
      console.log(`X respondeu ${status}; nova tentativa em ${(esperaMs * i) / 1000}s`);
      await espera(esperaMs * i);
    }
  }
  throw ultimo;
}

/** Sobe um PNG e devolve o media_id_string. */
export async function subirMidia(png, { creds = credenciais() } = {}) {
  const url = "https://upload.twitter.com/1.1/media/upload.json";
  const params = { media_category: "tweet_image" };
  const limite = "----trouxa" + crypto.randomBytes(12).toString("hex");
  const body = Buffer.concat([
    Buffer.from(`--${limite}\r\nContent-Disposition: form-data; name="media"; filename="cartaz.png"\r\nContent-Type: image/png\r\n\r\n`),
    png,
    Buffer.from(`\r\n--${limite}--\r\n`),
  ]);

  return comRetentativa(async () => {
    const resp = await fetch(`${url}?media_category=tweet_image`, {
      method: "POST",
      headers: {
        authorization: cabecalhoOAuth({ method: "POST", url, params, creds }),
        "content-type": `multipart/form-data; boundary=${limite}`,
        "content-length": String(body.length),
      },
      body,
    });
    const json = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(`upload de mídia falhou (${resp.status}): ${JSON.stringify(json)}`);
    const id = json.media_id_string || json.media_id;
    if (!id) throw new Error(`upload sem media_id: ${JSON.stringify(json)}`);
    return String(id);
  });
}

/** Publica o tweet. Texto já vem apertado em 280. */
export async function tuitar(texto, mediaIds = [], { creds = credenciais() } = {}) {
  const url = "https://api.x.com/2/tweets";
  const payload = { text: texto };
  if (mediaIds.length) payload.media = { media_ids: mediaIds.map(String) };

  return comRetentativa(async () => {
    const resp = await fetch(url, {
      method: "POST",
      headers: {
        authorization: cabecalhoOAuth({ method: "POST", url, creds }),
        "content-type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    const json = await resp.json().catch(() => ({}));
    if (!resp.ok) throw new Error(`tweet falhou (${resp.status}): ${JSON.stringify(json)}`);
    return json?.data ?? json;
  });
}

export { credenciais, baseString };

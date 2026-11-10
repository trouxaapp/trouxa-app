// Espelho do Alma.kt do app. Mesmas regras, mesmo tom: o post não é o JSON
// cru, é o JSON trabalhado. Se o texto passar de 280, o script reclama.
//
// Regra de ouro (a mesma do app): nada inventado. O que não vem do dado vem
// de regra declarada aqui, e o que é piada está marcado como piada.

export const LIMITE = 280;

/** Frases de processo, sem número: as mesmas do Alma.kt. */
export const ESCULACHOS = [
  "Quem paga a conta não é quem assina embaixo.",
  "Urgência é o jeito educado de dizer “vota sem ler”.",
  "No papel é temporário. Na prática, ninguém revoga.",
  "Ementa é o que eles querem que você leia. O texto é o que vale.",
  "Dar nome a ponte e renovar rádio passa fácil, e sai na foto.",
];

export const esculacho = (dias) => ESCULACHOS[Math.abs(dias) % ESCULACHOS.length];

const limpar = (s) => String(s ?? "").replace(/\s+/g, " ").trim();

/**
 * Selo do post: usa o curado. Sem curadoria, deriva de fato do próprio
 * post (seta de preço, valor alto). Nunca é adjetivo solto.
 */
export function seloDe(post) {
  const curado = limpar(post?.selo);
  if (curado) return curado.toUpperCase();

  const numero = limpar(post?.numero);
  if (numero.includes("→")) return "FICOU MAIS CARO";
  const m = numero.match(/R\$\s*([\d.]+)\s*(bilh|milh|trilh)?/i);
  if (m) {
    const v = Number(m[1].replace(/\./g, ""));
    const escala = (m[2] || "").toLowerCase();
    if (escala === "trilh" || (escala === "bilh" && v >= 1)) return "DINHEIRO GRANDE";
    if (!escala && v >= 1_000_000_000) return "DINHEIRO GRANDE";
  }
  return "";
}

/**
 * Monta o texto do post: carimbo + número, a frase, o veredito (se couber) e
 * a fonte com o link.
 *
 * Ordem de sacrifício quando estoura 280: hashtag, veredito, e só então uma
 * frase do texto. O carimbo, o número, a fonte e o link nunca saem.
 */
export function montarTexto(post, { siteUrl = "", comHashtags = false } = {}) {
  const cabeca = [seloDe(post), limpar(post?.numero)].filter(Boolean).join(" ");
  const texto = limpar(post?.texto);
  const veredito = limpar(post?.veredito);
  const rodape = [limpar(post?.fonte), siteUrl].filter(Boolean).join(" · ");
  const tags = (post?.hashtags || []).join(" ");

  const juntar = (blocos) => blocos.filter(Boolean).join("\n\n");

  const comTudo = [cabeca, texto, veredito, rodape];
  let saida = juntar(comTudo);
  if (comHashtags && tags && saida.length + tags.length + 2 <= LIMITE) saida = `${saida}\n\n${tags}`;
  if (saida.length <= LIMITE) return saida;

  // 1. sem veredito
  saida = juntar([cabeca, texto, rodape]);
  if (saida.length <= LIMITE) return saida;

  // 2. corta o texto no fim de frase, preservando cabeça e rodapé
  const espaço = LIMITE - juntar([cabeca, rodape]).length - 4;
  const encurtado = cortarEmFrases(texto, espaço);
  saida = juntar([cabeca, encurtado, rodape]);
  if (saida.length > LIMITE) throw new Error(`não coube em ${LIMITE}: ${post?.id}`);
  return saida;
}

/** Corta num fim de frase; se não houver, corta na palavra. */
export function cortarEmFrases(texto, limite) {
  if (texto.length <= limite) return texto;
  const frases = texto.split(/(?<=[.!?])\s+/);
  let acumulado = "";
  for (const frase of frases) {
    if ((acumulado + " " + frase).trim().length > limite) break;
    acumulado = (acumulado + " " + frase).trim();
  }
  if (acumulado) return acumulado;
  const corte = texto.slice(0, Math.max(0, limite - 1));
  return corte.slice(0, corte.lastIndexOf(" ") > 0 ? corte.lastIndexOf(" ") : corte.length).trimEnd() + "…";
}

/** Legenda que vai no post quando ele tem imagem (o cartaz fala pelo texto). */
export function legendaDaImagem(post) {
  return limpar(post?.imagem?.legenda) || "";
}

/** Nome do arquivo do cartaz, estável e legível. */
export const nomeArquivo = (post) =>
  `${String(post?.id || "post").replace(/[^a-z0-9-_]+/gi, "-").toLowerCase()}`;

/**
 * Rodízio determinístico: três posts por dia, sem estado guardado no repo.
 * Post com "quando" igual à data escolhida fura a fila.
 */
export function escolherPost(posts, { data, slot = 0, id = "" } = {}) {
  const ativos = (posts || []).filter((p) => p && p.id);
  if (!ativos.length) return null;
  if (id) {
    const achado = ativos.find((p) => p.id === id);
    if (!achado) throw new Error(`post "${id}" não existe em feed/posts.json`);
    return achado;
  }
  const dia = Math.floor(new Date(data || Date.now()).getTime() / 86_400_000);
  const marcados = ativos.filter((p) => p.quando && p.quando.slice(0, 10) === (data || "").slice(0, 10));
  const fila = marcados.length ? marcados : ativos.filter((p) => !p.quando);
  const base = fila.length ? fila : ativos;
  const indice = ((dia * 3 + Number(slot || 0)) % base.length + base.length) % base.length;
  return base[indice];
}

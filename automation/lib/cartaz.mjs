// Cartaz do post: 1080x1350, mesma língua visual do app.
//
// A piada visual pedida: o político que fez merda aparece mergulhado num mar
// de merda. Isso é montado em três camadas, todas determinísticas:
//   1. fundo — arte de IA sem censura (imagem.mjs) OU ondas procedurais
//   2. foto real do político, estilizada (P&B + duotone vermelho + moldura)
//   3. camada de texto em SVG: carimbo torto, número gigante, frase, fonte
//
// Nada de dado novo aqui: o cartaz só desenha o que veio do post curado.

import sharp from "sharp";
import { fundoIA, PROMPT_PADRAO } from "./imagem.mjs";
import { buscarDeputado } from "./camara.mjs";
import { seloDe, nomeArquivo } from "./legendas.mjs";

const W = 1080;
const H = 1350;
const MARGEM = 72;

const FUNDO = "#0B0B0B";
const VERMELHO = "#DC2626";
const BRANCO = "#FFFFFF";
const CINZA = "#9CA3AF";
const VERDE = "#22C55E";
const MAR = ["#3A2415", "#5A3A20", "#7A5230"];

const esc = (s) =>
  String(s ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[c]);

/** Quebra em linhas por contagem de caracteres (SVG não tem wrap). */
function quebrar(texto, maxChars, maxLinhas = 4) {
  const palavras = String(texto ?? "").split(/\s+/).filter(Boolean);
  const linhas = [];
  let atual = "";
  for (const p of palavras) {
    if ((atual + " " + p).trim().length > maxChars && atual) {
      linhas.push(atual);
      atual = p;
      if (linhas.length === maxLinhas - 1 && palavras.length) break;
    } else {
      atual = (atual + " " + p).trim();
    }
  }
  if (atual && linhas.length < maxLinhas) linhas.push(atual);
  return linhas;
}

function texto(linhas, { x, y, tamanho, cor, peso = "700", familia = "DejaVu Sans, Arial, sans-serif", espaco = 0 }) {
  return linhas
    .map(
      (l, i) =>
        `<text x="${x}" y="${y + i * Math.round(tamanho * 1.16)}" font-family="${familia}" ` +
        `font-size="${tamanho}" font-weight="${peso}" fill="${cor}" letter-spacing="${espaco}">${esc(l)}</text>`
    )
    .join("");
}

/** Mar de merda procedural: ondas empilhadas. Funciona sem internet. */
function marDeMerda() {
  const base = H * 0.52;
  const ondas = [
    { topo: base, amp: 40, cor: MAR[0], op: 0.55 },
    { topo: base + 90, amp: 55, cor: MAR[1], op: 0.75 },
    { topo: base + 210, amp: 70, cor: MAR[2], op: 0.95 },
  ];
  const paths = ondas
    .map(
      (o, i) => `<path d="${
        Array.from({ length: 13 }, (_, k) => {
          const x = (k / 12) * W;
          const y = o.topo + (k % 2 === 0 ? -o.amp : o.amp);
          return `${k === 0 ? "M" : "L"}${x.toFixed(0)},${y.toFixed(0)}`;
        }).join(" ") + ` L${W},${H} L0,${H} Z`
      }" fill="${o.cor}" opacity="${o.op}"/>` +
        // bolhas: a assinatura da merda
        (i === 2
          ? Array.from({ length: 9 }, (_, b) => {
              const cx = 90 + b * 115;
              const cy = o.topo - 40 - (b % 3) * 42;
              const r = 9 + (b % 4) * 5;
              return `<circle cx="${cx}" cy="${cy}" r="${r}" fill="${o.cor}" opacity="0.8"/>`;
            }).join("")
          : "")
    )
    .join("");
  return paths;
}

/** Foto do político: quadrada, P&B, tingida de vermelho, com moldura. */
async function estilizarFoto(fotoBuffer, tamanho = 340) {
  const base = await sharp(fotoBuffer)
    .resize(tamanho, tamanho, { fit: "cover", position: "top" })
    .grayscale()
    .linear(1.1, -8)
    .png()
    .toBuffer();
  // Duotone: preto -> preto, branco -> vermelho. O multiply faz o trabalho.
  const tinta = await sharp({
    create: { width: tamanho, height: tamanho, channels: 4, background: "#FFD9C2" },
  })
    .png()
    .toBuffer();
  return sharp(base).composite([{ input: tinta, blend: "multiply" }]).png().toBuffer();
}

async function baixar(url) {
  const resp = await fetch(url, { redirect: "follow" });
  if (!resp.ok) throw new Error(`foto HTTP ${resp.status}`);
  return Buffer.from(await resp.arrayBuffer());
}

/**
 * Monta o PNG do cartaz.
 * @returns {Promise<{buffer: Buffer, arquivo: string}>}
 */
export async function montarCartaz(post, { provider = process.env.IMAGE_PROVIDER || "pollinations" } = {}) {
  // Quem é o político da vez: foto_url cravada manda (é o normal: resolvida
  // uma vez e congelada no feed/posts.json); senão busca pelo nome na Câmara.
  let alvo = { foto_url: post?.imagem?.foto_url || "", legenda: post?.imagem?.legenda_foto || "" };
  if (!alvo.foto_url && post?.imagem?.politico) {
    try {
      const d = await buscarDeputado(post.imagem.politico, { uf: post?.imagem?.uf || "" });
      if (d) {
        alvo = {
          foto_url: d.foto,
          legenda: alvo.legenda || `${d.nome}${d.partido ? ` (${d.partido}${d.uf ? "-" + d.uf : ""})` : ""}`,
        };
        console.log(`foto resolvida por nome: ${post.imagem.politico} -> id ${d.id} ${d.nome} ${d.partido}-${d.uf}`);
      } else {
        console.log(`foto: "${post.imagem.politico}" não achado na Câmara, cartaz sai sem foto`);
      }
    } catch (e) {
      console.log(`busca de deputado falhou: ${e.message}`);
    }
  }

  const fundo = await fundoIA({
    prompt: post?.imagem?.prompt || PROMPT_PADRAO,
    provider,
    width: W,
    height: H,
  });

  const camadas = [];

  // 1. fundo
  if (fundo) {
    camadas.push({
      input: await sharp(fundo).resize(W, H, { fit: "cover" }).modulate({ brightness: 0.62 }).png().toBuffer(),
    });
  } else {
    camadas.push({ input: { create: { width: W, height: H, channels: 4, background: FUNDO } } });
  }
  // véu escuro para o texto vencer o fundo
  camadas.push({
    input: { create: { width: W, height: H, channels: 4, background: "rgba(0,0,0,0.34)" } },
  });

  // 2. foto do político, se houver
  let fotoTopo = 0;
  const fotoTam = 340;
  if (alvo.foto_url) {
    try {
      const estilizada = await estilizarFoto(await baixar(alvo.foto_url), fotoTam);
      fotoTopo = 250;
      camadas.push({ input: estilizada, left: W - MARGEM - fotoTam, top: fotoTopo });
      console.log(`foto estilizada no cartaz: ${alvo.legenda || alvo.foto_url}`);
    } catch (e) {
      console.log(`foto falhou (${alvo.foto_url}): ${e.message} — cartaz sai sem foto`);
    }
  } else {
    console.log("cartaz sem foto de político");
  }

  // 3. texto + carimbo + ondas
  const selo = seloDe(post);
  const numero = String(post?.numero ?? "").trim();
  const frase = quebrar(post?.texto, 34, 4);
  const veredito = quebrar(post?.veredito ?? "", 40, 3);

  const yFrase = fotoTopo ? fotoTopo + fotoTam + 90 : 330;
  const svg = `<svg width="${W}" height="${H}" xmlns="http://www.w3.org/2000/svg">
    ${marDeMerda()}
    <text x="${MARGEM}" y="96" font-family="DejaVu Sans Mono, monospace" font-size="30" fill="${CINZA}" letter-spacing="4">TE FIZERAM DE</text>
    <text x="${MARGEM}" y="150" font-family="DejaVu Sans Mono, monospace" font-size="54" font-weight="900" fill="${VERMELHO}" letter-spacing="8">TROUXA</text>
    ${
      selo
        ? `<g transform="translate(${MARGEM},205) rotate(-2)">
             <rect x="0" y="0" width="${Math.round(selo.length * 22 + 44)}" height="66" rx="10" fill="none" stroke="${VERMELHO}" stroke-width="5"/>
             <text x="22" y="46" font-family="DejaVu Sans, Arial, sans-serif" font-size="34" font-weight="900" fill="${VERMELHO}" letter-spacing="3">${esc(selo)}</text>
           </g>`
        : ""
    }
    ${
      numero
        ? texto(quebrar(numero, 16, 2), {
            x: MARGEM,
            y: yFrase - 24,
            tamanho: 82,
            cor: BRANCO,
            peso: "900",
          })
        : ""
    }
    ${texto(frase, { x: MARGEM, y: yFrase + 60, tamanho: 46, cor: BRANCO, peso: "700" })}
    ${texto(veredito, { x: MARGEM, y: yFrase + 60 + frase.length * 54 + 30, tamanho: 36, cor: "#F3D5C8", peso: "400" })}
    ${
      fotoTopo
        ? `<rect x="${W - MARGEM - fotoTam}" y="${fotoTopo}" width="${fotoTam}" height="${fotoTam}" rx="16" fill="none" stroke="${VERMELHO}" stroke-width="6"/>
           <text x="${W - MARGEM - fotoTam}" y="${fotoTopo + fotoTam + 40}" font-family="DejaVu Sans, Arial, sans-serif" font-size="28" font-weight="700" fill="${BRANCO}">${esc(alvo.legenda)}</text>
           <text x="${W - MARGEM - fotoTam}" y="${fotoTopo + fotoTam + 72}" font-family="DejaVu Sans, Arial, sans-serif" font-size="22" fill="${CINZA}">foto: Câmara dos Deputados</text>`
        : ""
    }
    <rect x="${MARGEM}" y="${H - 190}" width="${W - 2 * MARGEM}" height="3" fill="#333"/>
    ${texto(quebrar(`Fonte: ${post?.fonte ?? ""}`, 62, 2), { x: MARGEM, y: H - 140, tamanho: 30, cor: VERDE, peso: "400" })}
    ${texto([post?.data_referencia ? `dado de ${post.data_referencia}` : "@trouxaapp"], {
      x: MARGEM,
      y: H - 70,
      tamanho: 28,
      cor: CINZA,
      peso: "400",
    })}
    <text x="${W - MARGEM}" y="${H - 70}" text-anchor="end" font-family="DejaVu Sans Mono, monospace" font-size="28" fill="${CINZA}">trouxa · @trouxaapp</text>
  </svg>`;

  const buffer = await sharp({
    create: { width: W, height: H, channels: 4, background: FUNDO },
  })
    .composite([...camadas, { input: Buffer.from(svg), top: 0, left: 0 }])
    .png({ compressionLevel: 9 })
    .toBuffer();

  return { buffer, arquivo: `${nomeArquivo(post)}.png` };
}

#!/usr/bin/env node
// Publica no X a verdade do dia, com cartaz montado na hora.
//
// Uso:
//   node publicar.mjs --dry-run            # monta o post de hoje, não publica
//   node publicar.mjs --dry-run --todos    # monta a fila inteira, pra revisar
//   node publicar.mjs --id=cesta-2010      # publica um post específico
//   node publicar.mjs --slot=1             # segundo horário do dia
//
// Sem --dry-run, exige X_API_KEY, X_API_SECRET, X_ACCESS_TOKEN, X_ACCESS_SECRET.
// Antes de publicar, grava tudo em automation/_out para conferência humana.

import { readFile, writeFile, mkdir } from "node:fs/promises";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { escolherPost, montarTexto, nomeArquivo, LIMITE } from "./lib/legendas.mjs";
import { montarCartaz } from "./lib/cartaz.mjs";
import { subirMidia, tuitar, credenciais } from "./lib/x.mjs";

const aqui = path.dirname(fileURLToPath(import.meta.url));
const raiz = path.resolve(aqui, "..");
const SAIDA = path.join(aqui, "_out");

const args = Object.fromEntries(
  process.argv.slice(2).map((a) => {
    const [k, v] = a.replace(/^--/, "").split("=");
    return [k, v === undefined ? true : v];
  })
);

const DRY = Boolean(args["dry-run"]);
const SITE = process.env.SITE_URL || "https://trouxaapp.github.io/trouxa-app/";
const data = typeof args.data === "string" ? args.data : new Date().toISOString().slice(0, 10);

async function carregarFila() {
  const bruto = await readFile(path.join(raiz, "feed", "posts.json"), "utf8");
  const json = JSON.parse(bruto);
  return json.posts || [];
}

async function montarUm(post) {
  const texto = montarTexto(post, { siteUrl: SITE, comHashtags: Boolean(args.hashtags) });
  if (texto.length > LIMITE) throw new Error(`texto com ${texto.length} chars: ${post.id}`);
  const cartaz = args["sem-imagem"] ? null : await montarCartaz(post, { provider: args.provider });
  return { texto, cartaz };
}

async function salvarConferencia(post, { texto, cartaz }) {
  await mkdir(SAIDA, { recursive: true });
  const base = nomeArquivo(post);
  await writeFile(path.join(SAIDA, `${base}.txt`), texto, "utf8");
  if (cartaz) await writeFile(path.join(SAIDA, cartaz.arquivo), cartaz.buffer);
  return base;
}

async function main() {
  const fila = await carregarFila();

  if (args.todos) {
    const alvos = args.id ? fila.filter((p) => p.id === args.id) : fila;
    for (const [i, post] of alvos.entries()) {
      // pausa entre posts: o provedor de imagem é grátis e não gosta de rajada
      if (i > 0) await new Promise((r) => setTimeout(r, 1500));
      const montado = await montarUm(post);
      await salvarConferencia(post, montado);
      console.log(`· ${post.id} (${montado.texto.length} chars${montado.cartaz ? ", cartaz ok" : ""})`);
    }
    console.log(`\n${alvos.length} post(s) em ${SAIDA}`);
    return;
  }

  const post = escolherPost(fila, { data, slot: args.slot || 0, id: typeof args.id === "string" ? args.id : "" });
  if (!post) throw new Error("feed/posts.json sem posts ativos");

  const montado = await montarUm(post);
  const base = await salvarConferencia(post, montado);

  console.log(`— post: ${post.id} ————————————————`);
  console.log(montado.texto);
  console.log("———————————————————————————————");
  console.log(`chars: ${montado.texto.length}/${LIMITE} · arquivos: automation/_out/${base}.*`);

  if (DRY) {
    // Só avisa se faltar credencial; o dry-run não publica, então não exige
    try {
      credenciais();
    } catch (e) {
      console.log(`dry-run: nada publicado (${e.message}).`);
      return;
    }
    console.log("dry-run: nada publicado.");
    return;
  }

  const creds = credenciais();
  const ids = montado.cartaz ? [await subirMidia(montado.cartaz.buffer, { creds })] : [];
  const publicado = await tuitar(montado.texto, ids, { creds });
  const id = publicado?.id;
  console.log(`publicado: https://x.com/trouxaapp/status/${id}`);
}

main().catch((e) => {
  console.error(`erro: ${e.message}`);
  process.exit(1);
});

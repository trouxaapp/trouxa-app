import * as P from "./painel.js";
import { termosDoGlossario } from "./lei.js";

const $ = (id) => document.getElementById(id);
const esc = (s) => String(s ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[c]);
const seta = (d) => (d > 0 ? "▲" : d < 0 ? "▼" : "=");
const quando = (ms) => new Date(ms).toLocaleString("pt-BR", { day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit" });
const dataIso = (s) => (s ? s.slice(0, 10).split("-").reverse().join("/") : "");
const link = (url, texto) => `<a href="${esc(url)}" target="_blank" rel="noopener">${esc(texto)}</a>`;
const vazio = (texto) => `<div class="card carregando">${esc(texto)}</div>`;
// Shown when a block falls back to this browser's cache
const doCache = (r) => (r?.cache ? ` · fonte fora do ar, dado guardado em ${quando(r.em)}` : "");

function indicador(i, r) {
  return `<div class="card">
    <div class="rotulo">${esc(i.nome)}</div>
    <div class="valor">${esc(i.valor)}</div>
    ${i.mudanca ? `<div class="mudanca">${seta(i.direcao)} ${esc(i.mudanca)}</div>` : ""}
    <div class="fonte">${link(i.url, i.fonte)} · dado de ${esc(i.data)}${doCache(r)}</div>
  </div>`;
}

function barras(itens, max, cor) {
  return `<div class="barras">${itens.map((i) => `
    <div class="barra"><span class="nome" title="${esc(i.nome)}">${esc(i.nome)}</span>
      <span class="trilho"><span class="cheio" style="width:${Math.max(3, Math.abs(i.variacao) / max * 100)}%;background:${cor}"></span></span>
      <span class="num">${i.variacao > 0 ? "+" : ""}${P.pct(i.variacao)}</span></div>`).join("")}</div>`;
}

function explicacaoHtml(siglaTipo, ementa, completo) {
  const e = P.explicar(siglaTipo, ementa);
  let h = "";
  if (e.valor) h += `<div class="dinheiro">${esc(e.valor)}</div>`;
  if (e.emUmaFrase) h += `<p class="frase">${esc(e.emUmaFrase)}</p>`;
  if (!completo) return h;
  if (e.objetivo) h += `<p class="objetivo">Objetivo escrito no projeto: ${esc(e.objetivo)}</p>`;
  if (e.observacoes.length) h += `<ul class="obs">${e.observacoes.map((o) => `<li>${esc(o)}</li>`).join("")}</ul>`;
  if (e.termos.length) h += `<div class="termos">${e.termos.map((t) => `<a href="#palavras" title="${esc(t.explicacao)}">${esc(t.termo)}</a>`).join("")}</div>`;
  return h;
}

// ---------- blocks ----------

// A verdade do dia: curada no repo (feed/feed.json), com fonte obrigatória.
// O site lê o mesmo arquivo que o app, então os dois nunca divergem.
const FEED_URL = "https://raw.githubusercontent.com/trouxaapp/trouxa-app/main/feed/feed.json";

// Piadas de processo, sem número: as mesmas do Alma.kt do app.
const ESCULACHOS = [
  "Quem paga a conta não é quem assina embaixo.",
  "Urgência é o jeito educado de dizer “vota sem ler”.",
  "No papel é temporário. Na prática, ninguém revoga.",
  "Ementa é o que eles querem que você leia. O texto é o que vale.",
  "Dar nome a ponte e renovar rádio passa fácil, e sai na foto.",
  "Ninguém lê 40 páginas. Nem eles.",
];

async function verdade() {
  const alvo = $("verdade-do-dia");
  let feed;
  try {
    const resp = await fetch(FEED_URL, { cache: "no-cache" });
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    feed = await resp.json();
  } catch {
    alvo.className = "hero carregando";
    alvo.textContent = "A verdade do dia não carregou. Ela vive em feed/feed.json no repositório.";
    return;
  }

  const itens = (feed?.itens || []).filter((i) => i.ativo !== false && i.tipo === "destaque");
  if (!itens.length) {
    alvo.className = "hero carregando";
    alvo.textContent = "Sem verdade curada agora.";
    return;
  }

  // Rodízio determinístico por dia: muda todo dia, estável dentro do dia
  const dia = Math.floor(Date.now() / 86_400_000);
  const item = itens[dia % itens.length];

  alvo.className = "hero";
  alvo.innerHTML = `
    <div class="rotulo">A verdade de hoje</div>
    ${item.selo ? `<div class="carimbo">${esc(item.selo)}</div>` : ""}
    ${item.titulo ? `<p class="titulo">${esc(item.titulo)}</p>` : ""}
    ${item.numero ? `<p class="numero">${esc(item.numero)}</p>` : ""}
    ${item.veredito ? `<p class="veredito">${esc(item.veredito)}</p>` : ""}
    <p class="texto">${esc(item.texto)}</p>
    <div class="fonte">${link(item.fonte_url, item.fonte)} · dado de ${esc(dataIso(item.data_referencia))}</div>
    <p class="esculacho">“${esc(ESCULACHOS[dia % ESCULACHOS.length])}”</p>`;
}


async function mercado() {
  const [d, b] = await Promise.all([P.dolar(), P.ibovespa()]);
  const cards = [d && indicador(d.v, d), b && indicador(b.v, b)].filter(Boolean);
  $("mercado").innerHTML = cards.join("") || vazio("Banco Central e B3 não responderam agora. Tenta de novo em alguns minutos.");
}

async function parados() {
  const r = await P.parados();
  $("parados").innerHTML = r ? r.v.map((i) => indicador(i, r)).join("") : vazio("Banco Central não respondeu agora.");
}

async function precos() {
  const r = await P.precos();
  if (!r) { $("precos").innerHTML = vazio("IBGE não respondeu agora."); return; }
  const p = r.v;
  const max = Math.max(...p.altas.map((i) => i.variacao), ...p.quedas.map((i) => -i.variacao));
  $("precos").innerHTML = `<div class="card">
    <div class="rotulo">Inflação do mês (IPCA), ${esc(p.mes)}</div>
    <div class="valor">${P.pct(p.geral)}</div>
    <h3>Os que mais subiram</h3>${barras(p.altas, max, "var(--vermelho)")}
    <h3>Os que mais caíram</h3>${barras(p.quedas, max, "var(--texto-3)")}
    <p class="nota">O IBGE mede quanto cada preço mudou, mas não diz por quê. Entressafra, clima, câmbio e imposto são causas comuns; a explicação de cada item fica com quem estuda aquele mercado.</p>
    <div class="fonte">${link(P.FONTES.sidra, "IBGE, IPCA por subitem (tabela 7060)")}${doCache(r)}</div>
  </div>`;
}

async function camaraHoje() {
  const r = await P.camaraHoje();
  if (!r) { $("camara-hoje").innerHTML = vazio("Câmara não respondeu agora."); return; }
  const c = r.v;
  $("camara-hoje").innerHTML = `<div class="card">
    <div class="rotulo">Câmara hoje</div>
    <p class="frase">Plenário: ${esc(c.plenario)}</p>
    <p class="ementa">${c.reunioes} reuniões de comissão marcadas${c.canceladas ? `, ${c.canceladas} cancelada${c.canceladas > 1 ? "s" : ""}` : ""}.</p>
    <div class="fonte">${link("https://www.camara.leg.br/agenda", "Câmara dos Deputados, agenda")}${doCache(r)}</div>
  </div>`;
}

async function votacoes() {
  const r = await P.votacoes();
  if (!r) { $("votacoes").innerHTML = vazio("Câmara não respondeu agora."); return; }
  $("votacoes").innerHTML = r.v.map((v) => {
    const tag = v.aprovada === true ? `<span class="tag ok">APROVADO</span>` : v.aprovada === false ? `<span class="tag nao">REJEITADO</span>` : `<span class="tag neutra">SEM RESULTADO</span>`;
    return `<div class="card">
      <div>${tag}<span class="data">${dataIso(v.data)}</span></div>
      ${v.proposicao ? `<div class="sigla" style="margin-top:8px">${esc(v.proposicao)}</div>${explicacaoHtml(v.siglaTipo, v.ementa, false) || `<p class="frase longa" title="${esc(v.ementa)}">${esc(v.ementa)}</p>`}` : ""}
      <p class="ementa">${esc(v.descricao)}</p>
      ${v.placar ? `<div class="placar">${esc(v.placar)}</div>` : ""}
      ${v.orientacoes.length ? `<details><summary>Como cada partido orientou</summary><div class="orientacoes">${v.orientacoes.map((o) => `<span>${esc(o)}</span>`).join("")}</div></details>` : ""}
      ${v.url ? `<div class="fonte">${link(v.url, "Abrir a sessão na Câmara")}</div>` : ""}
    </div>`;
  }).join("") || vazio("Nenhuma votação de projeto no Plenário nos últimos 30 dias.");
}

async function senado() {
  const r = await P.senado();
  if (!r) { $("senado").innerHTML = vazio("Sem resultado do Plenário do Senado nos últimos 10 dias."); return; }
  const s = r.v;
  $("senado").innerHTML = `<div class="card">
    <div class="rotulo">${esc(s.sessao)}, ${esc(s.data)}</div>
    ${s.itens.map((i) => `<div style="margin-top:12px">
      <div class="sigla">${esc(i.identificacao)}</div>
      <p class="ementa" style="color:var(--texto)">${esc(i.ementa)}</p>
      <div class="mudanca">${esc(i.resultado)}</div></div>`).join("")}
    <div class="fonte">${link(P.FONTES.senado, "Senado Federal, dados abertos do Plenário")}${doCache(r)}</div>
  </div>`;
}

let leis = [];
let filtro = "Todos";

function desenharLeis() {
  const tipos = ["Todos", ...new Set(leis.map((l) => l.siglaTipo))];
  $("filtros").innerHTML = tipos.map((t) => `<button type="button" aria-pressed="${t === filtro}" data-t="${esc(t)}">${esc(t)}${t === "Todos" ? ` (${leis.length})` : ""}</button>`).join("");
  const lista = leis.filter((l) => filtro === "Todos" || l.siglaTipo === filtro).slice(0, 40);
  $("lista-leis").innerHTML = lista.map((l) => `<div class="card">
    <div class="sigla">${esc(l.sigla)}</div>
    ${explicacaoHtml(l.siglaTipo, l.ementa, true)}
    <details><summary>Ementa oficial</summary><p class="ementa">${esc(l.ementa)}</p></details>
    <div class="fonte">${link(`https://www.camara.leg.br/proposicoesWeb/fichadetramitacao?idProposicao=${l.id}`, "Ver na Câmara")}</div>
  </div>`).join("") || vazio("Nenhum projeto apresentado nos últimos 7 dias.");
}

$("filtros").addEventListener("click", (ev) => {
  const b = ev.target.closest("button");
  if (!b) return;
  filtro = b.dataset.t;
  desenharLeis();
});

async function leisDaSemana() {
  const r = await P.leisDaSemana();
  if (!r) { $("lista-leis").innerHTML = vazio("Câmara não respondeu agora."); return; }
  leis = r.v;
  desenharLeis();
}

async function creditos() {
  const r = await P.creditos();
  if (!r) { $("creditos").innerHTML = vazio("Câmara não respondeu agora."); return; }
  const anos = r.v;
  const max = Math.max(...anos.map((a) => a.total));
  $("creditos").innerHTML = `<div class="card">
    <p class="frase">Quanto foi liberado por crédito extraordinário, ano a ano</p>
    <p class="ementa">Crédito extraordinário é dinheiro liberado por medida provisória fora do Orçamento já aprovado. Começa a ser gasto antes de o Congresso votar. A Constituição só permite para despesa imprevisível e urgente (art. 167, § 3º).</p>
    <div class="barras">${anos.map((a) => `
      <div class="barra"><span class="nome">${a.ano} · ${esc(a.presidencia)}</span>
        <span class="trilho"><span class="cheio" style="width:${Math.max(1, a.total / max * 100)}%;background:var(--vermelho)"></span></span>
        <span class="num">${esc(P.humanizarReais(a.total).replace("R$ ", ""))}</span></div>`).join("")}</div>
    <p class="nota">Valores nominais, sem correção pela inflação. Soma dos valores escritos nas ementas das MPs de cada ano (${anos.reduce((s, a) => s + a.quantidade, 0)} MPs). 2020 inclui as MPs da pandemia.</p>
    <div class="fonte">${link(P.FONTES.camara, "Câmara dos Deputados, dados abertos de proposições (MPV)")}${doCache(r)}</div>
  </div>`;
}

async function eleicao() {
  const r = await P.eleicao();
  if (!r) { $("resultado").innerHTML = vazio("TSE não respondeu agora."); return; }
  const e = r.v;
  $("resultado").innerHTML = `<div class="card">
    <div class="rotulo">${esc(e.titulo)}</div>
    ${e.candidatos.map((c) => `<div class="candidato">
      <div><div class="frase">${esc(c.nome)} <span class="sigla">${esc(c.partido)}</span>${c.eleito ? ` <span class="tag ok">ELEITO</span>` : ""}</div>
      <div class="data">${c.votos.toLocaleString("pt-BR")} votos</div></div>
      <div class="pc">${esc(c.percentual)}%</div></div>`).join("")}
    <div class="fonte">${link(P.FONTES.tse, "TSE, resultado oficial")} · totalizado em ${esc(e.totalizacao)}${doCache(r)}</div>
  </div>`;
}

async function noticias() {
  const r = await P.noticias();
  if (!r) { $("lista-noticias").innerHTML = vazio("As agências não responderam agora."); return; }
  $("lista-noticias").innerHTML = r.v.map((n) => `<a class="noticia" href="${esc(n.link)}" target="_blank" rel="noopener">
    <div>${esc(n.titulo)}</div><div class="meta">${esc(n.fonte)}${n.em ? ` · ${quando(n.em)}` : ""}</div></a>`).join("");
}

function glossario() {
  $("glossario").innerHTML = termosDoGlossario().map((t) => `<div class="card termo">
    <h4>${esc(t.termo)}</h4><p>${esc(t.explicacao)}</p>
    <div class="fonte">${link(t.fonteUrl, t.fonteNome)}</div></div>`).join("");
}

glossario();
// Independent blocks: one slow source never holds the others
[verdade, mercado, parados, precos, camaraHoje, votacoes, senado, leisDaSemana, creditos, eleicao, noticias]
  .forEach((f) => f().catch(() => {}));

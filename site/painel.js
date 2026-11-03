// The daily panel, read in the visitor's browser straight from the official
// sources (all of them send CORS headers). Same rules as the app's
// PainelRepository: each block keeps the last good answer, with its own
// reference date, and nothing is computed beyond sums and differences.
import { explicar, humanizarReais } from "./lei.js";

const CAMARA = "https://dadosabertos.camara.leg.br/api/v2";
const SGS = "https://api.bcb.gov.br/dados/serie/bcdata.sgs";
export const FONTES = {
  sgs: "https://www3.bcb.gov.br/sgspub/",
  b3: "https://www.b3.com.br/pt_br/market-data-e-indices/indices/indices-amplos/ibovespa.htm",
  sidra: "https://sidra.ibge.gov.br/tabela/7060",
  tse: "https://resultados.tse.jus.br/oficial/app/index.html",
  camara: "https://dadosabertos.camara.leg.br/",
  senado: "https://www25.senado.leg.br/web/atividade/plenario",
};
const FEEDS = [
  ["https://www.camara.leg.br/noticias/rss/ultimas-noticias", "Agência Câmara"],
  ["https://www.camara.leg.br/noticias/rss/dinamico/ELEICOES", "Agência Câmara"],
  ["https://agenciabrasil.ebc.com.br/rss/politica/feed.xml", "Agência Brasil"],
  ["https://agenciabrasil.ebc.com.br/rss/economia/feed.xml", "Agência Brasil"],
];

// ---------- small helpers ----------

const pad = (n) => String(n).padStart(2, "0");
const iso = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
const br = (d) => `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
const diasAtras = (n) => new Date(Date.now() - n * 864e5);
const num = (v, casas) => v.toLocaleString("pt-BR", { minimumFractionDigits: casas, maximumFractionDigits: casas });
export const pct = (v) => num(v, 2).replace(/,00$/, "") + "%";
const reais = (v) => "R$ " + num(v, 2);

// One retry on server or network errors; a 4xx is an answer
async function buscar(url, tipo = "json") {
  for (let tentativa = 0; tentativa < 2; tentativa++) {
    try {
      const r = await fetch(url, { headers: { Accept: "application/json, application/xml, */*" } });
      if (r.ok) return tipo === "json" ? await r.json() : await r.text();
      if (r.status < 500) return null;
    } catch (_) { /* network: retry once */ }
    if (tentativa === 0) await new Promise((ok) => setTimeout(ok, 1500));
  }
  return null;
}

const guardado = (k) => { try { return JSON.parse(localStorage.getItem("trouxa." + k)); } catch (_) { return null; } };
const guardar = (k, v) => { try { localStorage.setItem("trouxa." + k, JSON.stringify({ v, em: Date.now() })); } catch (_) { /* private mode */ } };

// Runs a block; on failure falls back to the last good value from this browser
async function bloco(nome, fn, validadeMs = 0) {
  const antes = guardado(nome);
  if (validadeMs && antes && Date.now() - antes.em < validadeMs) return { ...antes, cache: true };
  let v = null;
  try { v = await fn(); } catch (_) { v = null; }
  const vazio = v == null || (Array.isArray(v) && v.length === 0);
  if (!vazio) { guardar(nome, v); return { v, em: Date.now(), cache: false }; }
  return antes ? { ...antes, cache: true } : null;
}

// ---------- BCB SGS ----------

async function sgs(serie, dias) {
  const url = `${SGS}.${serie}/dados?formato=json&dataInicial=${br(diasAtras(dias))}&dataFinal=${br(new Date())}`;
  const rows = await buscar(url);
  if (!Array.isArray(rows)) return null;
  const agora = Date.now();
  return rows.map((r) => {
    const [d, m, a] = r.data.split("/").map(Number);
    return { data: r.data, valor: Number(String(r.valor).replace(",", ".")), ts: new Date(a, m - 1, d).getTime() };
  }).filter((p) => Number.isFinite(p.valor) && p.ts <= agora).sort((a, b) => a.ts - b.ts);
}

function variacaoDiaria(nome, pontos, formatar, serie) {
  const ultimo = pontos.at(-1);
  if (!ultimo) return null;
  const anterior = pontos.at(-2);
  let mudanca = "", direcao = 0;
  if (anterior) {
    const p = (ultimo.valor - anterior.valor) / anterior.valor * 100;
    const quando = anterior.data.slice(0, 5);
    if (Math.abs(p) < 0.005) mudanca = `Igual a ${quando}`;
    else if (p > 0) { mudanca = `Subiu ${pct(p)} desde ${quando}`; direcao = 1; }
    else { mudanca = `Caiu ${pct(-p)} desde ${quando}`; direcao = -1; }
  }
  return { nome, valor: formatar(ultimo.valor), mudanca, direcao, data: ultimo.data, fonte: `Banco Central (SGS ${serie})`, url: FONTES.sgs };
}

function desdeQuando(nome, pontos, formatar, serie, mensal) {
  const ultimo = pontos.at(-1);
  if (!ultimo) return null;
  const fonte = `Banco Central (SGS ${serie})`;
  let i = -1;
  for (let k = pontos.length - 1; k >= 0; k--) if (Math.abs(pontos[k].valor - ultimo.valor) > 1e-9) { i = k; break; }
  if (i < 0) return { nome, valor: formatar(ultimo.valor), mudanca: "Sem mudança no período consultado", direcao: 0, data: ultimo.data, fonte, url: FONTES.sgs };
  const antes = pontos[i], desde = pontos[i + 1];
  const direcao = ultimo.valor > antes.valor ? 1 : -1;
  const mudanca = mensal
    ? `${direcao > 0 ? "Subiu" : "Caiu"}: era ${formatar(antes.valor)} no mês anterior`
    : `${direcao > 0 ? "Último aumento" : "Último corte"} em ${desde.data} (era ${formatar(antes.valor)}). Sem mudança desde então.`;
  return { nome, valor: formatar(ultimo.valor), mudanca, direcao: mensal ? direcao : 0, data: ultimo.data, fonte, url: FONTES.sgs };
}

// ---------- blocks ----------

export const dolar = () => bloco("dolar", async () => {
  const p = await sgs(1, 15);
  return p && variacaoDiaria("Dólar (PTAX venda)", p, reais, 1);
});

export const ibovespa = () => bloco("ibov", async () => {
  const j = await buscar("https://cotacao.b3.com.br/mds/api/v1/InstrumentQuotation/IBOV");
  const q = j?.Trad?.[0]?.scty?.SctyQtn;
  if (!q) return null;
  const flut = Number(q.prcFlcn);
  const direcao = Math.abs(flut) < 0.005 ? 0 : flut > 0 ? 1 : -1;
  const [d, h] = String(j.Msg?.dtTm || "").split(" ");
  const data = d ? `${d.split("-").reverse().join("/")} ${(h || "").slice(0, 5)}`.trim() : "";
  return {
    nome: "Ibovespa (bolsa)",
    valor: num(Number(q.curPrc), 0) + " pontos",
    mudanca: direcao === 0 ? "Estável em relação ao fechamento anterior" : `${direcao > 0 ? "Subiu" : "Caiu"} ${pct(Math.abs(flut))} no dia`,
    direcao, data, fonte: "B3", url: FONTES.b3,
  };
});

export const parados = () => bloco("parados", async () => {
  const [selic, ipca, minimo] = await Promise.all([sgs(432, 700), sgs(13522, 200), sgs(1619, 700)]);
  return [
    selic && desdeQuando("Selic (meta do Copom)", selic, (v) => num(v, 2) + "% ao ano", 432, false),
    ipca && desdeQuando("Inflação em 12 meses (IPCA)", ipca, pct, 13522, true),
    minimo && desdeQuando("Salário mínimo", minimo, reais, 1619, false),
  ].filter(Boolean);
});

export const precos = () => bloco("precos", async () => {
  const j = await buscar("https://apisidra.ibge.gov.br/values/t/7060/n1/all/v/63/p/last%201/c315/all");
  if (!Array.isArray(j)) return null;
  const linhas = j.slice(1);
  const geral = linhas.find((o) => o.D4C === "7169");
  if (!geral) return null;
  const itens = linhas.map((o) => {
    const m = /^(\d{7})\.(.+)$/.exec(o.D4N || "");
    const v = Number(o.V);
    return m && Number.isFinite(v) ? { nome: m[2].trim(), variacao: v } : null;
  }).filter(Boolean);
  if (!itens.length) return null;
  return {
    mes: geral.D3N,
    geral: Number(geral.V),
    altas: itens.filter((i) => i.variacao > 0).sort((a, b) => b.variacao - a.variacao).slice(0, 8),
    quedas: itens.filter((i) => i.variacao < 0).sort((a, b) => a.variacao - b.variacao).slice(0, 5),
  };
});

export const camaraHoje = () => bloco("camaraHoje", async () => {
  const hoje = iso(new Date());
  const j = await buscar(`${CAMARA}/eventos?dataInicio=${hoje}&dataFim=${hoje}&itens=100`);
  const ev = j?.dados;
  if (!Array.isArray(ev)) return null;
  const plen = ev.filter((e) => (e.orgaos || []).some((o) => o.sigla === "PLEN"))
    .sort((a, b) => (a.dataHoraInicio < b.dataHoraInicio ? 1 : -1))[0];
  const comissoes = ev.filter((e) => e !== plen);
  return {
    plenario: plen ? `${plen.descricaoTipo}, ${String(plen.situacao).toLowerCase()}` : "Sem sessão no Plenário marcada para hoje",
    reunioes: comissoes.filter((e) => !String(e.situacao).startsWith("Cancelad")).length,
    canceladas: comissoes.filter((e) => String(e.situacao).startsWith("Cancelad")).length,
  };
}, 0);

const PLACAR = /Sim:\s*(\d+);\s*N[ãa]o:\s*(\d+)(?:;\s*Absten[çc][ãa]o:\s*(\d+))?/;
const CITA_PROPOSICAO = /(Projeto de (Lei|Decreto Legislativo|Resolu[çc][ãa]o)|Medida Provis[óo]ria|Proposta de Emenda)[^.;]*?n[º°o.]/i;

export const votacoes = () => bloco("votacoes", async () => {
  const j = await buscar(`${CAMARA}/votacoes?dataInicio=${iso(diasAtras(30))}&dataFim=${iso(new Date())}&idOrgao=180&ordem=DESC&ordenarPor=dataHoraRegistro&itens=100`);
  if (!Array.isArray(j?.dados)) return null;
  const brutas = j.dados.map((o) => {
    const desc = String(o.descricao || "").trim();
    if (!desc || /Redação Final/i.test(desc)) return null;
    const m = PLACAR.exec(desc);
    if (!m && !CITA_PROPOSICAO.test(desc)) return null;
    const idEvento = String(o.uriEvento || "").split("/").pop();
    return {
      id: o.id, data: o.data, descricao: desc.split(" Sim:")[0].trim(),
      aprovada: o.aprovacao === 1 ? true : o.aprovacao === 0 ? false : null,
      placar: m ? `Sim ${m[1]} x Não ${m[2]}${m[3] ? `, ${m[3]} abstenções` : ""}` : "",
      nominal: !!m,
      url: idEvento ? `https://www.camara.leg.br/evento-legislativo/${idEvento}` : "",
    };
  }).filter(Boolean).slice(0, 8);
  return Promise.all(brutas.map(async (v) => {
    const idProp = String(v.id).split("-")[0];
    const [prop, ori] = await Promise.all([
      /^\d+$/.test(idProp) ? buscar(`${CAMARA}/proposicoes/${idProp}`) : null,
      v.nominal ? buscar(`${CAMARA}/votacoes/${v.id}/orientacoes`) : null,
    ]);
    const d = prop?.dados;
    return {
      ...v,
      proposicao: d ? `${d.siglaTipo} ${d.numero}/${d.ano}` : "",
      siglaTipo: d?.siglaTipo || "",
      ementa: d?.ementa?.trim() || "",
      orientacoes: (ori?.dados || []).filter((o) => o.siglaPartidoBloco && o.orientacaoVoto).map((o) => `${o.siglaPartidoBloco}: ${o.orientacaoVoto}`),
    };
  }));
}, 30 * 60e3);

const lista = (x) => (x == null ? [] : Array.isArray(x) ? x : [x]);

export const senado = () => bloco("senado", async () => {
  for (let i = 0; i < 10; i++) {
    const d = diasAtras(i);
    const j = await buscar(`https://legis.senado.leg.br/dadosabertos/plenario/resultado/${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}.json`);
    const sessoes = lista(j?.ResultadoPlenario?.Sessoes?.Sessao);
    if (!sessoes.length) continue;
    const itens = sessoes.flatMap((s) => lista(s?.Itens?.Item)).map((it) => {
      const resultado = String(it.textoResultado || "").replace(/^Resultado da matéria:/, "").trim().split("\n")[0].trim();
      const ementa = String(it.ementaPapeleta || "").trim();
      return resultado ? { identificacao: String(it.identificacao || "").trim(), ementa: ementa.charAt(0).toUpperCase() + ementa.slice(1), resultado } : null;
    }).filter(Boolean);
    if (itens.length) return { data: sessoes[0].dataSessao, sessao: sessoes[0].descricaoTipoSessao, itens };
  }
  return null;
}, 30 * 60e3);

// Credits outside the budget per year: all years or nothing, refreshed daily
export const creditos = () => bloco("creditos", async () => {
  const anoAtual = new Date().getFullYear();
  const anos = [];
  for (let a = 2019; a <= anoAtual; a++) anos.push(a);
  const re = /R\$\D{0,2}([\d.]+(?:,\d{2})?)/;
  const porAno = await Promise.all(anos.map(async (ano) => {
    let url = `${CAMARA}/proposicoes?siglaTipo=MPV&ano=${ano}&itens=100&ordem=ASC&ordenarPor=id`;
    let total = 0, quantidade = 0, paginas = 0;
    while (url && paginas < 5) {
      const j = await buscar(url);
      if (!j) return null;
      for (const p of j.dados || []) {
        if (!/^abre cr[ée]dito extraordin/i.test(String(p.ementa).trim())) continue;
        const m = re.exec(p.ementa);
        const v = m && Number(m[1].replace(/\./g, "").replace(",", "."));
        if (Number.isFinite(v)) { total += v; quantidade++; }
      }
      url = (j.links || []).find((l) => l.rel === "next")?.href || null;
      paginas++;
    }
    return { ano, total, quantidade, presidencia: ano <= 2022 ? "Bolsonaro" : ano <= 2026 ? "Lula" : "" };
  }));
  return porAno.some((x) => x == null) ? null : porAno;
}, 20 * 3600e3);

export const eleicao = () => bloco("eleicao", async () => {
  const j = await buscar("https://resultados.tse.jus.br/oficial/ele2026/6258/dados/br/br-c0001-e006258-u.json");
  const cargo = j?.carg?.[0];
  if (!cargo) return null;
  const candidatos = (cargo.agr || []).flatMap((a) => (a.par || []).flatMap((p) => (p.cand || []).map((c) => ({
    nome: c.nmu || c.nm, partido: p.sg, percentual: c.pvap, votos: Number(c.vap) || 0, eleito: c.e === "s",
  })))).sort((a, b) => b.votos - a.votos);
  if (!candidatos.length) return null;
  return { titulo: `${cargo.nmn}, ${j.t}º turno`, totalizacao: `${j.dg || ""} ${j.hg || ""}`.trim(), candidatos };
}, 60 * 60e3);

export const noticias = () => bloco("noticias", async () => {
  const todas = await Promise.all(FEEDS.map(async ([url, fonte]) => {
    const xml = await buscar(url, "text");
    if (!xml) return [];
    const doc = new DOMParser().parseFromString(xml, "text/xml");
    return [...doc.querySelectorAll("item")].map((it) => {
      const tx = (s) => (it.querySelector(s)?.textContent || "").trim();
      const limpo = (s) => new DOMParser().parseFromString(s, "text/html").body.textContent.replace(/\s+/g, " ").trim();
      const titulo = limpo(tx("title"));
      const link = tx("link");
      if (!titulo || !link.startsWith("http")) return null;
      return { titulo, link, fonte, em: Date.parse(tx("pubDate")) || 0 };
    }).filter(Boolean);
  }));
  const vistos = new Set();
  return todas.flat().filter((n) => !vistos.has(n.link) && vistos.add(n.link)).sort((a, b) => b.em - a.em).slice(0, 12);
}, 15 * 60e3);

// Bills presented in the last 7 days, explained
export const leisDaSemana = () => bloco("leis", async () => {
  const j = await buscar(`${CAMARA}/proposicoes?siglaTipo=PL,PEC,PLP,MPV&dataApresentacaoInicio=${iso(diasAtras(7))}&dataApresentacaoFim=${iso(new Date())}&ordem=DESC&ordenarPor=id&itens=100`);
  if (!Array.isArray(j?.dados)) return null;
  return j.dados.map((p) => ({
    id: p.id, sigla: `${p.siglaTipo} ${p.numero}/${p.ano}`, siglaTipo: p.siglaTipo, ementa: p.ementa,
  }));
}, 60 * 60e3);

export { explicar, humanizarReais };

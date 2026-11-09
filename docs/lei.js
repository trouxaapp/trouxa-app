// Port of app/src/main/java/com/trouxaapp/app/util/LeiExplicada.kt.
// Same rule: restate what the verb of the ementa means, quote its own
// purpose clause, link jargon to an official glossary. No match, no sentence.
// Keep both files in sync; site/test-lei.mjs checks the shared samples.

const CF = "https://www.planalto.gov.br/ccivil_03/constituicao/constituicao.htm";
const GLOSSARIO_SENADO = "https://www12.senado.leg.br/noticias/glossario-legislativo";
const GLOSSARIO_ORCAMENTO = "https://www.congressonacional.leg.br/legislacao-e-publicacoes/glossario-orcamentario";
const PROCESSO_LEGISLATIVO = "https://www.camara.leg.br/entenda-o-processo-legislativo/";
const CONSTITUICAO = "@constituicao";
const ADCT = "@adct";

const termo = (termo, explicacao, fonteNome, fonteUrl) => ({ termo, explicacao, fonteNome, fonteUrl });

export const GLOSSARIO = [
  ["credito extraordinario", termo("Crédito extraordinário",
    "Dinheiro liberado fora do Orçamento já aprovado, por medida provisória, e que começa a ser gasto antes de o Congresso votar. " +
    "A Constituição só permite isso para despesa imprevisível e urgente, como guerra, comoção interna ou calamidade pública (art. 167, § 3º).",
    "Senado Federal, glossário legislativo", `${GLOSSARIO_SENADO}/credito-extraordinario`)],
  ["credito suplementar", termo("Crédito suplementar",
    "Reforço de uma despesa que já existe no Orçamento, mas cujo dinheiro previsto não vai dar.",
    "Senado Federal, glossário legislativo", `${GLOSSARIO_SENADO}/credito-suplementar`)],
  ["credito especial", termo("Crédito especial",
    "Dinheiro para uma despesa que nem aparecia no Orçamento do ano. Precisa de lei aprovada pelo Congresso antes de ser gasto.",
    "Senado Federal, glossário legislativo", `${GLOSSARIO_SENADO}/credito-especial`)],
  ["encargos financeiros da uniao", termo("Encargos Financeiros da União",
    "Não é um ministério. É uma parte do Orçamento, administrada pelo Ministério da Fazenda, de onde saem despesas gerais da União, como subsídios, indenizações e encargos da dívida.",
    "Congresso Nacional, glossário orçamentário", GLOSSARIO_ORCAMENTO)],
  ["operacoes oficiais de credito", termo("Operações Oficiais de Crédito",
    "Também não é um ministério. É a parte do Orçamento que banca empréstimos de programas do governo e a diferença de juros que a União paga para baratear crédito, como o rural.",
    "Congresso Nacional, glossário orçamentário", GLOSSARIO_ORCAMENTO)],
  ["susta", termo("Sustar",
    "Suspender os efeitos de um ato do governo (decreto, portaria, resolução). A Constituição dá esse poder ao Congresso quando o Executivo passa do limite do que pode regulamentar (art. 49, inciso V).",
    "Constituição Federal", CF)],
  ["radiodifusao", termo("Radiodifusão",
    "Rádio e TV aberta. O governo concede, renova ou autoriza cada emissora, mas o ato só passa a valer depois que o Congresso aprova (art. 223 da Constituição).",
    "Constituição Federal", CF)],
  ["subvencao economica", termo("Subvenção econômica",
    "Dinheiro público pago a empresas ou produtores para cobrir parte de um custo, normalmente para segurar preço ou garantir abastecimento.",
    "Congresso Nacional, glossário orçamentário", GLOSSARIO_ORCAMENTO)],
  ["inelegibilidade", termo("Inelegibilidade",
    "Situação em que a pessoa fica proibida de se candidatar. As causas estão na Constituição e na Lei de Inelegibilidades (Lei Complementar 64/1990, a que a Ficha Limpa alterou).",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
  ["imputabilidade penal", termo("Imputabilidade penal",
    "Idade a partir da qual a pessoa responde a processo criminal como adulto. Hoje a Constituição fixa em 18 anos (art. 228).",
    "Constituição Federal", CF)],
  ["maioridade penal", termo("Maioridade penal",
    "Idade a partir da qual a pessoa responde a processo criminal como adulto. Hoje a Constituição fixa em 18 anos (art. 228).",
    "Constituição Federal", CF)],
  ["hediondo", termo("Crime hediondo",
    "Lista fechada de crimes considerados mais graves pela Lei 8.072/1990. Quem é condenado por eles não tem direito a fiança, anistia, graça ou indulto e demora mais para mudar de regime.",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
  ["disposicoes constitucionais transitorias", termo("ADCT",
    "Ato das Disposições Constitucionais Transitórias. Um anexo da Constituição com regras temporárias ou de transição. Mudar ele também exige PEC.",
    "Constituição Federal", CF)],
  ["lei complementar", termo("Lei complementar",
    "Lei que trata de assuntos que a Constituição reservou para ela. Precisa de maioria absoluta para passar: 257 deputados e 41 senadores, contando todos e não só os presentes.",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
  ["quota fixa", termo("Aposta de quota fixa",
    "Nome oficial das bets: aposta em que você já sabe quanto vai receber se acertar, porque o prêmio é o valor apostado vezes a cotação.",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
  ["classificacao indicativa", termo("Classificação indicativa",
    "A faixa de idade (livre, 10, 12, 14, 16, 18) que o Ministério da Justiça atribui a programas, filmes e jogos.",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
  ["emendas parlamentares", termo("Emenda parlamentar",
    "Parte do Orçamento que cada deputado e senador indica para onde vai. Uma fatia delas é de execução obrigatória: o governo tem que pagar.",
    "Congresso Nacional, glossário orçamentário", GLOSSARIO_ORCAMENTO)],
  ["superavit financeiro", termo("Superávit financeiro",
    "Dinheiro que sobrou em caixa de anos anteriores em contas com destino carimbado. Usar esse saldo exige autorização em lei.",
    "Congresso Nacional, glossário orçamentário", GLOSSARIO_ORCAMENTO)],
  ["responsabilidade fiscal", termo("Lei de Responsabilidade Fiscal",
    "Lei Complementar 101/2000. Põe limite em gasto com pessoal e em dívida de União, estados e municípios e proíbe certas despesas em ano de eleição.",
    "Câmara dos Deputados", PROCESSO_LEGISLATIVO)],
];

const APELIDOS = {
  "2.848/1940": "Código Penal", "3.689/1941": "Código de Processo Penal", "5.452/1943": "CLT",
  "4.737/1965": "Código Eleitoral", "5.172/1966": "Código Tributário Nacional", "4.320/1964": "lei geral do Orçamento",
  "1.079/1950": "Lei do Impeachment", "7.210/1984": "Lei de Execução Penal", "8.069/1990": "Estatuto da Criança e do Adolescente",
  "8.078/1990": "Código de Defesa do Consumidor", "8.080/1990": "lei do SUS", "8.072/1990": "Lei dos Crimes Hediondos",
  "8.112/1990": "estatuto dos servidores federais", "8.213/1991": "lei dos benefícios do INSS", "8.245/1991": "Lei do Inquilinato",
  "8.429/1992": "Lei de Improbidade Administrativa", "8.742/1993": "Lei Orgânica da Assistência Social",
  "9.096/1995": "Lei dos Partidos Políticos", "9.394/1996": "Lei de Diretrizes e Bases da Educação", "9.503/1997": "Código de Trânsito",
  "9.504/1997": "Lei das Eleições", "9.605/1998": "Lei de Crimes Ambientais", "9.656/1998": "Lei dos Planos de Saúde",
  "10.406/2002": "Código Civil", "10.741/2003": "Estatuto da Pessoa Idosa", "10.826/2003": "Estatuto do Desarmamento",
  "11.340/2006": "Lei Maria da Penha", "11.343/2006": "Lei de Drogas", "12.527/2011": "Lei de Acesso à Informação",
  "12.651/2012": "Código Florestal", "12.965/2014": "Marco Civil da Internet", "13.105/2015": "Código de Processo Civil",
  "13.146/2015": "Estatuto da Pessoa com Deficiência", "13.709/2018": "LGPD", "14.133/2021": "Lei de Licitações",
  "14.790/2023": "lei das bets", "64/1990": "Lei de Inelegibilidades", "101/2000": "Lei de Responsabilidade Fiscal",
  "214/2025": "lei que regulamenta a reforma tributária",
};

export const termosDoGlossario = () => {
  const vistos = new Set();
  return GLOSSARIO.map(([, t]) => t).filter((t) => !vistos.has(t.termo) && vistos.add(t.termo));
};

const normalize = (s) => s.toLowerCase().normalize("NFD").replace(/\p{Mn}+/gu, "");
const minuscula = (s) => (s ? s.charAt(0).toLocaleLowerCase("pt-BR") + s.slice(1) : s);
const fechar = (frase) => (frase.endsWith("…") ? frase : frase + ".");
const cortar = (t, max) => (t.length > max ? t.slice(0, max).replace(/\s+\S*$/, "").replace(/[,;:]+$/, "") + "…" : t);

const fmt = (v, casas) => v.toLocaleString("pt-BR", { minimumFractionDigits: casas, maximumFractionDigits: casas });

export function humanizarReais(v) {
  if (v >= 1e9) return "R$ " + fmt(v / 1e9, 2).replace(/,00$/, "") + (v < 2e9 ? " bilhão" : " bilhões");
  if (v >= 1e6) return "R$ " + fmt(v / 1e6, 1).replace(/,0$/, "") + (v < 2e6 ? " milhão" : " milhões");
  return "R$ " + fmt(v, 2);
}

function valorEmReais(original) {
  const m = original.match(/R\$\s*([\d.]+(?:,\d{2})?)/);
  if (!m) return "";
  const n = Number(m[1].replace(/\./g, "").replace(",", "."));
  return Number.isFinite(n) ? humanizarReais(n) : "";
}

function destinoDoCredito(original) {
  const m = original.match(/em favor d([aoe]s?)\s+(.+?)(?:,|;)?\s+no valor/i) ||
    original.match(/em favor d([aoe]s?)\s+(.+?)(?:,\s*para|\.\s*$)/i);
  if (!m) return "";
  const artigo = m[1].toLowerCase();
  const corpo = m[2].trim().replace(/[,;]+$/, "");
  if (corpo.length > 160) return "";
  return artigo === "e" ? corpo : `${artigo} ${corpo}`;
}

const ROTULO_ATO = { resolucao: "resolução", "instrucao normativa": "instrução normativa", "norma tecnica": "norma técnica", deliberacao: "deliberação" };

function leiAlvo(original, n, siglaTipo) {
  if (siglaTipo === "PEC" || /^(altera|acrescenta|modifica|inclui)[^.]{0,80}(constituicao|disposicoes constitucionais transitorias)/.test(n)) {
    return n.includes("disposicoes constitucionais transitorias") ? ADCT : CONSTITUICAO;
  }
  const lei = original.match(/(Lei Complementar|Decreto-Lei|Lei)\s*n?[ºo°.]*\s*([\d.]+)(?:,\s*de\s*\d{1,2}[ºo°]?\s*de\s*\p{L}+\s*de\s*|,\s*de\s*|\/)(\d{4})(\s*\(([^)]{3,80})\))?/iu);
  if (!lei) return "";
  const numero = `${lei[2]}/${lei[3]}`;
  const apelido = (lei[5] || "").trim() || APELIDOS[numero] || "";
  const tipo = /^lei complementar$/i.test(lei[1]) ? "Lei Complementar" : /^decreto/i.test(lei[1]) ? "Decreto-Lei" : "Lei";
  return apelido ? `${apelido} (${tipo} ${numero})` : `${tipo} ${numero}`;
}

function nomeCriado(original) {
  let t = original.replace(/^(Institui|Cria),?\s+/i, "");
  t = t.replace(/^no âmbito d[oa] .+?,\s*/i, "");
  t = t.split(/;|,\s+(?:estabelece|disp[õo]e|altera|define|cria|institui|prev[êe]|regulamenta|disciplina|destinad[oa]|fundo de natureza|com o objetivo|com a finalidade|e d[áa] outras)|\s+e\s+(?:d[áa] outras|disp[õo]e|altera|estabelece|define)\b|,?\s+para\s|\.\s*$/i)[0]
    .trim().replace(/[,.]+$/, "");
  t = cortar(t, 160);
  return t.length >= 6 ? t : "";
}

function aposVerbo(original, verbo) {
  let t = original.slice(Math.min(verbo.length, original.length)).trim();
  t = t.split(";")[0].split(", e dá outras")[0].split(" e dá outras")[0];
  t = t.split(/,?\s+(?:e\s+)?(?:altera|acrescenta|revoga)\s+(?:a|o|os|as)\s+(?:Lei|Decreto|Medida)|,\s+para\s|,\s+com o objetivo/i)[0];
  t = t.replace(/[.,\s]+$/, "");
  return minuscula(cortar(t, 180));
}

function objetivoDeclarado(original) {
  const m = original.match(/(?:,\s*|\s)(?:para|a fim de|com o objetivo de|com a finalidade de)\s+(.+)/i);
  if (!m) return "";
  let t = m[1].trim();
  if (/^(os? fins?|o fim) que especifica/i.test(t)) return "";
  if (!/^\p{L}+(ar|er|ir|or)(?![\p{L}\d_])/u.test(t)) return "";
  t = t.split(/[;,]?\s+(?:e\s+)?(?:altera|acrescenta|revoga|institui)\s+(?:a|o|os|as)\s+(?:Lei|Medida|Decreto|art)/i)[0];
  t = t.split("; ")[0];
  t = t.replace(/[;,]?\s*e d[aá] outras provid[eê]ncias\.?$/i, "");
  t = t.replace(/[.;,\s]+$/, "");
  if (t.length < 12) return "";
  return minuscula(cortar(t, 260));
}

export function explicar(siglaTipo, ementa) {
  const original = (ementa || "").replace(/\s+/g, " ").trim();
  const n = normalize(original);
  const vazio = { emUmaFrase: "", objetivo: "", observacoes: [], termos: [], valor: "" };
  if (!n) return vazio;

  const obs = [];
  let valor = "";
  let frase = "";
  const comeca = (...p) => p.some((x) => n.startsWith(x));

  if (comeca("abre credito extraordinario")) {
    valor = valorEmReais(original);
    const destino = destinoDoCredito(original);
    if (/fins? que especifica/.test(n)) {
      obs.push('A ementa não diz no quê o dinheiro vai ser gasto ("para o fim que especifica"). O destino está no texto completo.');
    }
    frase = `Libera ${valor || "dinheiro"} fora do Orçamento já aprovado${destino ? ` para ${destino}` : ""}. O gasto pode começar antes da votação no Congresso.`;
  } else if (comeca("abre credito suplementar", "abre credito especial")) {
    valor = valorEmReais(original);
    const destino = destinoDoCredito(original);
    frase = (n.startsWith("abre credito suplementar") ? "Reforça o Orçamento com " : "Cria no Orçamento uma despesa nova de ") +
      (valor || "um valor") + (destino ? ` para ${destino}` : "") + ".";
  } else if (comeca("susta")) {
    obs.push("Não passa por sanção da Presidência: se Câmara e Senado aprovarem, o ato do governo perde o efeito.");
    const alvo = (n.match(/(decreto|portaria|resolucao|instrucao normativa|norma tecnica|deliberacao|ato)/) || [])[0];
    const feminino = ["portaria", "resolucao", "instrucao normativa", "norma tecnica", "deliberacao"].includes(alvo);
    frase = alvo ? `Tenta cancelar um${feminino ? "a" : ""} ${ROTULO_ATO[alvo] || alvo} do governo.` : "Tenta cancelar um ato do governo.";
  } else if (n.startsWith("aprova o ato que") && (n.includes("radiodifusao") || /\b(radio|televisao|sons e imagens)\b/.test(n))) {
    frase = n.startsWith("aprova o ato que renova") ? "Confirma a renovação da concessão de uma emissora de rádio ou TV."
      : n.startsWith("aprova o ato que outorga") ? "Confirma a concessão de uma emissora de rádio ou TV nova."
      : "Confirma a autorização para uma emissora de rádio funcionar.";
  } else if (n.startsWith("aprova o texto d")) {
    frase = "Aprova um tratado ou acordo internacional que o Brasil assinou. Sem esse aval do Congresso, o país não pode ratificar.";
  } else if (n.startsWith("revoga")) {
    frase = "Cancela uma lei, ou um trecho dela, que hoje está valendo.";
  } else if (/^(denomina|da a denominacao|da denominacao|confere a denominacao|atribui a denominacao|atribui o nome)/.test(n)) {
    frase = "Dá nome a um lugar ou obra pública, como rodovia, ponte, viaduto ou prédio.";
  } else if (/^institui (o|a) (dia|semana|mes|ano) /.test(n) || /^institui,? no calendario/.test(n)) {
    frase = "Cria uma data comemorativa no calendário oficial.";
  } else if (comeca("institui feriado", "declara feriado")) {
    frase = "Cria um feriado.";
  } else if (n.includes("livro dos herois e heroinas da patria")) {
    frase = "Põe um nome no Livro dos Heróis e Heroínas da Pátria. É homenagem.";
  } else if (/^(confere|concede) (o|ao)? ?titulo/.test(n) || n.includes("titulo de capital nacional")) {
    frase = "Dá um título honorífico. É homenagem.";
  } else if (/^(declara|reconhece) .{0,80}patrimonio/.test(n)) {
    frase = "Reconhece algo como patrimônio cultural ou histórico.";
  } else if (comeca("altera", "acrescenta", "modifica", "inclui")) {
    const alvo = leiAlvo(original, n, siglaTipo);
    const acrescenta = comeca("acrescenta", "inclui");
    if (alvo === CONSTITUICAO) frase = acrescenta ? "Acrescenta um trecho novo à Constituição." : "Muda o texto da Constituição.";
    else if (alvo === ADCT) frase = "Mexe no ADCT, o anexo de regras transitórias da Constituição.";
    else if (acrescenta) frase = alvo ? `Acrescenta um trecho novo a uma lei que já existe: ${alvo}.` : "Acrescenta um trecho novo a uma lei que já existe.";
    else frase = alvo ? `Muda uma lei que já existe: ${alvo}.` : "Muda uma lei que já existe.";
  } else if (/^(institui|cria)[ ,]/.test(n)) {
    const nome = nomeCriado(original);
    frase = nome ? fechar(`Cria ${nome}`) : "Cria algo que ainda não existe na lei.";
  } else if (comeca("autoriza")) {
    const o = aposVerbo(original, "Autoriza");
    frase = o ? fechar(`Dá permissão legal para ${o}`) : "Dá uma permissão legal ao governo.";
  } else if (comeca("proibe", "veda")) {
    const o = aposVerbo(original, n.startsWith("proibe") ? "Proíbe" : "Veda");
    frase = o ? fechar(`Proíbe ${o}`) : "Cria uma proibição.";
  } else if (comeca("dispoe sobre")) {
    const o = aposVerbo(original, "Dispõe sobre");
    frase = o ? fechar(`Cria regras sobre ${o}`) : "";
  } else if (comeca("estabelece")) {
    const o = aposVerbo(original, "Estabelece");
    frase = o ? fechar(`Define ${o}`) : "";
  }

  if (siglaTipo === "MPV" || siglaTipo === "MP") obs.push("Medida provisória já vale desde a publicação. O Congresso tem até 120 dias para aprovar, ou ela cai.");
  if (siglaTipo === "PEC") obs.push("Para passar, precisa de 308 deputados e 49 senadores a favor, em dois turnos em cada Casa.");
  if (siglaTipo === "PLP") obs.push("Para passar, precisa de 257 deputados e 41 senadores a favor.");

  const vistos = new Set();
  const termos = GLOSSARIO.filter(([g]) => n.includes(g)).map(([, t]) => t)
    .filter((t) => !vistos.has(t.termo) && vistos.add(t.termo));

  let objetivo = objetivoDeclarado(original);
  if (objetivo && frase.includes(objetivo)) objetivo = "";

  return { emUmaFrase: frase, objetivo, observacoes: obs, termos, valor };
}

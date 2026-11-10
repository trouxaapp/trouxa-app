// Resolve a foto oficial de um deputado na API da Câmara.
//
// O app resolve autor por id, nunca por nome. Aqui é o contrário: o post é
// escrito por humano com o nome, e o pipeline busca o id — e loga qual id
// achou, pra revisão. Quando o match é errado, é só cravar "foto_url" no
// feed/posts.json e a busca nem roda.
//
// A API da Câmara cai com frequência (503). Duas tentativas, depois desiste:
// cartaz sem foto é melhor que cartaz quebrado.

const BASE = "https://dadosabertos.camara.leg.br/api/v2";

const normalizar = (s) =>
  String(s ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();

async function pedir(url, tentativas = 2) {
  for (let i = 0; i < tentativas; i++) {
    try {
      const resp = await fetch(url, {
        headers: { accept: "application/json", "user-agent": "trouxa-automacao" },
      });
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
      return await resp.json();
    } catch (e) {
      if (i === tentativas - 1) throw e;
      await new Promise((r) => setTimeout(r, 1500));
    }
  }
  return null;
}

/**
 * @returns {Promise<{id:number,nome:string,partido:string,uf:string,foto:string}|null>}
 */
export async function buscarDeputado(nome, { uf = "" } = {}) {
  const alvo = normalizar(nome);
  if (!alvo) return null;
  const url = `${BASE}/deputados?nome=${encodeURIComponent(nome)}&itens=30&ordem=ASC&ordenarPor=nome`;
  const json = await pedir(url);
  const lista = json?.dados || [];
  const comFoto = lista.filter((d) => d?.id && d?.nome);
  const doUf = uf ? comFoto.filter((d) => normalizar(d.siglaUf) === normalizar(uf)) : comFoto;
  const escolhidos = doUf.length ? doUf : comFoto;
  const exato = escolhidos.find((d) => normalizar(d.nome) === alvo) || escolhidos[0];
  if (!exato) return null;
  return {
    id: exato.id,
    nome: exato.nome,
    partido: exato.siglaPartido || "",
    uf: exato.siglaUf || "",
    foto: `https://www.camara.leg.br/internet/deputado/bandep/${exato.id}.jpg`,
  };
}

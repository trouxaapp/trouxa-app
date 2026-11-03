package com.trouxaapp.app.data.painel

/**
 * The daily panel. Every block carries the source it came from and the
 * moment it was fetched; a block that failed is null (or empty) and the
 * screen says so instead of filling the gap.
 */
data class Painel(
    val mercado: List<Indicador> = emptyList(),
    val parados: List<Indicador> = emptyList(),
    val precos: Precos? = null,
    val camaraHoje: CamaraHoje? = null,
    val votacoesCamara: List<Votacao> = emptyList(),
    val senado: SenadoDia? = null,
    val creditos: List<CreditoAno> = emptyList(),
    // The yearly series is 16+ requests; it is refreshed at most once a day
    val creditosEm: Long = 0,
    val eleicao: Eleicao? = null,
    val noticias: List<Noticia> = emptyList(),
    val atualizadoEm: Long = 0
)

/** "Dólar R$ 5,49, subiu 0,4% desde 31/10" or "Selic 15% desde 18/06/2025". */
data class Indicador(
    val nome: String,
    val valor: String,
    val mudanca: String,
    // +1 up, -1 down, 0 unchanged; drives the color only
    val direcao: Int,
    val dataReferencia: String,
    val fonte: String,
    val fonteUrl: String
)

data class Precos(
    val mes: String,                 // "setembro 2026"
    val indiceGeral: Double,
    val altas: List<ItemPreco>,
    val quedas: List<ItemPreco>,
    val fonte: String,
    val fonteUrl: String
)

data class ItemPreco(val nome: String, val variacao: Double)

data class CamaraHoje(
    val plenario: String,            // "Sessão Deliberativa Extraordinária, em andamento"
    val reunioes: Int,               // committee meetings today (any status)
    val canceladas: Int
)

data class Votacao(
    val data: String,                // yyyy-MM-dd
    val descricao: String,
    val aprovada: Boolean?,
    val placar: String,              // "Sim 285 x Não 134" or ""
    val orientacoes: List<String>,   // "PL: Não", "PT: Sim"
    val orgao: String,
    val url: String,
    // Bill being voted ("PL 5239/2025") and its official summary; nullable because
    // panels cached by older versions do not have them
    val proposicao: String? = null,
    val siglaTipo: String? = null,
    val ementa: String? = null
)

data class SenadoDia(
    val data: String,                // dd/MM/yyyy
    val sessao: String,
    val itens: List<ItemSenado>
)

data class ItemSenado(val identificacao: String, val ementa: String, val resultado: String)

data class CreditoAno(
    val ano: Int,
    val total: Double,
    val quantidade: Int,
    val presidencia: String
)

data class Eleicao(
    val titulo: String,              // "Presidente, 2º turno"
    val dataTotalizacao: String,
    val candidatos: List<Candidato>,
    val fonteUrl: String
)

data class Candidato(
    val nome: String,
    val partido: String,
    val percentual: String,
    val votos: Long,
    val eleito: Boolean
)

data class Noticia(
    val titulo: String,
    val resumo: String,
    val link: String,
    val fonte: String,
    val publicadaEm: Long
)

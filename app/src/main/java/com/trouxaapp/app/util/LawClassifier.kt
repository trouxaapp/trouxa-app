package com.trouxaapp.app.util

import java.text.Normalizer

/**
 * Classifies bills by theme and explains bill types.
 *
 * Hard rule carried over from the rewrite: this object never asserts
 * anything about a bill that is not in the bill itself. It groups bills
 * by theme keywords (organization only) and explains what a PL/PEC/MPV
 * is in regimental terms (public, verifiable facts). It does NOT score
 * severity, does NOT guess winners/losers, does NOT editorialize.
 * Opinionated analysis only enters the app through human curation with
 * mandatory sources (see LeisRepository.applyCuradoria).
 */
object LawClassifier {

    data class Classificacao(
        val tema: String,
        val tipoExplicacao: String,
        val tituloPopular: String
    )

    // Theme buckets used for filtering in the feed. Scoring is by keyword
    // hits on the normalized ementa; ties go to the higher total weight.
    private val temas: List<Pair<String, List<Pair<String, Int>>>> = listOf(
        "Trabalho e aposentadoria" to listOf(
            "aposentad" to 3, "inss" to 3, "previdenc" to 3, "trabalhist" to 3,
            "clt" to 3, "fgts" to 3, "salario" to 2, "trabalhador" to 2,
            "emprego" to 2, "ferias" to 2, "jornada" to 2, "sindicat" to 2
        ),
        "Impostos e tributos" to listOf(
            "tribut" to 3, "imposto" to 3, "icms" to 3, "ipva" to 3, "iptu" to 3,
            "contribuicao social" to 3, "aliquota" to 3, "isencao" to 2,
            "incentivo fiscal" to 2, "imposto de renda" to 3, "cofins" to 3
        ),
        "Saúde" to listOf(
            "saude" to 3, "sus" to 3, "hospital" to 3, "medicament" to 3,
            "vacina" to 3, "medic" to 2, "doenca" to 2, "plano de saude" to 3,
            "enferm" to 2, "atendimento" to 1
        ),
        "Educação" to listOf(
            "educacao" to 3, "escola" to 3, "universidad" to 3, "professor" to 3,
            "ensino" to 3, "estudante" to 2, "enem" to 3, "fies" to 3,
            "creche" to 2, "alfabetiz" to 2
        ),
        "Segurança e justiça" to listOf(
            "penal" to 3, "crime" to 3, "policia" to 3, "seguranca publica" to 3,
            "penitenciari" to 3, "violencia" to 2, "arma de fogo" to 3,
            "feminicidio" to 3, "trafico" to 2, "codigo penal" to 3
        ),
        "Internet e tecnologia" to listOf(
            "internet" to 3, "rede social" to 3, "digital" to 2, "dados pessoais" to 3,
            "inteligencia artificial" to 3, "aplicativo" to 2, "plataforma" to 2,
            "cibernetic" to 3, "telecomunicac" to 2
        ),
        "Meio ambiente" to listOf(
            "ambiental" to 3, "meio ambiente" to 3, "desmatamento" to 3,
            "clima" to 2, "poluic" to 3, "energia renovavel" to 3,
            "unidade de conservacao" to 3, "fauna" to 2, "flora" to 2
        ),
        "Transporte e combustível" to listOf(
            "transporte" to 3, "transito" to 3, "combustivel" to 3, "gasolina" to 3,
            "etanol" to 2, "pedagio" to 3, "rodovi" to 2, "motorista" to 2,
            "cnh" to 3, "aereo" to 2
        ),
        "Moradia e cidades" to listOf(
            "moradia" to 3, "habitacao" to 3, "aluguel" to 3, "imovel" to 2,
            "saneamento" to 3, "urbanis" to 2, "regularizacao fundiaria" to 3
        ),
        "Eleições e política" to listOf(
            "eleitoral" to 3, "eleicao" to 3, "partido politic" to 3, "mandato" to 2,
            "campanha eleitoral" to 3, "voto" to 2, "candidat" to 2
        ),
        "Orçamento e gasto público" to listOf(
            "orcament" to 3, "credito suplementar" to 3, "despesa publica" to 3,
            "emenda parlamentar" to 3, "divida publica" to 3, "fundo" to 1,
            "repasse" to 2, "licitac" to 3
        ),
        "Direitos e cidadania" to listOf(
            "direitos humanos" to 3, "consumidor" to 3, "crianca e do adolescente" to 3,
            "idoso" to 3, "pessoa com deficiencia" to 3, "igualdade" to 2,
            "discriminac" to 3, "violencia domestica" to 3
        ),
        "Agro e alimentos" to listOf(
            "agropecuari" to 3, "agricultur" to 3, "rural" to 2, "agrotoxic" to 3,
            "alimento" to 2, "pesca" to 2, "pecuari" to 3, "safra" to 3
        ),
        "Apostas e jogos" to listOf(
            "aposta" to 3, "bets" to 3, "jogo de azar" to 3, "loteria" to 3,
            "cassino" to 3, "bingo" to 3
        )
    )

    private val tipoExplicacoes = mapOf(
        "PL" to "Projeto de Lei. Cria ou altera uma lei comum. Pra virar lei, precisa ser aprovado na Câmara e no Senado e depois sancionado pela Presidência.",
        "PEC" to "Proposta de Emenda à Constituição. Muda o texto da própria Constituição. Precisa de 3/5 dos votos, em dois turnos, na Câmara e no Senado. Não passa por sanção presidencial.",
        "PLP" to "Projeto de Lei Complementar. Regulamenta pontos que a Constituição exige detalhar. Precisa de maioria absoluta na Câmara e no Senado.",
        "MPV" to "Medida Provisória. Editada pela Presidência, já vale como lei desde a publicação. O Congresso tem 120 dias pra aprovar, senão ela perde a validade.",
        "MP" to "Medida Provisória. Editada pela Presidência, já vale como lei desde a publicação. O Congresso tem 120 dias pra aprovar, senão ela perde a validade.",
        "PDL" to "Projeto de Decreto Legislativo. Instrumento do Congresso pra sustar atos do Executivo ou aprovar acordos internacionais. Não passa por sanção presidencial."
    )

    fun classify(siglaTipo: String, ementa: String): Classificacao {
        val texto = normalize(ementa)
        var melhorTema = "Outros"
        var melhorScore = 0
        for ((tema, keywords) in temas) {
            var score = 0
            for ((kw, peso) in keywords) {
                if (texto.contains(kw)) score += peso
            }
            if (score > melhorScore) {
                melhorScore = score
                melhorTema = tema
            }
        }
        return Classificacao(
            tema = melhorTema,
            tipoExplicacao = tipoExplicacoes[siglaTipo]
                ?: "Proposição legislativa em tramitação na Câmara dos Deputados.",
            tituloPopular = buildTitulo(ementa)
        )
    }

    /**
     * Produces a short title from the real ementa text. Cleanup only:
     * strips law-number boilerplate and trims length. Adds no words that
     * are not in the original.
     */
    fun buildTitulo(ementa: String): String {
        var t = cleanEmenta(ementa)
        // Do NOT cut at the first ". ": legal text is full of "art.", "arts.",
        // "inc.", "n.º", "§" that are abbreviations, not sentence ends (that bug
        // turned "Susta a aplicação do art. 5º..." into "Susta a aplicação do art").
        // Just trim to a readable length at a word boundary; the card shows 3 lines.
        if (t.length > 140) {
            t = t.take(140).substringBeforeLast(' ').trimEnd(',', ';', ':', '.') + "…"
        }
        return t.removeSuffix(".")
    }

    /**
     * Removes legal boilerplate from an ementa without changing meaning:
     * "Lei nº 8.080, de 19 de setembro de 1990" -> "Lei 8.080/1990".
     */
    fun cleanEmenta(ementa: String): String {
        var t = ementa.trim()
        // "Lei nº 8.080, de 19 de setembro de 1990" -> "Lei 8.080/1990"
        t = t.replace(
            Regex("""(Lei|Decreto|Decreto-Lei)\s*n[ºo°.]*\s*([\d.]+),?\s*de\s*\d{1,2}[ºo°]?\s*de\s*\p{L}+\s*de\s*(\d{4})""", RegexOption.IGNORE_CASE)
        ) { m -> "${m.groupValues[1]} ${m.groupValues[2]}/${m.groupValues[3]}" }
        // Leftover "nº" markers
        t = t.replace(Regex("""\s*n[ºo°.]+\s*"""), " ")
        // Parenthetical legislature references add noise on cards
        t = t.replace(Regex("""\s{2,}"""), " ").trim()
        return t
    }

    private fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("""\p{Mn}+"""), "")
}

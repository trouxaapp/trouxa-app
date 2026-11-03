package com.trouxaapp.app.util

import java.text.Normalizer
import java.util.Locale

/**
 * Turns an ementa into plain Portuguese: what the bill does in one sentence,
 * the purpose the ementa itself declares, and the jargon it uses.
 *
 * Same hard rule as LawClassifier: nothing here is a judgment about the bill.
 * The sentence only restates what the verb of the ementa means in regimental
 * terms ("Susta" = tries to cancel an executive act), the purpose is a quote
 * of the ementa's own "para ..." clause, and every glossary term carries an
 * official source. When no pattern matches, the result is empty and the
 * screen simply shows the ementa.
 */
object LeiExplicada {

    data class Termo(
        val termo: String,
        val explicacao: String,
        val fonteNome: String,
        val fonteUrl: String
    )

    data class Explicacao(
        val emUmaFrase: String,
        val objetivo: String,
        val observacoes: List<String>,
        val termos: List<Termo>,
        // Money figure lifted from the ementa, already humanized
        val valor: String
    ) {
        val vazia get() = emUmaFrase.isBlank() && objetivo.isBlank() && termos.isEmpty()
    }

    private const val CF = "https://www.planalto.gov.br/ccivil_03/constituicao/constituicao.htm"
    private const val GLOSSARIO_SENADO = "https://www12.senado.leg.br/noticias/glossario-legislativo"
    private const val GLOSSARIO_ORCAMENTO =
        "https://www.congressonacional.leg.br/legislacao-e-publicacoes/glossario-orcamentario"
    private const val PROCESSO_LEGISLATIVO = "https://www.camara.leg.br/entenda-o-processo-legislativo/"
    private const val CONSTITUICAO = "@constituicao"
    private const val ADCT = "@adct"

    // Timeless definitions. Keyed by a normalized trigger found in the ementa.
    private val glossario: List<Pair<String, Termo>> = listOf(
        "credito extraordinario" to Termo(
            "Crédito extraordinário",
            "Dinheiro liberado fora do Orçamento já aprovado, por medida provisória, e que começa a ser gasto antes de o Congresso votar. " +
                "A Constituição só permite isso para despesa imprevisível e urgente, como guerra, comoção interna ou calamidade pública (art. 167, § 3º).",
            "Senado Federal, glossário legislativo",
            "$GLOSSARIO_SENADO/credito-extraordinario"
        ),
        "credito suplementar" to Termo(
            "Crédito suplementar",
            "Reforço de uma despesa que já existe no Orçamento, mas cujo dinheiro previsto não vai dar.",
            "Senado Federal, glossário legislativo",
            "$GLOSSARIO_SENADO/credito-suplementar"
        ),
        "credito especial" to Termo(
            "Crédito especial",
            "Dinheiro para uma despesa que nem aparecia no Orçamento do ano. Precisa de lei aprovada pelo Congresso antes de ser gasto.",
            "Senado Federal, glossário legislativo",
            "$GLOSSARIO_SENADO/credito-especial"
        ),
        "encargos financeiros da uniao" to Termo(
            "Encargos Financeiros da União",
            "Não é um ministério. É uma parte do Orçamento, administrada pelo Ministério da Fazenda, de onde saem despesas gerais da União, como subsídios, indenizações e encargos da dívida.",
            "Congresso Nacional, glossário orçamentário",
            GLOSSARIO_ORCAMENTO
        ),
        "operacoes oficiais de credito" to Termo(
            "Operações Oficiais de Crédito",
            "Também não é um ministério. É a parte do Orçamento que banca empréstimos de programas do governo e a diferença de juros que a União paga para baratear crédito, como o rural.",
            "Congresso Nacional, glossário orçamentário",
            GLOSSARIO_ORCAMENTO
        ),
        "susta" to Termo(
            "Sustar",
            "Suspender os efeitos de um ato do governo (decreto, portaria, resolução). A Constituição dá esse poder ao Congresso quando o Executivo passa do limite do que pode regulamentar (art. 49, inciso V).",
            "Constituição Federal",
            CF
        ),
        "radiodifusao" to Termo(
            "Radiodifusão",
            "Rádio e TV aberta. O governo concede, renova ou autoriza cada emissora, mas o ato só passa a valer depois que o Congresso aprova (art. 223 da Constituição).",
            "Constituição Federal",
            CF
        ),
        "subvencao economica" to Termo(
            "Subvenção econômica",
            "Dinheiro público pago a empresas ou produtores para cobrir parte de um custo, normalmente para segurar preço ou garantir abastecimento.",
            "Congresso Nacional, glossário orçamentário",
            GLOSSARIO_ORCAMENTO
        ),
        "inelegibilidade" to Termo(
            "Inelegibilidade",
            "Situação em que a pessoa fica proibida de se candidatar. As causas estão na Constituição e na Lei de Inelegibilidades (Lei Complementar 64/1990, a que a Ficha Limpa alterou).",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        ),
        "imputabilidade penal" to Termo(
            "Imputabilidade penal",
            "Idade a partir da qual a pessoa responde a processo criminal como adulto. Hoje a Constituição fixa em 18 anos (art. 228).",
            "Constituição Federal",
            CF
        ),
        "maioridade penal" to Termo(
            "Maioridade penal",
            "Idade a partir da qual a pessoa responde a processo criminal como adulto. Hoje a Constituição fixa em 18 anos (art. 228).",
            "Constituição Federal",
            CF
        ),
        "hediondo" to Termo(
            "Crime hediondo",
            "Lista fechada de crimes considerados mais graves pela Lei 8.072/1990. Quem é condenado por eles não tem direito a fiança, anistia, graça ou indulto e demora mais para mudar de regime.",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        ),
        "disposicoes constitucionais transitorias" to Termo(
            "ADCT",
            "Ato das Disposições Constitucionais Transitórias. Um anexo da Constituição com regras temporárias ou de transição. Mudar ele também exige PEC.",
            "Constituição Federal",
            CF
        ),
        "lei complementar" to Termo(
            "Lei complementar",
            "Lei que trata de assuntos que a Constituição reservou para ela. Precisa de maioria absoluta para passar: 257 deputados e 41 senadores, contando todos e não só os presentes.",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        ),
        "quota fixa" to Termo(
            "Aposta de quota fixa",
            "Nome oficial das bets: aposta em que você já sabe quanto vai receber se acertar, porque o prêmio é o valor apostado vezes a cotação.",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        ),
        "classificacao indicativa" to Termo(
            "Classificação indicativa",
            "A faixa de idade (livre, 10, 12, 14, 16, 18) que o Ministério da Justiça atribui a programas, filmes e jogos.",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        ),
        "emendas parlamentares" to Termo(
            "Emenda parlamentar",
            "Parte do Orçamento que cada deputado e senador indica para onde vai. Uma fatia delas é de execução obrigatória: o governo tem que pagar.",
            "Congresso Nacional, glossário orçamentário",
            GLOSSARIO_ORCAMENTO
        ),
        "superavit financeiro" to Termo(
            "Superávit financeiro",
            "Dinheiro que sobrou em caixa de anos anteriores em contas com destino carimbado. Usar esse saldo exige autorização em lei.",
            "Congresso Nacional, glossário orçamentário",
            GLOSSARIO_ORCAMENTO
        ),
        "responsabilidade fiscal" to Termo(
            "Lei de Responsabilidade Fiscal",
            "Lei Complementar 101/2000. Põe limite em gasto com pessoal e em dívida de União, estados e municípios e proíbe certas despesas em ano de eleição.",
            "Câmara dos Deputados",
            PROCESSO_LEGISLATIVO
        )
    )

    // Well-known laws referred to by number in ementas without their nickname
    private val apelidos = mapOf(
        "2.848/1940" to "Código Penal",
        "3.689/1941" to "Código de Processo Penal",
        "5.452/1943" to "CLT",
        "4.737/1965" to "Código Eleitoral",
        "5.172/1966" to "Código Tributário Nacional",
        "4.320/1964" to "lei geral do Orçamento",
        "1.079/1950" to "Lei do Impeachment",
        "7.210/1984" to "Lei de Execução Penal",
        "8.069/1990" to "Estatuto da Criança e do Adolescente",
        "8.078/1990" to "Código de Defesa do Consumidor",
        "8.080/1990" to "lei do SUS",
        "8.072/1990" to "Lei dos Crimes Hediondos",
        "8.112/1990" to "estatuto dos servidores federais",
        "8.213/1991" to "lei dos benefícios do INSS",
        "8.245/1991" to "Lei do Inquilinato",
        "8.429/1992" to "Lei de Improbidade Administrativa",
        "8.742/1993" to "Lei Orgânica da Assistência Social",
        "9.096/1995" to "Lei dos Partidos Políticos",
        "9.394/1996" to "Lei de Diretrizes e Bases da Educação",
        "9.503/1997" to "Código de Trânsito",
        "9.504/1997" to "Lei das Eleições",
        "9.605/1998" to "Lei de Crimes Ambientais",
        "9.656/1998" to "Lei dos Planos de Saúde",
        "10.406/2002" to "Código Civil",
        "10.741/2003" to "Estatuto da Pessoa Idosa",
        "10.826/2003" to "Estatuto do Desarmamento",
        "11.340/2006" to "Lei Maria da Penha",
        "11.343/2006" to "Lei de Drogas",
        "12.527/2011" to "Lei de Acesso à Informação",
        "12.651/2012" to "Código Florestal",
        "12.965/2014" to "Marco Civil da Internet",
        "13.105/2015" to "Código de Processo Civil",
        "13.146/2015" to "Estatuto da Pessoa com Deficiência",
        "13.709/2018" to "LGPD",
        "14.133/2021" to "Lei de Licitações",
        "14.790/2023" to "lei das bets",
        "64/1990" to "Lei de Inelegibilidades",
        "101/2000" to "Lei de Responsabilidade Fiscal",
        "214/2025" to "lei que regulamenta a reforma tributária"
    )

    fun termosDoGlossario(): List<Termo> = glossario.map { it.second }.distinctBy { it.termo }

    fun explicar(siglaTipo: String, ementa: String): Explicacao {
        val original = ementa.replace(Regex("""\s+"""), " ").trim()
        val n = normalize(original)
        if (n.isBlank()) return Explicacao("", "", emptyList(), emptyList(), "")

        val obs = mutableListOf<String>()
        var valor = ""
        val frase: String = when {
            n.startsWith("abre credito extraordinario") -> {
                valor = valorEmReais(original)
                val destino = destinoDoCredito(original)
                if (Regex("""fins? que especifica""").containsMatchIn(n)) {
                    obs += "A ementa não diz no quê o dinheiro vai ser gasto (\"para o fim que especifica\"). O destino está no texto completo."
                }
                buildString {
                    append("Libera ")
                    append(valor.ifBlank { "dinheiro" })
                    append(" fora do Orçamento já aprovado")
                    if (destino.isNotBlank()) append(" para $destino")
                    append(". O gasto pode começar antes da votação no Congresso.")
                }
            }
            n.startsWith("abre credito suplementar") || n.startsWith("abre credito especial") -> {
                valor = valorEmReais(original)
                val destino = destinoDoCredito(original)
                buildString {
                    append(if (n.startsWith("abre credito suplementar")) "Reforça o Orçamento com " else "Cria no Orçamento uma despesa nova de ")
                    append(valor.ifBlank { "um valor" })
                    if (destino.isNotBlank()) append(" para $destino")
                    append(".")
                }
            }
            n.startsWith("susta") -> {
                obs += "Não passa por sanção da Presidência: se Câmara e Senado aprovarem, o ato do governo perde o efeito."
                val alvo = Regex("""(decreto|portaria|resolucao|instrucao normativa|norma tecnica|deliberacao|ato)""")
                    .find(n)?.value
                if (alvo != null) "Tenta cancelar um${if (alvo == "portaria" || alvo == "resolucao" || alvo == "instrucao normativa" || alvo == "norma tecnica" || alvo == "deliberacao") "a" else ""} ${rotuloAto(alvo)} do governo."
                else "Tenta cancelar um ato do governo."
            }
            n.startsWith("aprova o ato que") && n.contains("radiodifusao") || n.startsWith("aprova o ato que") && Regex("""\b(radio|televisao|sons e imagens)\b""").containsMatchIn(n) -> {
                when {
                    n.startsWith("aprova o ato que renova") -> "Confirma a renovação da concessão de uma emissora de rádio ou TV."
                    n.startsWith("aprova o ato que outorga") -> "Confirma a concessão de uma emissora de rádio ou TV nova."
                    else -> "Confirma a autorização para uma emissora de rádio funcionar."
                }
            }
            n.startsWith("aprova o texto d") ->
                "Aprova um tratado ou acordo internacional que o Brasil assinou. Sem esse aval do Congresso, o país não pode ratificar."
            n.startsWith("revoga") -> "Cancela uma lei, ou um trecho dela, que hoje está valendo."
            Regex("""^(denomina|da a denominacao|da denominacao|confere a denominacao|atribui a denominacao|atribui o nome)""").containsMatchIn(n) ->
                "Dá nome a um lugar ou obra pública, como rodovia, ponte, viaduto ou prédio."
            Regex("""^institui (o|a) (dia|semana|mes|ano) """).containsMatchIn(n) ||
                Regex("""^institui,? no calendario""").containsMatchIn(n) ->
                "Cria uma data comemorativa no calendário oficial."
            n.startsWith("institui feriado") || n.startsWith("declara feriado") -> "Cria um feriado."
            n.contains("livro dos herois e heroinas da patria") ->
                "Põe um nome no Livro dos Heróis e Heroínas da Pátria. É homenagem."
            Regex("""^(confere|concede) (o|ao)? ?titulo""").containsMatchIn(n) || n.contains("titulo de capital nacional") ->
                "Dá um título honorífico. É homenagem."
            Regex("""^(declara|reconhece) .{0,80}patrimonio""").containsMatchIn(n) ->
                "Reconhece algo como patrimônio cultural ou histórico."
            n.startsWith("altera") || n.startsWith("acrescenta") || n.startsWith("modifica") || n.startsWith("inclui") -> {
                val alvo = leiAlvo(original, n, siglaTipo)
                val acrescenta = n.startsWith("acrescenta") || n.startsWith("inclui")
                when {
                    alvo == CONSTITUICAO -> if (acrescenta) "Acrescenta um trecho novo à Constituição." else "Muda o texto da Constituição."
                    alvo == ADCT -> "Mexe no ADCT, o anexo de regras transitórias da Constituição."
                    acrescenta && alvo.isNotBlank() -> "Acrescenta um trecho novo a uma lei que já existe: $alvo."
                    acrescenta -> "Acrescenta um trecho novo a uma lei que já existe."
                    alvo.isNotBlank() -> "Muda uma lei que já existe: $alvo."
                    else -> "Muda uma lei que já existe."
                }
            }
            Regex("""^(institui|cria)[ ,]""").containsMatchIn(n) -> {
                val nome = nomeCriado(original)
                if (nome.isNotBlank()) fechar("Cria $nome") else "Cria algo que ainda não existe na lei."
            }
            n.startsWith("autoriza") -> {
                val objeto = aposVerbo(original, "Autoriza")
                if (objeto.isNotBlank()) fechar("Dá permissão legal para $objeto") else "Dá uma permissão legal ao governo."
            }
            n.startsWith("proibe") || n.startsWith("veda") -> {
                val objeto = aposVerbo(original, if (n.startsWith("proibe")) "Proíbe" else "Veda")
                if (objeto.isNotBlank()) fechar("Proíbe $objeto") else "Cria uma proibição."
            }
            n.startsWith("dispoe sobre") -> {
                val tema = aposVerbo(original, "Dispõe sobre")
                if (tema.isNotBlank()) fechar("Cria regras sobre $tema") else ""
            }
            n.startsWith("estabelece") -> {
                val objeto = aposVerbo(original, "Estabelece")
                if (objeto.isNotBlank()) fechar("Define $objeto") else ""
            }
            else -> ""
        }

        when (siglaTipo) {
            "MPV", "MP" -> obs += "Medida provisória já vale desde a publicação. O Congresso tem até 120 dias para aprovar, ou ela cai."
            "PEC" -> obs += "Para passar, precisa de 308 deputados e 49 senadores a favor, em dois turnos em cada Casa."
            "PLP" -> obs += "Para passar, precisa de 257 deputados e 41 senadores a favor."
        }

        val termos = glossario.filter { (gatilho, _) -> n.contains(gatilho) }
            .map { it.second }
            .distinctBy { it.termo }

        val objetivo = objetivoDeclarado(original).takeUnless { frase.contains(it) }.orEmpty()

        return Explicacao(
            emUmaFrase = frase,
            objetivo = objetivo,
            observacoes = obs,
            termos = termos,
            valor = valor
        )
    }

    // "..., para estabelecer critérios de X." -> "estabelecer critérios de X"
    private fun objetivoDeclarado(original: String): String {
        val m = Regex("""(?:,\s*|\s)(?:para|a fim de|com o objetivo de|com a finalidade de)\s+(.+)""", RegexOption.IGNORE_CASE)
            .find(original) ?: return ""
        var t = m.groupValues[1].trim()
        // Boilerplate purposes say nothing
        if (Regex("""^(os? fins?|o fim) que especifica""", RegexOption.IGNORE_CASE).containsMatchIn(t)) return ""
        // "para" also shows up mid-sentence ("diretrizes para a prevenção");
        // a declared purpose always opens with a verb in the infinitive
        if (!Regex("""^\p{L}+(ar|er|ir|or)\b""").containsMatchIn(t)) return ""
        t = t.split(Regex("""[;,]?\s+(e\s+)?(altera|acrescenta|revoga|institui)\s+(a|o|os|as)\s+(Lei|Medida|Decreto|art)""", RegexOption.IGNORE_CASE))[0]
        t = t.substringBefore("; ")
        t = t.replace(Regex("""[;,]?\s*e d[aá] outras provid[eê]ncias\.?$""", RegexOption.IGNORE_CASE), "")
        t = t.trimEnd('.', ';', ',', ' ')
        if (t.length < 12) return ""
        if (t.length > 260) t = t.take(260).substringBeforeLast(' ').trimEnd(',', ';', ':') + "…"
        return t.replaceFirstChar { it.lowercase(Locale("pt", "BR")) }
    }

    // "no valor de R$ 6.494.296.446,00" -> "R$ 6,49 bilhões"
    private fun valorEmReais(original: String): String {
        val m = Regex("""R\$\s*([\d.]+(?:,\d{2})?)""").find(original) ?: return ""
        val numero = m.groupValues[1].replace(".", "").replace(",", ".").toDoubleOrNull() ?: return ""
        return humanizarReais(numero)
    }

    fun humanizarReais(v: Double): String {
        val br = Locale("pt", "BR")
        return when {
            v >= 1e9 -> "R$ " + "%.2f".format(br, v / 1e9).removeSuffix(",00") + (if (v < 2e9) " bilhão" else " bilhões")
            v >= 1e6 -> "R$ " + "%.1f".format(br, v / 1e6).removeSuffix(",0") + (if (v < 2e6) " milhão" else " milhões")
            else -> "R$ " + "%,.2f".format(br, v)
        }
    }

    private fun destinoDoCredito(original: String): String {
        val m = Regex("""em favor d([aoe]s?)\s+(.+?)(?:,|;)?\s+no valor""", RegexOption.IGNORE_CASE).find(original)
            ?: Regex("""em favor d([aoe]s?)\s+(.+?)(?:,\s*para|\.\s*$)""", RegexOption.IGNORE_CASE).find(original)
            ?: return ""
        val artigo = m.groupValues[1].lowercase()
        val corpo = m.groupValues[2].trim().trimEnd(',', ';')
        if (corpo.length > 160) return ""
        return when (artigo) {
            "e" -> corpo
            "o" -> "o $corpo"
            "a" -> "a $corpo"
            "os" -> "os $corpo"
            "as" -> "as $corpo"
            else -> corpo
        }
    }

    private fun rotuloAto(alvo: String) = when (alvo) {
        "resolucao" -> "resolução"
        "instrucao normativa" -> "instrução normativa"
        "norma tecnica" -> "norma técnica"
        "deliberacao" -> "deliberação"
        else -> alvo
    }

    // Which law is being changed, by nickname when the ementa gives one or we know it
    private fun leiAlvo(original: String, n: String, siglaTipo: String): String {
        if (siglaTipo == "PEC" || Regex("""^(altera|acrescenta|modifica|inclui)[^.]{0,80}(constituicao|disposicoes constitucionais transitorias)""").containsMatchIn(n)) {
            return if (n.contains("disposicoes constitucionais transitorias")) ADCT else CONSTITUICAO
        }
        val lei = Regex("""(Lei Complementar|Decreto-Lei|Lei)\s*n?[ºo°.]*\s*([\d.]+)(?:,\s*de\s*\d{1,2}[ºo°]?\s*de\s*\p{L}+\s*de\s*|,\s*de\s*|/)(\d{4})(\s*\(([^)]{3,80})\))?""", RegexOption.IGNORE_CASE)
            .find(original) ?: return ""
        val numero = "${lei.groupValues[2]}/${lei.groupValues[3]}"
        val apelido = lei.groupValues[5].trim().ifBlank { apelidos[numero].orEmpty() }
        val tipo = lei.groupValues[1].let { if (it.equals("Lei Complementar", true)) "Lei Complementar" else if (it.startsWith("Decreto", true)) "Decreto-Lei" else "Lei" }
        return if (apelido.isNotBlank()) "$apelido ($tipo $numero)" else "$tipo $numero"
    }

    // "Institui a Política Nacional de X, estabelece ..." -> "a Política Nacional de X"
    private fun nomeCriado(original: String): String {
        var t = original.replaceFirst(Regex("""^(Institui|Cria),?\s+""", RegexOption.IGNORE_CASE), "")
        t = t.replaceFirst(Regex("""^no âmbito d[oa] .+?,\s*""", RegexOption.IGNORE_CASE), "")
        t = t.split(
            Regex(
                """;|,\s+(estabelece|disp[õo]e|altera|define|cria|institui|prev[êe]|regulamenta|disciplina|destinad[oa]|fundo de natureza|com o objetivo|com a finalidade|e d[áa] outras)|\s+e\s+(d[áa] outras|disp[õo]e|altera|estabelece|define)\b|,?\s+para\s|\.\s*$""",
                RegexOption.IGNORE_CASE
            )
        )[0].trim().trimEnd(',', '.')
        if (t.length > 160) t = t.take(160).substringBeforeLast(' ').trimEnd(',', ';', ':') + "…"
        return if (t.length >= 6) t else ""
    }

    private fun aposVerbo(original: String, verbo: String): String {
        var t = original.removeRange(0, minOf(verbo.length, original.length)).trim()
        t = t.substringBefore(";").substringBefore(", e dá outras").substringBefore(" e dá outras")
        t = t.split(Regex(""",?\s+(e\s+)?(altera|acrescenta|revoga)\s+(a|o|os|as)\s+(Lei|Decreto|Medida)|,\s+para\s|,\s+com o objetivo""", RegexOption.IGNORE_CASE))[0]
        t = t.trimEnd('.', ',', ' ')
        if (t.length > 180) t = t.take(180).substringBeforeLast(' ').trimEnd(',', ';', ':') + "…"
        return t.replaceFirstChar { it.lowercase(Locale("pt", "BR")) }
    }

    // Ellipsized fragments already end the sentence
    private fun fechar(frase: String) = if (frase.endsWith("…")) frase else "$frase."

    private fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("""\p{Mn}+"""), "")
}

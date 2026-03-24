package com.trouxaapp.app.util

data class TranslatedLaw(
    val titulo: String,
    val impacto: String,
    val tipoExplicacao: String,
    val nivelEstimado: Int
)

object LawTranslator {

    // Type explanations
    private val tipoMap = mapOf(
        "PL" to "Projeto de Lei. Se aprovado, vira lei.",
        "PEC" to "Proposta de Emenda a Constituição. Quer mudar a Constituição.",
        "MP" to "Medida Provisória. Ja vale agora, o presidente criou sozinho.",
        "MPV" to "Medida Provisória. Ja vale agora, o presidente criou sozinho.",
        "PLP" to "Projeto de Lei Complementar. Regulamentá algo da Constituição.",
        "PDL" to "Projeto de Decreto Legislativo. Decisão so do Congresso."
    )

    // Keyword groups ordered by specificity (most specific first)
    private val templates = listOf(
        ImpactTemplate(
            keywords = listOf("combustível", "combustível", "gasolina", "etanol", "petróleo", "petróleo", "anp", "petrobras"),
            titulo = "Querem mexer no preço do combustível",
            impacto = "Pode afetar o preço da gasolina, etanol ou gas. O que muda no combustível muda no preço de TUDO (frete, comida, transporte).",
            nivel = 7
        ),
        ImpactTemplate(
            keywords = listOf("saúde", "saúde", "sus", "hospital", "medic", "médic", "farmacia", "farmácia", "plano de saúde", "plano de saúde", "medicament"),
            titulo = "Querem mexer na sua saúde",
            impacto = "Pode afetar o SUS, planos de saúde ou acesso a medicamentos. Saude e onde o imposto deveria voltar pra você.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("educação", "educação", "escola", "universidade", "fundeb", "professor", "ensino"),
            titulo = "Querem mexer na educação",
            impacto = "Pode afetar escolas, universidades ou salário de professor. O Brasil investe 1/3 do que países desenvolvidos investem por aluno.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("trabalh", "clt", "empreg", "salário", "salário", "previdencia", "previdência", "aposentadoria", "inss"),
            titulo = "Querem mexer no seu trabalho ou aposentadoria",
            impacto = "Pode afetar seus direitos trabalhistas, salário ou aposentadoria. Cada mudança aqui afeta seu bolso direto.",
            nivel = 7
        ),
        ImpactTemplate(
            keywords = listOf("tribut", "imposto", "icms", "ibs", "cbs", "fiscal", "taxa"),
            titulo = "Querem mexer nos seus impostos",
            impacto = "Pode mudar quanto você paga de imposto. Produtos e serviços podem ficar mais caros ou mais baratos dependendo da regulamentação.",
            nivel = 7
        ),
        ImpactTemplate(
            keywords = listOf("segurança", "segurança", "arma", "policia", "polícia", "penal", "crime", "pena", "prisao", "prisão"),
            titulo = "Querem mexer na segurança",
            impacto = "Pode mudar leis penais, acesso a armas ou atuação policial. O Brasil resolve menos de 10% dos homicídios.",
            nivel = 5
        ),
        ImpactTemplate(
            keywords = listOf("internet", "digital", "rede social", "dado", "privacidade", "lgpd", "telecomunic"),
            titulo = "Querem mexer na sua internet",
            impacto = "Pode afetar o que você faz online, seus dados pessoais ou quanto paga pela internet.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("habitação", "habitação", "moradia", "aluguel", "imóvel", "imóvel", "minha casa"),
            titulo = "Querem mexer na moradia",
            impacto = "Pode afetar preço de aluguel, financiamento ou programas habitacionais.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("transporte", "onibus", "ônibus", "metro", "metrô", "pedagio", "pedágio", "rodovia", "ferrovia"),
            titulo = "Querem mexer no transporte",
            impacto = "Pode afetar preço de passagem, pedagio ou investimento em estradas.",
            nivel = 5
        ),
        ImpactTemplate(
            keywords = listOf("eleicao", "eleição", "voto", "partido", "campanha", "eleitoral", "tse"),
            titulo = "Querem mudar regras eleitorais",
            impacto = "Pode mudar como você vota, quem pode se candidatar ou como campanhas são financiadas.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("orçamento", "orçamento", "emenda", "gasto", "despesa", "licitação", "licitação"),
            titulo = "Querem mexer no dinheiro público",
            impacto = "Pode mudar como o governo gasta SEU dinheiro. Cada real desviado e um real que não vai pra escola, hospital ou asfalto.",
            nivel = 7
        ),
        ImpactTemplate(
            keywords = listOf("meio ambiente", "desmatamento", "clima", "carbono", "ambiental"),
            titulo = "Querem mexer no meio ambiente",
            impacto = "Pode afetar desmatamento, poluição ou licenciamento ambiental. Desastre ambiental vira conta pra você.",
            nivel = 5
        ),
        ImpactTemplate(
            keywords = listOf("militar", "forcas armadas", "forças armadas", "defesa", "exercito", "exército"),
            titulo = "Querem mexer nas Forcas Armadas",
            impacto = "Pode mudar gastos militares ou privilégios das Forcas Armadas. O Brasil gasta mais com militares inativos do que com o Bolsa Familia.",
            nivel = 5
        ),
        ImpactTemplate(
            keywords = listOf("agrotoxic", "agrotóxic", "pesticid", "agricultur"),
            titulo = "Querem mexer nos agrotóxicos",
            impacto = "Pode mudar regras sobre venenos na sua comida. O Brasil e o país que mais usa agrotóxicos no mundo.",
            nivel = 6
        ),
        ImpactTemplate(
            keywords = listOf("aposta", "bet", "jogo", "loteria"),
            titulo = "Querem mexer nas apostas e bets",
            impacto = "Pode mudar regras de apostas online. As bets já movimentam bilhões e afetam famílias endividadas.",
            nivel = 5
        )
    )

    fun translate(siglaTipo: String, ementa: String): TranslatedLaw {
        val ementaLower = ementa.lowercase()
        val tipoExplicacao = tipoMap[siglaTipo] ?: "Proposta legislativa."

        // Find matching template
        for (template in templates) {
            if (template.keywords.any { keyword -> ementaLower.contains(keyword) }) {
                return TranslatedLaw(
                    titulo = template.titulo,
                    impacto = template.impacto,
                    tipoExplicacao = tipoExplicacao,
                    nivelEstimado = template.nivel
                )
            }
        }

        // Fallback: clean the ementa
        val cleanedEmenta = cleanEmenta(ementa)
        return TranslatedLaw(
            titulo = cleanedEmenta,
            impacto = "Essa lei pode te afetar. Leia a ementa original pra entender.",
            tipoExplicacao = tipoExplicacao,
            nivelEstimado = 4
        )
    }

    fun cleanEmenta(ementa: String): String {
        var cleaned = ementa

        // Remove common legal prefixes
        val prefixes = listOf(
            "Altera a Lei n[ºo°]?\\s*[\\d.]+[,/]?\\s*de\\s*\\d+\\s*de\\s*\\w+\\s*de\\s*\\d+[,.]?\\s*",
            "Altera a Lei n[ºo°]?\\s*[\\d.]+[,/]?\\s*",
            "Altera o Decreto-Lei n[ºo°]?\\s*[\\d.]+[,/]?\\s*de\\s*\\d+\\s*de\\s*\\w+\\s*de\\s*\\d+[,.]?\\s*",
            "Acrescentá dispositivo[s]?\\s*(?:ao?|à)\\s*Lei\\s*n[ºo°]?\\s*[\\d.]+[,/]?\\s*(?:de\\s*\\d+\\s*de\\s*\\w+\\s*de\\s*\\d+)?[,.]?\\s*",
            "Dispõe sobre\\s*",
            "Institui\\s*",
            "Autoriza\\s*",
            "Regulamenta\\s*",
        )

        for (prefix in prefixes) {
            cleaned = cleaned.replaceFirst(Regex(prefix, RegexOption.IGNORE_CASE), "")
        }

        // Remove trailing jargon
        cleaned = cleaned.replace(Regex("[,;]?\\s*e\\s*dá\\s*outras\\s*providências\\.?", RegexOption.IGNORE_CASE), "")
        cleaned = cleaned.replace(Regex("[,;]?\\s*e\\s*dá\\s*outras\\s*providencias\\.?", RegexOption.IGNORE_CASE), "")

        // Clean up leading connectors
        cleaned = cleaned.replaceFirst(Regex("^\\s*para\\s+", RegexOption.IGNORE_CASE), "")
        cleaned = cleaned.replaceFirst(Regex("^\\s*que\\s+", RegexOption.IGNORE_CASE), "")
        cleaned = cleaned.replaceFirst(Regex("^\\s*a\\s+fim\\s+de\\s+", RegexOption.IGNORE_CASE), "")

        cleaned = cleaned.trim()

        // Capitalize first letter
        if (cleaned.isNotEmpty()) {
            cleaned = cleaned[0].uppercaseChar() + cleaned.substring(1)
        }

        // Truncate
        if (cleaned.length > 100) {
            cleaned = cleaned.take(97) + "..."
        }

        return cleaned.ifEmpty { ementa.take(100) }
    }

    private data class ImpactTemplate(
        val keywords: List<String>,
        val titulo: String,
        val impacto: String,
        val nivel: Int
    )
}

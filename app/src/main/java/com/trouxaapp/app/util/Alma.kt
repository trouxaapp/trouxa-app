package com.trouxaapp.app.util

import com.trouxaapp.app.data.local.LeiEntity
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A alma do Trouxa.
 *
 * O app ficou neutro demais: mostrava o número e saía de fininho. Aqui é onde
 * ele fala em voz alta o que os dados já dizem. A regra continua a mesma de
 * sempre — nada é inventado —, mas fica em três camadas bem separadas, pra
 * ninguém confundir dado com opinião:
 *
 *  1. LEITURA   -> o que o projeto faz, por regra sobre o texto oficial (LeiExplicada).
 *  2. VEREDITO  -> o "e daí?", e cada veredito aponta pra o campo/regra que o sustenta
 *                  (listado em [vereditoDoProjeto] e [seloDoProjeto]).
 *  3. ESCULACHO -> frases de processo, sem número, que valem sempre. Nunca
 *                  precisam de fonte porque não afirmam fato novo: são piada
 *                  sobre como o Congresso funciona.
 *
 * Selos e vereditos curados (feed/curadoria.json) vencem os derivados: quando
 * um humano escreveu, o texto dele entra na frente.
 */
object Alma {

    /** Selo + veredito de um projeto. Os dois podem vir vazios. */
    data class Punch(val selos: List<String>, val veredito: String) {
        val vazio get() = selos.isEmpty() && veredito.isBlank()
    }

    // ============================================================
    // SELOS — etiqueta curta, sempre colada num fato verificável
    // ============================================================

    /**
     * Selos derivados só de campos reais da Câmara (sigla, situação, despacho,
     * ementa). A ordem é de gravidade: o primeiro é o que entra no card.
     */
    fun seloDoProjeto(lei: LeiEntity, valor: String = ""): List<String> {
        val n = normalize("${lei.ementa} ${lei.tituloPopular}")
        val situacao = normalize("${lei.ultimaSituacao} ${lei.despacho}")
        val selos = mutableListOf<String>()

        // Medida provisória já é lei desde a publicação (art. 62 da Constituição)
        if (lei.siglaTipo == "MPV" || lei.siglaTipo == "MP") selos += "JÁ VALE"
        if (situacao.contains("urgenc")) selos += "URGÊNCIA"
        if (n.contains("credito extraordinario")) selos += "FORA DO ORÇAMENTO"
        if (n.startsWith("susta")) selos += "MANDA DESFAZER"
        if (lei.siglaTipo == "PEC") selos += "MEXE NA CONSTITUIÇÃO"
        if (valorEstaGrande(valor)) selos += "DINHEIRO GRANDE"
        if (criaGasto(n)) selos += "CRIA GASTO"
        if (mexeNoBolso(n)) selos += "SAI DO SEU BOLSO"
        if (soHomenagem(n)) selos += "SÓ HOMENAGEM"

        return selos.distinct()
    }

    // ============================================================
    // VEREDITO — o "e daí?", amarrado no que o projeto faz
    // ============================================================

    /**
     * Cada ramo só dispara com o padrão que o sustenta. Quando nada casa, o
     * veredito fica vazio em vez de virar chute.
     */
    fun vereditoDoProjeto(lei: LeiEntity, valor: String = ""): String {
        val n = normalize(lei.ementa)
        return when {
            n.startsWith("abre credito extraordinario") ->
                "O dinheiro pode começar a ser gasto antes de qualquer deputado votar. " +
                    "O voto vem depois, e só pra confirmar."

            n.contains("credito extraordinario") ->
                "É dinheiro fora do Orçamento aprovado, liberado por medida provisória."

            n.startsWith("susta") ->
                "Aqui o Congresso tenta desfazer o que o governo já fez por decreto ou portaria."

            n.startsWith("abre credito suplementar") ->
                "Não é dinheiro novo: é reforço numa despesa que o Orçamento já tinha e não ia dar conta."

            lei.siglaTipo == "PEC" ->
                "Mexe na Constituição. Só passa com 308 deputados e 49 senadores, em dois turnos em cada Casa."

            n.startsWith("revoga") ->
                "Cancela uma lei que hoje está valendo. Voltar atrás dá mais trabalho que criar."

            criaGasto(n) && valor.isNotBlank() ->
                "$valor saindo do caixa de todo mundo. Quem paga não é quem assina."

            criaGasto(n) ->
                "Cria despesa nova. No Orçamento, despesa nova é sempre conta pra depois."

            mexeNoBolso(n) ->
                "Toca direto no que sai do seu bolso: imposto, preço ou salário."

            soHomenagem(n) ->
                "Passa fácil, vira foto e ninguém lembra. Não muda a vida de ninguém."

            n.contains("radiodifusao") ->
                "O governo concede a emissora, mas quem dá o aval final é o Congresso."

            n.startsWith("aprova o texto d") ->
                "Tratado assinado pelo governo só vale de verdade depois desse aval do Congresso."

            else -> ""
        }
    }

    /** Junta selo derivado + veredito derivado, deixando a curadoria vencer. */
    fun punchDoProjeto(
        lei: LeiEntity,
        valor: String = "",
        seloCurado: String = "",
        vereditoCurado: String = ""
    ): Punch {
        val selos = if (seloCurado.isNotBlank()) listOf(seloCurado) else seloDoProjeto(lei, valor)
        val veredito = vereditoCurado.ifBlank { vereditoDoProjeto(lei, valor) }
        return Punch(selos, veredito)
    }

    // ============================================================
    // DINHEIRO — vereditos do painel do dia
    // ============================================================

    /** Crédito extraordinário somado por ano: a frase que o gráfico não diz. */
    fun vereditoCredito(ano: Int, total: Double, quantidade: Int): String = when {
        quantidade == 0 -> "Nenhuma MP de crédito extraordinário no ano."
        total >= 100e9 -> "Só em $ano: ${LeiExplicada.humanizarReais(total)} em $quantidade MPs, " +
            "liberados fora do Orçamento que você viu ser votado."
        total >= 10e9 -> "${LeiExplicada.humanizarReais(total)} fora do Orçamento em $ano, " +
            "em $quantidade MPs. O gasto começou antes da votação."
        else -> "Dinheiro fora do Orçamento, mesmo assim: ${LeiExplicada.humanizarReais(total)} em $quantidade MPs."
    }

    /**
     * O mínimo oficial contra o mínimo que o DIEESE calcula pra uma família
     * viver. Os dois números são oficiais; a razão entre eles é aritmética.
     */
    fun vereditoSalarioMinimo(minimo: Double, necessario: Double): String {
        if (minimo <= 0 || necessario <= 0) return ""
        val pct = (minimo / necessario) * 100
        // Os parênteses importam: sem eles o format() cola só no segundo
        // literal e o %.0f%% sai cru na tela.
        return (
            "O mínimo oficial cobre só %.0f%% do que o DIEESE calcula que uma família precisa pra viver. " +
                "A diferença quem cobre é você."
            ).format(Locale("pt", "BR"), pct)
    }

    // ============================================================
    // ESCULACHO — frases de processo, sem número, sempre verdadeiras
    // ============================================================

    private val esculachos = listOf(
        "Quem paga a conta não é quem assina embaixo.",
        "Urgência é o jeito educado de dizer “vota sem ler”.",
        "Todo ano tem mais uma lei. Todo ano tem a mesma promessa.",
        "No papel é temporário. Na prática, ninguém revoga.",
        "Crédito extraordinário: o dinheiro sai, o voto chega depois.",
        "Comissão é onde o projeto dorme. Plenário é onde ele acorda no susto.",
        "Ementa é o que eles querem que você leia. O texto é o que vale.",
        "Decreto é rápido, lei é devagar. Adivinha qual move dinheiro.",
        "Emenda é o Orçamento de verdade; o resto é expectativa.",
        "Se te explicaram em 10 segundos, faltou pedaço. Em 10 minutos, é lei.",
        "Dar nome a ponte e renovar rádio passa fácil, e sai na foto.",
        "Ninguém lê 40 páginas. Nem eles. Alguém lê por você: é esse o app."
    )

    /** Rotação determinística: muda a cada dia, estável dentro do dia. */
    fun esculacho(dia: Long = hoje()): String {
        val n = esculachos.size
        val indice = (((dia % n) + n) % n).toInt()
        return esculachos[indice]
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private fun hoje(): Long = try {
        val d = SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().time)
        d.toLong()
    } catch (_: Exception) {
        (System.currentTimeMillis() / 86_400_000L)
    }

    private fun valorEstaGrande(valor: String): Boolean {
        val m = Regex("""R\$\s*([\d.]+)(?:\s*(bilh|milh|trilh))?""").find(valor) ?: return false
        val n = m.groupValues[1].replace(".", "").replace(",", ".").toDoubleOrNull() ?: return false
        return when (m.groupValues[2]) {
            "bilh" -> n >= 1
            "trilh" -> true
            "milh" -> n >= 1000
            else -> n >= 1_000_000_000
        }
    }

    // Verbo de criação + objeto que custa dinheiro (fundo, auxílio, piso, cota...)
    private fun criaGasto(n: String): Boolean {
        val cria = Regex("""(^|\. )(institui|cria|dispoe sobre a criacao)""").containsMatchIn(n)
        val gasto = Regex("""(fundo|auxilio|beneficio|piso|salario|bolsa|subsidio|subvencao|remuneracao|isencao|aposentadoria|pensao|juros|financiamento)""")
            .containsMatchIn(n)
        return cria && gasto
    }

    private fun mexeNoBolso(n: String): Boolean = Regex(
        """(imposto|tributo|tributari|aliquota|contribuicao|tarifa|preco|salario minimo|reajuste|correcao monetaria)"""
    ).containsMatchIn(n)

    private fun soHomenagem(n: String): Boolean = Regex(
        """^(denomina|da a denominacao|da denominacao|confere a denominacao|atribui a denominacao|atribui o nome|institui (o|a) (dia|semana|mes|ano) |institui,? no calendario|institui feriado|declara feriado)"""
    ).containsMatchIn(n)

    private fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(Locale("pt", "BR")), Normalizer.Form.NFD)
            .replace(Regex("""\p{Mn}+"""), "")
}

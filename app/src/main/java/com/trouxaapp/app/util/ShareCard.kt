package com.trouxaapp.app.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * Renders shareable 1080x1920 story cards on device. A card has to work on
 * its own, out of context, in a story or a WhatsApp chat: the figure, what it
 * is, the source and the reference date always travel together. Nothing on a
 * card is computed here; it only lays out data the caller already has.
 *
 * Content stays inside y 260..1600 so the Instagram story chrome (profile
 * row on top, reply bar at the bottom) never covers the source line.
 */
object ShareCard {

    data class Conteudo(
        val rotulo: String,          // small caps label, e.g. "A CESTA BÁSICA DO MÊS"
        val numero: String,          // big red figure; may be blank
        val titulo: String,          // bold line under the figure
        val texto: String,           // body
        val fonte: String,           // "DIEESE, cesta básica de São Paulo"
        val fonteUrl: String,
        val dataReferencia: String   // already formatted, e.g. "30/04/2026"
    )

    private const val W = 1080
    private const val H = 1920
    private const val MARGEM = 96f
    private const val TOPO = 260f
    private const val LIMITE = 1600f

    private val FUNDO = Color.parseColor("#0A0A0A")
    private val VERMELHO = Color.parseColor("#DC2626")
    private val BRANCO = Color.parseColor("#FFFFFF")
    private val CINZA = Color.parseColor("#9CA3AF")
    private val CINZA_ESCURO = Color.parseColor("#6B7280")
    private val VERDE = Color.parseColor("#22C55E")
    private val LINHA = Color.parseColor("#2A2A2A")

    suspend fun compartilhar(context: Context, conteudo: Conteudo, legenda: String) {
        val uri = withContext(Dispatchers.IO) { salvar(context, render(conteudo)) }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, legenda)
            clipData = ClipData.newRawUri(null, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { context.startActivity(Intent.createChooser(send, null)) }
    }

    fun render(c: Conteudo): Bitmap {
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(FUNDO)
        val largura = (W - 2 * MARGEM).toInt()

        // Brand, top left, small: the data is the protagonist, not us
        val marcaPequena = textPaint(CINZA_ESCURO, 30f, Typeface.MONOSPACE)
        val marca = textPaint(VERMELHO, 44f, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD))
        canvas.drawText("te fizeram de", MARGEM, TOPO, marcaPequena)
        canvas.drawText("TROUXA", MARGEM, TOPO + 50f, marca)

        var y = TOPO + 170f

        if (c.rotulo.isNotBlank()) {
            val rotulo = textPaint(CINZA, 34f, Typeface.create("sans-serif-medium", Typeface.NORMAL))
            rotulo.letterSpacing = 0.12f
            y = desenhar(canvas, c.rotulo.uppercase(PT_BR), rotulo, largura, y, maxLinhas = 2) + 36f
        }

        if (c.numero.isNotBlank()) {
            val numero = textPaint(VERMELHO, 0f, Typeface.create("sans-serif-condensed", Typeface.BOLD))
            // Non-breaking space keeps "R$" glued to its value when it wraps
            val valor = c.numero.replace("R$ ", "R$\u00A0")
            numero.textSize = tamanhoQueCabe(valor, numero, largura.toFloat(), max = 190f, min = 110f)
            y = desenhar(canvas, valor, numero, largura, y, maxLinhas = 2, entrelinha = 0.9f) + 40f
        }

        if (c.titulo.isNotBlank()) {
            val titulo = textPaint(BRANCO, 56f, Typeface.create("sans-serif", Typeface.BOLD))
            y = desenhar(canvas, c.titulo, titulo, largura, y, maxLinhas = 5) + 36f
        }

        // Footer is laid out first, from the bottom up, so the body knows how
        // much room it has and gets ellipsized instead of covering the source.
        val fontePaint = textPaint(VERDE, 34f, Typeface.create("sans-serif-medium", Typeface.NORMAL))
        val metaPaint = textPaint(CINZA_ESCURO, 30f, Typeface.DEFAULT)
        val fonteLayout = layout("Fonte: ${c.fonte}", fontePaint, largura, maxLinhas = 3)
        val meta = buildString {
            val dominio = dominio(c.fonteUrl)
            if (dominio.isNotBlank()) append(dominio)
            if (c.dataReferencia.isNotBlank()) {
                if (isNotEmpty()) append("  ·  ")
                append("dado de ${c.dataReferencia}")
            }
        }
        val metaLayout = layout(meta, metaPaint, largura, maxLinhas = 2)
        val rodapeAltura = fonteLayout.height + 16f + metaLayout.height
        val rodapeTopo = LIMITE - rodapeAltura

        if (c.texto.isNotBlank()) {
            val corpo = textPaint(Color.parseColor("#E5E5E5"), 42f, Typeface.DEFAULT)
            val espaco = rodapeTopo - 64f - y
            val alturaLinha = corpo.fontSpacing * 1.15f
            val linhas = (espaco / alturaLinha).toInt()
            if (linhas > 0) desenhar(canvas, c.texto, corpo, largura, y, maxLinhas = linhas, entrelinha = 1.15f)
        }

        val divisor = Paint().apply { color = LINHA; strokeWidth = 2f }
        canvas.drawLine(MARGEM, rodapeTopo - 32f, W - MARGEM, rodapeTopo - 32f, divisor)
        canvas.save()
        canvas.translate(MARGEM, rodapeTopo)
        fonteLayout.draw(canvas)
        canvas.translate(0f, fonteLayout.height + 16f)
        metaLayout.draw(canvas)
        canvas.restore()

        val convite = textPaint(CINZA_ESCURO, 30f, Typeface.DEFAULT)
        convite.textAlign = Paint.Align.CENTER
        canvas.drawText("Dado público. Confira a fonte.", W / 2f, LIMITE + 90f, convite)

        return bmp
    }

    private fun salvar(context: Context, bmp: Bitmap): Uri {
        val dir = File(context.cacheDir, "cards").apply { mkdirs() }
        // One file at a time is enough; old cards are never reused
        dir.listFiles()?.forEach { it.delete() }
        val file = File(dir, "trouxa_${System.currentTimeMillis()}.png")
        file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bmp.recycle()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun textPaint(cor: Int, tamanho: Float, typeface: Typeface) =
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = cor
            textSize = tamanho
            this.typeface = typeface
        }

    private fun layout(
        texto: String,
        paint: TextPaint,
        largura: Int,
        maxLinhas: Int,
        entrelinha: Float = 1f
    ): StaticLayout = StaticLayout.Builder.obtain(texto, 0, texto.length, paint, largura)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(0f, entrelinha)
        .setMaxLines(maxLinhas)
        .setEllipsize(TextUtils.TruncateAt.END)
        .build()

    /** Draws at y (top) and returns the y right below the block. */
    private fun desenhar(
        canvas: Canvas,
        texto: String,
        paint: TextPaint,
        largura: Int,
        y: Float,
        maxLinhas: Int,
        entrelinha: Float = 1f
    ): Float {
        val l = layout(texto, paint, largura, maxLinhas, entrelinha)
        canvas.save()
        canvas.translate(MARGEM, y)
        l.draw(canvas)
        canvas.restore()
        return y + l.height
    }

    // Shrinks the figure until it fits on one line ("PL 5709/2026" split in
    // two reads badly). Below the floor it wraps, but only between words, so
    // "R$ 7.612,49" never breaks in the middle of the number.
    private fun tamanhoQueCabe(texto: String, paint: TextPaint, largura: Float, max: Float, min: Float): Float {
        var t = max
        while (t > min) {
            paint.textSize = t
            if (paint.measureText(texto) <= largura) break
            t -= 6f
        }
        return t
    }

    private fun dominio(url: String): String =
        runCatching { Uri.parse(url).host.orEmpty().removePrefix("www.").removePrefix("www12.") }
            .getOrDefault("")

    private val PT_BR = Locale("pt", "BR")
}

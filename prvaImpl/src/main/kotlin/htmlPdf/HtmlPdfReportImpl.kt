package htmlPdf

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import spec.*
import java.io.OutputStream
import java.io.StringReader

private object HtmlRenderer {
    fun render(report: Report): String {
        return buildString {
            append("<!DOCTYPE html><html><head><style>")
            append("body { font-family: sans-serif; }")
            append("table { border-collapse: collapse; width: 100%; }")
            append("th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }")
            append("th { background-color: #f2f2f2; }")
            append(".summary-list { list-style-type: circle; padding-left: 20px; }")
            append("</style></head><body>")
            report.elements.forEach { element ->
                when (element) {
                    is Title -> append("<h1 style='${styleMapToString(element.style)}'>${element.text}</h1>")
                    is Table -> append(renderTable(element))
                    is Summary -> append(renderSummary(element))
                }
                append("<br/>")
            }
            append("</body></html>")
        }
    }

    private fun renderTable(table: Table): String = buildString {
        append("<table style='${styleMapToString(table.style)}'>")
        table.header?.let {
            append("<thead><tr>")
            it.forEach { h -> append("<th>$h</th>") }
            append("</tr></thead>")
        }
        append("<tbody>")
        table.rows.forEach { row ->
            append("<tr>")
            row.forEach { cell -> append("<td>$cell</td>") }
            append("</tr>")
        }
        append("</tbody></table>")
    }

    private fun renderSummary(summary: Summary): String = buildString {
        append("<div style='${styleMapToString(summary.style)}'>")
        append("<h3>Summary</h3>")
        append("<ul class='summary-list'>")
        summary.items.forEach { item ->
            append("<li><b>${item.label}:</b> ${item.value}</li>")
        }
        append("</ul></div>")
    }

    private fun styleMapToString(style: Map<String, String>): String {
        return style.entries.joinToString(separator = ";") { "${it.key}: ${it.value}" }
    }
}

class HtmlReportImpl : ReportGenerator {
    override fun getFormat(): String = "html"

    override fun generate(report: Report, stream: OutputStream) {
        val htmlContent = HtmlRenderer.render(report)
        stream.bufferedWriter().use { it.write(htmlContent) }
    }
}

class PdfReportImpl : ReportGenerator {
    override fun getFormat(): String = "pdf"

    override fun generate(report: Report, stream: OutputStream) {
        val htmlContent = HtmlRenderer.render(report)

        PdfRendererBuilder()
            .withHtmlContent(htmlContent, null) // Corrected line
            .toStream(stream)
            .run()
    }
}
package markdown

import spec.*
import java.io.OutputStream

class MarkdownReportImpl : ReportGenerator {
    override fun getFormat(): String = "md"

    override fun generate(report: Report, stream: OutputStream) {
        stream.bufferedWriter().use { writer ->
            report.elements.forEach { element ->
                when (element) {
                    is Title -> writer.write(renderTitle(element))
                    is Table -> writer.write(renderTable(element))
                    is Summary -> writer.write(renderSummary(element))
                }
                writer.newLine()
                writer.newLine()
            }
        }
    }

    private fun renderTitle(title: Title): String {
        return "# ${title.text}\n"
    }

    private fun renderSummary(summary: Summary): String {
        return buildString {
            append("### Summary\n")
            summary.items.forEach { item ->
                append("* **${item.label}:** ${item.value}\n")
            }
        }
    }

    private fun renderTable(table: Table): String {
        return buildString {
            // Render header
            table.header?.let {
                append("| ")
                append(it.joinToString(" | "))
                append(" |\n")

                append("|")
                append(it.joinToString("|") { "---" })
                append("|\n")
            }

            // Render rows
            table.rows.forEach { row ->
                append("| ")
                append(row.joinToString(" | "))
                append(" |\n")
            }
        }
    }
}
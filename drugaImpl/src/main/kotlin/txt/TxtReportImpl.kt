package txt

import spec.*
import java.io.OutputStream

class TxtReportImpl : ReportGenerator {
    override fun getFormat(): String = "txt"

    override fun generate(report: Report, stream: OutputStream) {
        stream.bufferedWriter().use { writer ->
            report.elements.forEach { element ->
                when (element) {
                    is Title -> renderTitle(element, writer)
                    is Table -> renderTable(element, writer)
                    is Summary -> renderSummary(element, writer)
                }
                writer.newLine()
                writer.newLine()
            }
        }
    }

    private fun renderTitle(title: Title, writer: java.io.BufferedWriter) {
        writer.write(title.text)
        writer.newLine()
    }

    private fun renderSummary(summary: Summary, writer: java.io.BufferedWriter) {
        writer.write("Summary:")
        writer.newLine()
        summary.items.forEach { item ->
            // Format as a simple list. The value can be of any type, so we convert it to string.
            writer.write(" - ${item.label}: ${item.value}")
            writer.newLine()
        }
    }

    private fun renderTable(table: Table, writer: java.io.BufferedWriter) {
        val columnWidths = table.header?.map { it.length }?.toMutableList() ?: mutableListOf()

        val columnCount = table.rows.firstOrNull()?.size ?: table.header?.size ?: 0
        while (columnWidths.size < columnCount) {
            columnWidths.add(0)
        }

        table.rows.forEach { row ->
            row.forEachIndexed { index, cell ->
                columnWidths[index] = maxOf(columnWidths[index], cell.toString().length)
            }
        }

        val format = columnWidths.joinToString(" | ") { "%-${it}s" } + "\n"

        table.header?.let {
            writer.write(String.format(format, *it.toTypedArray()))
            val separator = columnWidths.joinToString("-+-") { "-".repeat(it) } + "\n"
            writer.write(separator)
        }

        table.rows.forEach { row ->
            writer.write(String.format(format, *row.map { it.toString() }.toTypedArray()))
        }
    }
}
package testApp

import calculations.Calculator
import spec.*
import java.io.File
import java.util.ServiceLoader

fun main(rawArgs: Array<String>) {
    println("Report Generator v1.0")

    val generators = ServiceLoader.load(ReportGenerator::class.java).toList()
    if (generators.isEmpty()) {
        println("ERROR: No report generator implementations found. Check your build dependencies.")
        return
    }

    val availableFormats = generators.map { it.getFormat() }.joinToString(", ")
    println("Available formats: $availableFormats")

    val args = Arguments(rawArgs)

    val chosenGenerator = generators.find { it.getFormat().equals(args.format, ignoreCase = true) }
    if (chosenGenerator == null) {
        println("ERROR: Format '${args.format}' is not supported. Please choose from: $availableFormats")
        return
    }

    try {
        println("Loading data from ${args.source}...")
        val tableData = DataLoader.load(args.source, args.query)
        val table = Table(header = tableData.header, rows = tableData.rows)
        println("Data loaded successfully: ${table.rows.size} rows found.")

        val summaryItems = mutableListOf<SummaryItem>()

        summaryItems.add(
            SummaryItem(
                label = "Total Records",
                value = table.rows.size
            )
        )

        val firstColumn = table.header?.firstOrNull()
        if (firstColumn != null) {
            summaryItems.add(
                SummaryItem(
                    label = "Count of records in '$firstColumn'",
                    value = Calculator.calculate(table, Calculation(CalculationType.COUNT, firstColumn))
                )
            )
        }

        val report = Report(
            elements = listOf(
                Title(text = "Generated Report: ${args.source.substringAfterLast('/')}"),
                table,
                Summary(items = summaryItems)
            )
        )

        val outputFile = File(args.output)
        println("Generating report with format '${chosenGenerator.getFormat()}' to ${outputFile.absolutePath}...")
        outputFile.outputStream().use { stream ->
            chosenGenerator.generate(report, stream)
        }

        println("Report generated successfully!")

    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}
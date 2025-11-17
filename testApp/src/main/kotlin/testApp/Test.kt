package testApp

import calculations.Calculator
import spec.*
import java.io.File
import java.util.ServiceLoader

fun main(rawArgs: Array<String>) {
    println("Report Generator v1.0")

    // 1. DISCOVER available report generators using ServiceLoader
    val generators = ServiceLoader.load(ReportGenerator::class.java).toList()
    if (generators.isEmpty()) {
        println("ERROR: No report generator implementations found. Check your build dependencies.")
        return
    }

    val availableFormats = generators.map { it.getFormat() }.joinToString(", ")
    println("Available formats: $availableFormats")

    // 2. PARSE user command-line arguments
    val args = Arguments(rawArgs)

    // 3. SELECT the generator that matches the user's request
    val chosenGenerator = generators.find { it.getFormat().equals(args.format, ignoreCase = true) }
    if (chosenGenerator == null) {
        println("ERROR: Format '${args.format}' is not supported. Please choose from: $availableFormats")
        return
    }

    try {
        // 4. LOAD data from the specified source
        println("Loading data from ${args.source}...")
        val tableData = DataLoader.load(args.source, args.query)
        val table = Table(header = tableData.header, rows = tableData.rows)
        println("Data loaded successfully: ${table.rows.size} rows found.")

        // 5. BUILD the report model.
        val summaryItems = mutableListOf<SummaryItem>()

        // THE FIX IS HERE:
        // For a simple COUNT of all records, it's safer and more direct to just count the rows.
        // This works even if there is no header.
        summaryItems.add(
            SummaryItem(
                label = "Total Records",
                value = table.rows.size
            )
        )

        // Example for adding another calculation that DOES require a header column.
        // This is the safe way to do it.
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
                Summary(items = summaryItems) // Use the list of items we built.
            )
        )

        // 6. GENERATE the report and save it to a file
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
package testApp

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class Arguments(args: Array<String>) {
    private val parser = ArgParser("report-generator")

    val source by parser.option(
        ArgType.String,
        shortName = "s",
        fullName = "source",
        description = "Data source: path to a .csv file or a database JDBC URL"
    ).required()

    val format by parser.option(
        ArgType.String,
        shortName = "f",
        fullName = "format",
        description = "The output report format (e.g., txt, pdf, html, md)"
    ).required()

    val output by parser.option(
        ArgType.String,
        shortName = "o",
        fullName = "output",
        description = "Path to the output file (e.g., ./report.pdf)"
    ).required()

    val query by parser.option(
        ArgType.String,
        shortName = "q",
        fullName = "query",
        description = "SQL query to execute if the source is a database"
    )

    init {
        parser.parse(args)
    }
}
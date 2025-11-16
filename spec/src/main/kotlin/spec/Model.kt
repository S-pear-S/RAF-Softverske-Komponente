package spec

/**
 * Represents a full report, which consists of a list of elements.
 * This structure allows a single report file to contain multiple sections (e.g., multiple tables with titles).
 */
data class Report(
    val elements: List<ReportElement>
)

/**
 * A sealed class representing any possible element within a report.
 */
sealed class ReportElement

/**
 * Represents a title section in the report.
 * @param text The content of the title.
 * @param style A map for optional formatting hints (e.g., "font-weight" to "bold").
 */
data class Title(
    val text: String,
    val style: Map<String, String> = emptyMap()
) : ReportElement()

/**
 * Represents a table of data in the report.
 * @param header An optional list of column names.
 * @param rows The data, represented as a list of rows, where each row is a list of cell values.
 * @param style A map for optional formatting hints.
 */
data class Table(
    val header: List<String>?,
    val rows: List<List<Any>>,
    val style: Map<String, String> = emptyMap()
) : ReportElement()

/**
 * Represents a summary section at the end of a report segment.
 * @param items A list of labeled values or calculations.
 * @param style A map for optional formatting hints.
 */
data class Summary(
    val items: List<SummaryItem>,
    val style: Map<String, String> = emptyMap()
) : ReportElement()

/**
 * A single item within a summary, consisting of a label and a value.
 */
data class SummaryItem(
    val label: String,
    val value: Any
)

/**
 * Enum for defining the types of calculations that can be performed on table columns.
 */
enum class CalculationType {
    COUNT, SUM, AVERAGE, MIN, MAX
}

/**
 * Represents a calculation to be performed, which can be used as a value in a SummaryItem.
 * @param type The type of calculation.
 * @param column The name of the column to perform the calculation on.
 * @param condition An optional lambda for conditional calculations (e.g., COUNT where).
 */
data class Calculation(
    val type: CalculationType,
    val column: String,
    val condition: (Any) -> Boolean = { true }
)
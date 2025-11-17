package calculations

import spec.Calculation
import spec.CalculationType
import spec.Table
import java.lang.IllegalArgumentException

/**
 * A singleton utility object for performing calculations on report data.
 */
object Calculator {

    /**
     * The main entry point for all calculations. It takes a table and a calculation request,
     * validates the input, and dispatches to the appropriate private function.
     *
     * @param table The Table object containing the data.
     * @param calculation The Calculation object describing the operation to perform.
     * @return The result of the calculation. Can be Int, Double, or another type for MIN/MAX.
     * @throws IllegalArgumentException if the specified column does not exist in the table header.
     */
    fun calculate(table: Table, calculation: Calculation): Any {
        val columnIndex = table.header?.indexOf(calculation.column)
            ?: throw IllegalArgumentException("Column '${calculation.column}' not found. Available columns: ${table.header}")

        val columnValues = table.rows.mapNotNull { row -> row.getOrNull(columnIndex) }

        val filteredValues = columnValues.filter(calculation.condition)

        return when (calculation.type) {
            CalculationType.COUNT -> count(filteredValues)
            CalculationType.SUM -> sum(filteredValues)
            CalculationType.AVERAGE -> average(filteredValues)
            CalculationType.MIN -> min(filteredValues) ?: "N/A"
            CalculationType.MAX -> max(filteredValues) ?: "N/A"
        }
    }

    private fun count(values: List<Any>): Int = values.size

    private fun sum(values: List<Any>): Double {
        return values.sumOf { it.toString().toDoubleOrNull() ?: 0.0 }
    }

    private fun average(values: List<Any>): Double {
        val numericValues = values.mapNotNull { it.toString().toDoubleOrNull() }
        if (numericValues.isEmpty()) {
            return 0.0
        }
        return numericValues.sum() / numericValues.size
    }

    private fun min(values: List<Any>): Any? {
        val numericValues = values.mapNotNull { it.toString().toDoubleOrNull() }
        if (numericValues.isNotEmpty()) {
            return numericValues.minOrNull()
        }
        val stringValues = values.filterIsInstance<String>()
        return stringValues.minOrNull()
    }

    private fun max(values: List<Any>): Any? {
        val numericValues = values.mapNotNull { it.toString().toDoubleOrNull() }
        if (numericValues.isNotEmpty()) {
            return numericValues.maxOrNull()
        }
        val stringValues = values.filterIsInstance<String>()
        return stringValues.maxOrNull()
    }
}
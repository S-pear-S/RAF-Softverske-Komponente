package testApp

import com.opencsv.CSVReader
import java.io.FileReader
import java.sql.DriverManager

data class TableData(val header: List<String>, val rows: List<List<String>>)

object DataLoader {

    fun load(source: String, query: String? = null): TableData {
        return when {
            source.endsWith(".csv") -> loadFromCsv(source)
            source.startsWith("jdbc:") -> {
                requireNotNull(query) { "A --query must be provided for a database source." }
                loadFromDatabase(source, query)
            }
            else -> throw IllegalArgumentException("Unsupported source type: $source")
        }
    }

    private fun loadFromCsv(filePath: String): TableData {
        CSVReader(FileReader(filePath)).use { csvReader ->
            val records = csvReader.readAll()
            if (records.isEmpty()) return TableData(emptyList(), emptyList())
            val header = records[0].toList()
            val rows = records.drop(1).map { it.toList() }
            return TableData(header, rows)
        }
    }

    private fun loadFromDatabase(jdbcUrl: String, query: String): TableData {
        // NOTE: For a real app, you would pass user/password securely.
        DriverManager.getConnection(jdbcUrl).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(query).use { resultSet ->
                    val metadata = resultSet.metaData
                    val header = (1..metadata.columnCount).map { metadata.getColumnName(it) }

                    val rows = mutableListOf<List<String>>()
                    while (resultSet.next()) {
                        val row = (1..metadata.columnCount).map { resultSet.getString(it) }
                        rows.add(row)
                    }
                    return TableData(header, rows)
                }
            }
        }
    }
}
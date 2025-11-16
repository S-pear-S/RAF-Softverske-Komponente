package spec

import java.io.OutputStream

/**
 * The main interface for a report generator service.
 * Implementations of this interface are responsible for creating a report
 * in a specific format (e.g., TXT, HTML, PDF).
 */
interface ReportGenerator {
    /**
     * Generates a report based on the provided data model and writes it to an output stream.
     * @param report The data model of the report to be generated.
     * @param stream The output stream to write the generated report to.
     */
    fun generate(report: Report, stream: OutputStream)

    /**
     * Returns the unique format name supported by this generator (e.g., "txt", "html", "pdf").
     */
    fun getFormat(): String
}
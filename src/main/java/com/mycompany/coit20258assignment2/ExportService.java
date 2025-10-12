package com.mycompany.coit20258assignment2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

/** Tiny CSV writer for exporting lists. */
public class ExportService {

    /**
     * Write a list of CSV lines to a file.
     * Expects each string in {@code lines} to already be a CSV row (comma-separated, quoted as needed).
     *
     * @param file  target path for the CSV file
     * @param lines list of CSV rows to write (in order)
     * @throws RuntimeException if any I/O error occurs
     */
    public static void writeCsv(Path file, List<String> lines){
        // Use try-with-resources to ensure the writer is always closed (even on exceptions).
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))){
            // Write each line followed by a newline to form the CSV file content.
            for (String s: lines){
                bw.write(s);      // write the CSV row exactly as provided
                bw.newLine();     // platform-appropriate newline
            }
        } catch (Exception e){
            // Wrap and rethrow as unchecked to signal failure to callers without forcing checked handling.
            throw new RuntimeException("CSV export failed: " + e.getMessage(), e);
        }
    }
}

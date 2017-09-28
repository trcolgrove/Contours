package edu.tufts.contours.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Simple Class to write CSV files
 *
 * Created by Thomas on 10/14/16.
 */
public class CSVWriter {

    ArrayList<String> headers;
    FileWriter fileWriter;

    private void writeRowString(String row) throws IOException {
        fileWriter.write(row);
        fileWriter.write(System.getProperty("line.separator"));
        fileWriter.flush();
    }

    private String join(ArrayList<String> a, String separator) {
        StringBuilder sb = new StringBuilder();

        for(String s: a) {
            sb.append(s);
            sb.append(separator);
        }

        return sb.substring(0, sb.length() - 1);
    }

    public CSVWriter(ArrayList<String> headers, File outFile, boolean append) throws IOException {
        this.headers = headers;
        this.fileWriter = new FileWriter(outFile, append);
    }

    public void close() throws IOException {
        fileWriter.close();
    }

    public void writeHeaders(ArrayList<String> headers) throws IOException {
        String csvRow = join(headers, ",");
        writeRowString(csvRow);
    }

    public void writeRow(Map<String, String> rowData) throws IOException {
        ArrayList<String> row = new ArrayList<>();

        for(String header: headers) {
            String data = rowData.get(header);
            row.add(data.replace(",",""));
        }

        String csvRow = join(row, ",");
        writeRowString(csvRow);
    }
}

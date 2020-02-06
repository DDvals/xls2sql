package danzisoft.xls2sql;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

public class Xls2sql {
    public static void main(final String[] args) {

        final var options = new Options();
        final var cliInput = new Option("i", "inputFile", true, "Xsl filepath");
        cliInput.setRequired(true);
        options.addOption(cliInput);

        final var cliQuery = new Option("q", "query", true, "Query");
        cliQuery.setRequired(true);
        options.addOption(cliQuery);

        final var cliTrim = new Option("t", "trim", false, "Trim values from xsl");
        options.addOption(cliTrim);

        final CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = cliParser.parse(options, args);
        } catch (final ParseException e) {
            System.err.println("Error parsing cli argument " + e.getMessage());
            System.exit(-1);
        }

        boolean trim;
        String query;
        String xslPath;

        if (cmd.hasOption("t"))
            trim = true;
        else
            trim = false;

        query = cmd.getOptionValue("q");
        xslPath = cmd.getOptionValue("i");

        System.out.println("Query: " + query);

        FileInputStream file = null;
        Workbook workbook = null;

        try {
            file = new FileInputStream(new File(xslPath));
            workbook = new XSSFWorkbook(file);
        } catch (final FileNotFoundException notFound) {
            System.err.println("File not found " + notFound.getMessage());
            System.exit(-1);
        } catch (final IOException ioEx) {
            System.err.println("Error reading input file " + ioEx.getMessage());
            System.exit(-1);
        }

        final Sheet sheet = workbook.getSheetAt(0);
        final String[] queryColumns = StringUtils.substringsBetween(query, "[$", "]");

        if (queryColumns == null || queryColumns.length == 0) {
            System.out.println("No column specified");
            System.exit(0);
        }

        final List<String> queries = new ArrayList<>();

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            String q = new String(query);
            final var row = sheet.getRow(i);

            for (int c = 0; c < queryColumns.length; c++) {
                final var cell = row.getCell(Integer.parseInt(queryColumns[c]));
                var cellValue = cell.getStringCellValue();
                if (cellValue == null)
                    cellValue = "";

                if (trim)
                    cellValue = cellValue.trim();

                q = q.replaceAll("\\[\\$" + queryColumns[c] + "\\]", cellValue);
            }

            queries.add(q);
        }

        for (final String str : queries)
            System.out.println(str);
    }
}

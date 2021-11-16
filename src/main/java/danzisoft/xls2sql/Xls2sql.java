package danzisoft.xls2sql;

import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Xls2sql {
    public static void main(final String[] args) {

        final Options options = buildCliOptions();
        final CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = cliParser.parse(options, args);
        } catch (final ParseException e) {
            System.err.println("Error parsing cli argument " + e.getMessage());
            System.exit(-1);
        }

        Xls2sql xls2sql = new Xls2sql();
        xls2sql.run(cmd);        
    }

    public static Options buildCliOptions() {
        Options options = new Options();
        final Option cliInput = new Option("i", "inputFile", true, "Xls filepath");
        cliInput.setRequired(true);
        options.addOption(cliInput);

        final Option cliQuery = new Option("q", "query", true, "Query");
        cliQuery.setRequired(true);
        options.addOption(cliQuery);

        final Option cliTrim = new Option("t", "trim", false, "Trim values from xls");
        options.addOption(cliTrim);

        return options;
    }

    private void run(CommandLine cmd) {
        boolean trim;

        trim = cmd.hasOption("t");

        String query = cmd.getOptionValue("q");
        String xslPath = cmd.getOptionValue("i");

        System.out.println("Query: " + query);

        Workbook workbook = readWorkbook(xslPath);
       
        Xls2sqlBuilder builder = new Xls2sqlBuilder();
        List<String> queries = null;

        try {
            queries = builder.buildSql(query, workbook, 0, trim, false);
        } catch (Xls2sqlException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        for (final String str : queries)
            System.out.println(str);
    }

    private Workbook readWorkbook(final String xlsPath) {
        FileInputStream file = null;
        Workbook workbook = null;

        try {
            file = new FileInputStream(new File(xlsPath));
            workbook = new XSSFWorkbook(file);
        } catch (final FileNotFoundException notFound) {
            System.err.println("File not found " + notFound.getMessage());
            System.exit(-1);
        } catch (final IOException ioEx) {
            System.err.println("Error reading input file " + ioEx.getMessage());
            System.exit(-1);
        }

        return workbook;
    }
}

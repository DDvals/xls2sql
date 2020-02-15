package danzisoft.xls2sql;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
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

        final Options options = new Options();
        final Option cliInput = new Option("i", "inputFile", true, "Xls filepath");
        cliInput.setRequired(true);
        options.addOption(cliInput);

        final Option cliQuery = new Option("q", "query", true, "Query");
        cliQuery.setRequired(true);
        options.addOption(cliQuery);

        final Option cliTrim = new Option("t", "trim", false, "Trim values from xls");
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

        if (cmd.hasOption("t"))
            trim = true;
        else
            trim = false;

        String query = cmd.getOptionValue("q");
        String xslPath = cmd.getOptionValue("i");

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
            final Row row = sheet.getRow(i);

            for (int c = 0; c < queryColumns.length; c++) {
                final Cell cell = row.getCell(Integer.parseInt(queryColumns[c]));
                
                boolean needQuote = false;
                String cellValue = null;
                
                if(cell != null) {
	                CellType type = cell.getCellType();
	                
	                switch(type) {
	                case NUMERIC:
	                	Double value = cell.getNumericCellValue();
	                	
	                	if(value.equals(Math.rint(value)))
	                		cellValue = Integer.toString(value.intValue());
	                	else
	                		cellValue = Double.toString(cell.getNumericCellValue());
	                	
	                	break;
	                case STRING:
	                	cellValue = cell.getStringCellValue();
	                	needQuote = true;
	                	break;
	                case BLANK:
	                	cellValue = "null";
	                	break;
	                default:
	                	System.err.println("Unsopported cell type: " + type.toString());
	                    System.exit(-1);
	                }
                } else {
                	cellValue = "null";
                }

                if(needQuote) {
	                if (trim)
	                    cellValue = cellValue.trim();
	
	                cellValue = cellValue.replaceAll("'", "''");	                
	                q = q.replaceAll("\\[\\$" + queryColumns[c] + "\\]", "'" + cellValue + "'");
	                
                } else {
                	q = q.replaceAll("\\[\\$" + queryColumns[c] + "\\]", cellValue);
                }

                
            }

            queries.add(q);
        }

        for (final String str : queries)
            System.out.println(str);
    }
}

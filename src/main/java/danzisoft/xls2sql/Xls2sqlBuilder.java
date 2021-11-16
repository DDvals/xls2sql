package danzisoft.xls2sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

public class Xls2sqlBuilder {

    public List<String> buildSql(String inputQuery, Workbook workbook, int workSheetNumber, boolean trim,
                                 boolean skipHeader) throws Xls2sqlException {

        final Sheet sheet = workbook.getSheetAt(workSheetNumber);
        final String[] queryColumns = StringUtils.substringsBetween(inputQuery, "[$", "]");

        if (queryColumns == null || queryColumns.length == 0)
            throw new Xls2sqlException("No column specified");

        final List<String> queries = new ArrayList<>();

        int startIdx = 0;
        if (skipHeader)
            startIdx = 1;
        
        for (int i = startIdx; i <= sheet.getLastRowNum(); i++) {
            String q = new String(inputQuery);
            final Row row = sheet.getRow(i);

            for (String queryColumn : queryColumns) {
                final Cell cell = row.getCell(Integer.parseInt(queryColumn));

                boolean needQuote = false;
                String cellValue;

                if (cell != null) {
                    CellType type = cell.getCellType();

                    switch (type) {
                        case NUMERIC:
                            Double value = cell.getNumericCellValue();

                            if (value.equals(Math.rint(value)))
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
                            throw new Xls2sqlException("Unsopported cell type: " + type.toString());
                    }
                } else {
                    cellValue = "null";
                }

                if (needQuote) {
                    if (trim)
                        cellValue = cellValue.trim();

                    cellValue = cellValue.replaceAll("'", "''");
                    q = q.replaceAll("\\[\\$" + queryColumn + "\\]", "'" + cellValue + "'");

                } else {
                    q = q.replaceAll("\\[\\$" + queryColumn + "\\]", cellValue);
                }
            }

            queries.add(q);
        }

        return queries;
    }
}

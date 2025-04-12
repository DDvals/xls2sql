package danzisoft;

import danzisoft.xls2sql.Xls2sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppTest 
{
    String trimOpt;
    String queryOpt;
    String queryOptVal;
    String inputOpt;
    String inputOptVal;
    String[] args;

    Workbook workbook;

    @BeforeAll
    public void setup() {
        trimOpt = "-t";
        queryOpt = "-q";
        queryOptVal = "select * from test where col1 = [$0] and col2 = [$1];";
        inputOpt = "-i";
        inputOptVal = "test.xlsx";
        String[] localArgs = {trimOpt, queryOpt, queryOptVal, inputOpt, inputOptVal};
        args = localArgs;
    }

    public Workbook buildTestWorkbook() {
        return null;
    }

    private CommandLine buildCommandLine() throws ParseException {
        Options opt = Xls2sql.buildCliOptions();
        final CommandLineParser cliParser = new DefaultParser();
        final CommandLine cmd = cliParser.parse(opt, args);
        return cmd;
    }        


    @Test
    public void testCliOptions() throws ParseException {
        final CommandLine cmd = buildCommandLine();

        assertTrue(cmd.hasOption("t"));
        assertTrue(cmd.hasOption("q"));
        assertTrue(cmd.hasOption("i"));
        assertEquals(queryOptVal, cmd.getOptionValue("q"));
        assertEquals(inputOptVal, cmd.getOptionValue("i"));
    }
}

package danzisoft;

import static org.junit.Assert.assertTrue;

import danzisoft.xls2sql.Xls2sql;
import org.apache.commons.cli.*;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testCliOptions() throws ParseException {
        Options opt = Xls2sql.buildCliOptions();
        String trimOpt = "-t";
        String queryOpt = "-q";
        String queryOptVal = "select * from test where col1 = [$0] and col2 = [$1];";
        String inputOpt = "-i";
        String inputOptVal = "test.xlsx";
        String[] args = {trimOpt, queryOpt, queryOptVal, inputOpt, inputOptVal};

        final CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(opt, args);

        assertTrue(cmd.hasOption("t"));
        assertTrue(cmd.hasOption("q"));
        assertTrue(cmd.hasOption("i"));
        assertTrue(queryOptVal.equals(cmd.getOptionValue("q")));
        assertTrue(inputOptVal.equals(cmd.getOptionValue("i")));
    }
    
}

package ed.inf.adbs.lightdb;

import java.io.FileReader;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;


/**
 * Lightweight in-memory database system
 *
 */
public class LightDB {

	public static void main(String[] args) {
		
		args = new String[3]; 
		args[0] = "./samples/db";
		args[1] = "./samples/input/query5.sql";
		args[2] = "./samples/output/query2.csv";

		if (args.length != 3) {
			System.err.println("Usage: LightDB database_dir input_file output_file");
			return;
		}

		String databaseDir = args[0];
		String inputFile = args[1];
		String outputFile = args[2];

		parsingExample(inputFile);
	}

	/**
	 * Example method for getting started with JSQLParser. Reads SQL statement from
	 * a file and prints it to screen; then extracts SelectBody from the query and
	 * prints it to screen.
	 */

	public static void parsingExample(String filename) {
		try {
			//Statement statement = CCJSqlParserUtil.parse(new FileReader(filename));
            Statement statement = CCJSqlParserUtil.parse("SELECT Boats.A as A, T.B as B, T.C as C FROM Boats, T");
			if (statement != null) {
				System.out.println("Read statement: " + statement);
				Select select = (Select) statement;
				PlainSelect plain = (PlainSelect) select.getSelectBody();
				List<SelectItem> selectitems = plain.getSelectItems();
				System.out.println("Select body is " + select.getSelectBody());
				System.out.println(selectitems);
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}

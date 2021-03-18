package ed.inf.adbs.lightdb;

import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;


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
            Statement statement = CCJSqlParserUtil.parse(
            		"SELECT * FROM Boats B, T t WHERE T.D=Boats.E and T.A=3 order by T.A");
			if (statement != null) {
				System.out.println("Read statement: " + statement);
				Select select = (Select) statement;
				PlainSelect plain = (PlainSelect) select.getSelectBody();
				System.out.println(plain.getFromItem());
				
				List<OrderByElement> orderByElements = plain.getOrderByElements();
				System.out.println(orderByElements.get(0).toString());
				
								
				List<Join> joins = plain.getJoins();
				System.out.println(plain.getFromItem().getClass());
				
				System.out.println(plain.getJoins());
				
				List<SelectItem> selectItems = plain.getSelectItems();
				for (SelectItem item: selectItems) {
					System.out.println(item);
				}
				
				
				Stack stack = new Stack();
				SelectDeParser parser = new SelectDeParser() {
					public void visit(Table table) {stack.add(table); System.out.println(table);}
					public void visit(AllColumns allColumns) {stack.add(allColumns);System.out.println(allColumns);}
					public void visit(SubJoin subjoin) {stack.add(subjoin); System.out.println(subjoin);};
					
					
				};
				
				
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				plain.accept(parser);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				
				
				FromItem fromItem = plain.getFromItem();
				Expression whereExp = plain.getWhere();
				
				//System.out.println(plain.getJoins());
				//System.out.println("Select body is " + select.getSelectBody());
				//System.out.println(selectItems);
				//System.out.println(fromItem);
				//System.out.println(whereExp);
				ExpressionDeParser expressionDeParser = new ExpressionDeParser();
				whereExp.accept(expressionDeParser);
				AndExpression andExp = (AndExpression)whereExp;
				EqualsTo exp1 = (EqualsTo)andExp.getLeftExpression();
				EqualsTo exp2 = (EqualsTo)andExp.getRightExpression();
				
				exp1.accept(expressionDeParser);
				
				System.out.println(exp1.getLeftExpression());
				System.out.println(exp2.getRightExpression());
				
				System.out.println(exp1.toString().contains("T."));
	    	    AndExpression and = new AndExpression();
	    	    System.out.println(and);
	    	    
	    	    
	    	    List<String> list = new ArrayList<String>();
	    	    
	    	    
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}

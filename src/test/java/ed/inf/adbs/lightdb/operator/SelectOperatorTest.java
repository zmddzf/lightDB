package ed.inf.adbs.lightdb.operator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.impl.SelectOperator;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SelectOperatorTest {
	@Test
	public void testNext() {
		String sql = "SELECT Boats.E FROM Boats WHERE Boats.E > 3 and 2 <= 4";
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectExp = (Select) statement;
			PlainSelect plain = (PlainSelect) selectExp.getSelectBody();
			Expression whereExp = plain.getWhere();
			
			System.out.println(whereExp);
			
			Catalog catalog = Catalog.getInstance("./samples/db");
			ScanOperator scan = new ScanOperator(catalog, "Boats");
			SelectOperator select = new SelectOperator(scan, catalog, whereExp);
			select.reset();
			
			Tuple tuple; 
			while ((tuple = select.getNextTuple()) != null) {
				System.out.println(tuple.toString());
			}
			
			System.out.println("test dump>>>>>>>>>>>>>>>>>>>");
			select.dump(null);
								    
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

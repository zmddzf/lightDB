package ed.inf.adbs.lightdb.operator.utils;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SelectionVisitorTest {
	Catalog catalog = Catalog.getInstance("./samples/db");
	
	@Test
	public void testCheck() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		String tableName = "Boats";
		List values = new ArrayList();
		values.add(101);
		values.add(2);
		values.add(3);
		Tuple tuple = new Tuple(values);
		TableInfo tableInfo = (TableInfo) catalog.tables.get(tableName);
		
		String sql = "SELECT Boats.E FROM Boats WHERE "
				+ "Boats.E <= 3 and "
				+ "Boats.F > 1 and "
				+ "Boats.F != Boats.E";
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select select = (Select) statement;
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Expression whereExp = plain.getWhere();
			
			SelectionVisitor visitor = new SelectionVisitor(catalog, whereExp);
			
			assertTrue(visitor.check(tuple)==true);
		    
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

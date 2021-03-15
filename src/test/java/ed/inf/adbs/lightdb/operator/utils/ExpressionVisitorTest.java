package ed.inf.adbs.lightdb.operator.utils;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class ExpressionVisitorTest {
	Catalog catalog = Catalog.getInstance("./samples/db");
	
	@Test
	public void testGetExpressions() {
		String sql = "SELECT Boats.E FROM Boats, Sailors "
				+ "WHERE "
				+ "Boats.E = Sailors.B and "
				+ "Boats.F > Sailors.A and "
				+ "Boats.F <= Sailors.E and "
				+ "Boats.F <= Sailors.A and "
				+ "Boats.F != Boats.E and "
				+ "1 = Boats.F and "
				+ "1 != 2";
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select select = (Select) statement;
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Expression whereExp = plain.getWhere();
			List<String> tableOrder = new ArrayList<String>(){{add("Boats"); add("Sailors");}};
			ExpressionVisitor visitor = new ExpressionVisitor(whereExp, tableOrder);
			System.out.println(visitor.getExpMap());
			HashMap hashMap = visitor.getExpressions();
			System.out.println(hashMap);
			
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

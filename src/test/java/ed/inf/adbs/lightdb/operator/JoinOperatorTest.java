package ed.inf.adbs.lightdb.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.JoinOperator;
import ed.inf.adbs.lightdb.operator.impl.ProjectOperator;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.utils.ExpressionVisitor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class JoinOperatorTest {
	String sql = "select Sailors.A, Reserves.G from Reserves, Sailors where Sailors.A < Reserves.G and Sailors.A < Reserves.G and 1!=2";
	@Test
	public void testgetNextTuple() {
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select select = (Select) statement;
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Expression whereExp = plain.getWhere();
			List<SelectItem> selectItems = plain.getSelectItems();
			
			ExpressionVisitor visitor = new ExpressionVisitor(whereExp, 
					new ArrayList<String>(){{add("Reserves"); add("Sailors");}});
			System.out.println(whereExp);
			HashMap hashMap = visitor.getExpressions();
			System.out.println(hashMap);
			
			Catalog catalog = Catalog.getInstance("./samples/db");
			
			ScanOperator scan1 = new ScanOperator(catalog, "Reserves");
			ScanOperator scan2 = new ScanOperator(catalog, "Sailors");
						
			JoinOperator join = new JoinOperator(scan1, scan2, 
					catalog, whereExp);
						
			ProjectOperator project = new ProjectOperator(join, catalog, selectItems);
			project.reset();
						
			project.dump(null);;
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

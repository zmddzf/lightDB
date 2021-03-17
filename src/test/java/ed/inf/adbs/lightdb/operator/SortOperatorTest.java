package ed.inf.adbs.lightdb.operator;

import java.util.List;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.impl.SelectOperator;
import ed.inf.adbs.lightdb.operator.impl.SortOperator;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SortOperatorTest {
	
	@Test
	public void testNext() {
		String sql = "SELECT Boats.F, Boats.E FROM Boats WHERE 2 <= 4 order by Boats.E, Boats.F";
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectExp = (Select) statement;
			PlainSelect plain = (PlainSelect) selectExp.getSelectBody();
			Expression whereExp = plain.getWhere();
			List<OrderByElement> orderByElements = plain.getOrderByElements();
			
			System.out.println(whereExp);
			
			Catalog catalog = Catalog.getInstance("./samples/db");
			ScanOperator scan = new ScanOperator(catalog, "Boats");
			SelectOperator select = new SelectOperator(scan, catalog, whereExp);
			SortOperator sort = new SortOperator(select, catalog, orderByElements);
			
			select.reset();
			select.dump(null);
			
			sort.reset();
			
			System.out.println("test dump>>>>>>>>>>>>>>>>>>>");
			sort.dump(null);
								    
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

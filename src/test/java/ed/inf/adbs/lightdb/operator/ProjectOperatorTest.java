package ed.inf.adbs.lightdb.operator;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.ProjectOperator;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.impl.SelectOperator;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperatorTest {
	@Test
	public void testNext() {
		String sql = "SELECT Boats.D, Boats.D FROM Boats WHERE Boats.E <= 3 and Boats.F <= 4";
        try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectExp = (Select) statement;
			PlainSelect plain = (PlainSelect) selectExp.getSelectBody();
			Expression whereExp = plain.getWhere();
			List<SelectItem> selectItems = plain.getSelectItems();
			System.out.println(selectItems);
			
			Catalog catalog = Catalog.getInstance("./samples/db");
			ScanOperator scan = new ScanOperator(catalog, "Boats");
			SelectOperator select = new SelectOperator(scan, catalog, whereExp);
			ProjectOperator project = new ProjectOperator(select, catalog, selectItems);
			
			Tuple tuple; 
			project.reset();
			
 		    while ((tuple = project.getNextTuple()) != null) {
				System.out.println(tuple.toString());
			}
			
			System.out.println("test dump>>>>>>>>>>>>>>>>>>>");
			project.dump(null);
								    
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

package ed.inf.adbs.lightdb.operator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.interpreter.PlanBuilder;
import ed.inf.adbs.lightdb.operator.impl.DuplicateEliminationOperator;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class DuplicateEliminationOperatorTest {
	
	PlanBuilder planBuilder = new PlanBuilder("./samples/db");
	String sql = "select distinct * from Reserves R";
	
	@Test
	public void testNext() {
		
		try {
			Operator operator = planBuilder.buildTree(sql);
			List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
			Catalog catalog = Catalog.getInstance("./samples/db");
			DuplicateEliminationOperator dpOperator = new DuplicateEliminationOperator(operator, catalog, orderByElements);
			dpOperator.reset();
			dpOperator.dump(null);
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

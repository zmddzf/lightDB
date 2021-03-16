package interpreter;

import org.junit.Test;

import ed.inf.adbs.lightdb.interpreter.PlanBuilder;
import ed.inf.adbs.lightdb.operator.Operator;
import net.sf.jsqlparser.JSQLParserException;

public class PlanBuilderTest {
	String sql = "select Sailors.A, Reserves.G"
			+ " from Reserves, Sailors "
			+ "where Sailors.A < Reserves.G and "
			+ "Reserves.G <= 5"
			+ "and 1!=2";
	PlanBuilder planBuilder = new PlanBuilder("./samples/db");
	
	@Test
	public void buildTreeTest() {
		try {
			Operator operator = planBuilder.buildTree(sql);
			operator.reset();
			operator.dump(null);
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

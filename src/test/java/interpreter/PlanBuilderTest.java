package interpreter;

import java.io.IOException;

import org.junit.Test;

import ed.inf.adbs.lightdb.interpreter.PlanBuilder;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.impl.JoinOperator;
import net.sf.jsqlparser.JSQLParserException;

public class PlanBuilderTest {
	String sql1 = "select Sailors.A, Reserves.G"
			+ " from Reserves, Sailors "
			+ "where Sailors.A < Reserves.G and "
			+ "Reserves.G <= 3"
			+ "and 1!=2";
	
	String sql2 = "select * from Reserves R, Sailors S";
	String sql3 = "select R.G, R.G, S.A from Reserves R, Sailors S, Boats B where S.A < R.G "
			+ "and B.D < S.B order by S.A";
	
	String sql4 = "SELECT DISTINCT Reserves.G, s.C, s2.A, b0.D, s2.B, b0.E, b0.F, b1.D, b1.E, b1.F,Reserves.H\r\n" + 
			"FROM Reserves," + 
			"     Sailors s," + 
			"     Sailors s2," + 
			"     Boats b0," + 
			"     Boats b1 " + 
			"WHERE Reserves.H >= 102" + 
			"  and 100 = s.B" + 
			"  and s2.A = 6" + 
			"  and Reserves.H < 104" + 
			"  and s.A = Reserves.G" + 
			"  and s2.A<s2.B" + 
			"  and b1.D<103" + 
			"  ORDER BY b0.E,b0.F,b0.D, b1.F";
	
	PlanBuilder planBuilder = new PlanBuilder("./samples/db");
	
	@Test
	public void buildTreeTest() throws IOException {
		try {
			System.out.println(">>>>>>>sql1>>>>>>>");
			Operator operator1 = planBuilder.buildTree(sql1);
			operator1.reset();
			operator1.dump(null);

			System.out.println(">>>>>>>sql2>>>>>>>");
			Operator operator2 = planBuilder.buildTree(sql2);
			operator2.reset();
			operator2.dump(null);
			
			System.out.println(">>>>>>>sql3>>>>>>>");
			Operator operator3 = planBuilder.buildTree(sql3);
			operator3.reset();
			operator3.dump(null);
						
			System.out.println(">>>>>>>sql4>>>>>>>");
			Operator operator4 = planBuilder.buildTree(sql4);
			operator4.reset();
			operator4.dump(null);
			
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

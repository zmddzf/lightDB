package ed.inf.adbs.lightdb.operator;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.tuple.Tuple;

import static org.junit.Assert.assertTrue;

public class ScanOperatorTest {
	@Test
	public void testNext() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		ScanOperator scan = new ScanOperator(catalog, "Boats");
		scan.reset();
		Tuple tuple;
		tuple = scan.getNextTuple();
		
		Integer[] values = new Integer[3];
		values[0] = 101;
		values[1] = 2;
		values[2] = 3;
		Tuple truthTuple = new Tuple(values);
		
		assertTrue(truthTuple.toString().equals(tuple.toString()));
		/**
		while ((tuple = scan.getNextTuple()) != null) {
			System.out.println(tuple.toString());
		}**/
	}
	
	@Test
	public void testDump() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		ScanOperator scan = new ScanOperator(catalog, "Boats");
		scan.dump(null);
		scan.dump("./samples/test/scan_test.txt");
	}

}

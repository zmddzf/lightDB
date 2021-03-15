package ed.inf.adbs.lightdb.operator;

import org.junit.Test;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.tuple.Tuple;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class ScanOperatorTest {
	@Test
	public void testNext() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		ScanOperator scan = new ScanOperator(catalog, "Boats");
		scan.reset();
		Tuple tuple;
		tuple = scan.getNextTuple();
		
		List<Integer> values = new ArrayList();
		values.add(101);
		values.add(2);
		values.add(3);
		Tuple truthTuple = new Tuple(values);
		
		assertTrue(truthTuple.toString().equals(tuple.toString()));
	}
	
	@Test
	public void testDump() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		ScanOperator scan = new ScanOperator(catalog, "Boats");
		scan.dump(null);
		scan.dump("./samples/test/scan_test.txt");
	}

}

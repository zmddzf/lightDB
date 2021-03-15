package ed.inf.adbs.lightdb.tuple;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class TupleTest {
	
	@Test
	public void testToString() {
		List values = new ArrayList();
		values.add(1);
		values.add(2);
		values.add(3);
		values.add(4);
		Tuple tuple = new Tuple(values);
		//System.out.println(tuple.toString());
		assertTrue(tuple.toString().equals("1,2,3,4"));
	}
	
	@Test
	public void testToList() {
		List<Integer> values = new ArrayList();
		values.add(1);
		values.add(2);
		values.add(3);
		values.add(4);
		Tuple tuple = new Tuple(values);
		//System.out.println(tuple.toList());
		List<Integer> trueValues = new ArrayList<Integer>();
		for(int i: values) {
			trueValues.add(i);
			}
		assertTrue(tuple.toList().equals(trueValues));
	}
	
	@Test
	public void testConcat() {
		List<Integer> values1 = new ArrayList();
		values1.add(1);
		values1.add(2);
		values1.add(3);
		values1.add(4);
		Tuple tuple1 = new Tuple(values1);
		
		List<Integer> values2 = new ArrayList();
		values2.add(1);
		values2.add(2);
		values2.add(3);
		Tuple tuple2 = new Tuple(values2);
		System.out.println(tuple1.concate(tuple2));
	}
	
	
}

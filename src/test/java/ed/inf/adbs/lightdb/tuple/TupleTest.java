package ed.inf.adbs.lightdb.tuple;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class TupleTest {
	
	@Test
	public void testTuple() {
		Integer[] values = {1,2,3,4};
		Tuple tuple = new Tuple(values);
		System.out.println(tuple.toString());
		assertTrue(tuple.toString().equals("1,2,3,4"));
	}
}

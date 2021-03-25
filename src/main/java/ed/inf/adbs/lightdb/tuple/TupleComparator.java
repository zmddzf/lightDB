package ed.inf.adbs.lightdb.tuple;
import java.util.Comparator;
import java.util.List;

/**
 * A subclass of Comparator, designed for tuple comparing.
 * @author zmddzf
 *
 */
public class TupleComparator implements Comparator<Tuple<Integer>> {
	List<Integer> indexList;
	
	/**
	 * Constructor
	 * @param indexList: the sort key index.
	 */
	public TupleComparator(List<Integer> indexList) {
		this.indexList = indexList;
	}
	
	/**
	 * Compare two tuples.
	 * @param o1: the first tuple.
	 * @param o2: the second tuple;
	 * @return -1 means o1<o2, 1 means o1>o2, 0 means equal
	 */
	@Override
	public int compare(Tuple<Integer> o1, Tuple<Integer> o2) {
		// TODO Auto-generated method stub
		
		for(int index: indexList) {
			if(o1.get(index) < o2.get(index)) {
				return -1;
			} else {
				if(o1.get(index) > o2.get(index)) {
					return 1;
				}
			}
		}
		
		return 0;
	}

}

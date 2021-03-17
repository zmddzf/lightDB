package ed.inf.adbs.lightdb.tuple;
import java.util.Comparator;
import java.util.List;


public class TupleComparator implements Comparator<Tuple<Integer>> {
	List<Integer> indexList;
	public TupleComparator(List<Integer> indexList) {
		this.indexList = indexList;
	}
	
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

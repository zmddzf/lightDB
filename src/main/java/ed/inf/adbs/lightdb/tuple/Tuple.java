package ed.inf.adbs.lightdb.tuple;
import java.util.ArrayList;
import java.util.List;

public class Tuple<T> {
	// final type of list
    private final List<T> values;
	
    /**
     * Constructor
     * @param values: N-tuple values
     */
    public Tuple(T[] args) {
    	List<T> list = new ArrayList<T>();
    	for(T item:args) {
    		list.add(item);
    	}
    	this.values = list;
    }
    
    /**
     * Change tuple to String
     * preparing for output
     */
    public String toString() {
    	String str = new String();
    	for(T item: this.values) {
    		str += String.valueOf(item);
    		str += ",";
    	}
    	str = str.substring(0, str.length()-1);
    	return str;
    }
}

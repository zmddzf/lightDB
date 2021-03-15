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
    public Tuple(List<T> args) {
    	this.values = args;
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
    
    public List toList() {
    	List values_list = new ArrayList();
    	for(T item: values) {
    		values_list.add(item);
    	}
		return values_list;
    	
    }
    
    public T get(int index) {
    	T v = values.get(index);
		return v;
    }
    
    public Tuple<T> concate(Tuple<T> tuple) {
    	Tuple<T> newTuple;
    	List<T> list = this.toList();
    	
    	list.addAll(tuple.toList());
    	
    	newTuple = new Tuple<T>(list);
    	
		return newTuple;
    	
    }
}

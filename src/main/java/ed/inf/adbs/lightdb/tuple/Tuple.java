package ed.inf.adbs.lightdb.tuple;
import java.util.ArrayList;
import java.util.List;


/**
 * This is a tuple class.
 * The values in tuple is not changeable.
 * @author zmddzf
 * @param <T>: the type of the Tuple.
 */
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
     * Change tuple to String.
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
    
    /**
     * Create a new List and return.
     * @return valueList
     */
    public List toList() {
    	List valuesList = new ArrayList();
    	for(T item: values) {
    		valuesList.add(item);
    	}
		return valuesList;
    	
    }
    
    /**
     * Get the value corresponds to the index
     * @param index
     * @return v
     */
    public T get(int index) {
    	T v = values.get(index);
		return v;
    }
    
    /**
     * Concat two tuple
     * @param tuple
     * @return newTuple
     */
    public Tuple<T> concate(Tuple<T> tuple) {
    	Tuple<T> newTuple;
    	List<T> list = this.toList();
    	
    	list.addAll(tuple.toList());
    	
    	newTuple = new Tuple<T>(list);
    	
		return newTuple;
    }
    
    /**
     * Compare all the values in two tuple
     * @param tuple
     * @return boolean type
     */
    public boolean compareValues(Tuple tuple) {
    	if(tuple == null) {return false;}
    	
    	if(tuple.toList().equals(values)) {return true;}
    	
    	return false;
    }
}

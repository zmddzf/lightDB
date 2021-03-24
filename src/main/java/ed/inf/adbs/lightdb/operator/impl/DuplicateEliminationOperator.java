package ed.inf.adbs.lightdb.operator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;


/**
 * The DuplicateEliminationOperator assumes the tuple are sorted by at least one column
 * Here the group eliminate strategy is used, a group means a set of tuples that are
 * organised together by the sorted keys
 * We need to maintain a list to store previous order-by key values and a HashSet to
 * store the tuples that in one group
 * When the tuples order-by key values are not same as the previous order-by key values
 * clear the HashSet and update the previous key value list to release memory
 * @author zmddzf
 */
public class DuplicateEliminationOperator extends Operator {

	
	// tuple set, store the tuples that at one same group  
	private HashSet<List> cacheTupleSet = new HashSet<List>();
	// save the group keys' values
	private List<Integer> prevKey = new ArrayList<Integer>();
	private Operator child;
	// the sorted key index
	private List<Integer> indexList = new ArrayList<Integer>();
	
	public DuplicateEliminationOperator(Operator child, Catalog catalog, 
			List<OrderByElement> orderByElements) {
		this.child = child;
		this.tableName = child.getTableName();
		if(orderByElements == null) {
			// this is designed for no-oder-by situation
			// when there is no order by
			// create a sort operator to sort all keys before using this operator
			int length = catalog.getTable(tableName).getColumns().size();
			for(int i=0; i< length; i++) {
				indexList.add(i);
			}
		} else {
		    for(OrderByElement element: orderByElements) {
			    indexList.add(catalog.getIndex(tableName, element.toString()));
		    }
	    }
	}
	
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple tuple;
		
		while((tuple = child.getNextTuple()) != null) {
			// first get the tuple's sorted keys' values
			List<Integer> orderKey = new ArrayList<Integer>();
			for(int index: indexList) {
				orderKey.add((Integer) tuple.get(index));
			}
			
			// check the sorted key values is changed or not
			if(!prevKey.equals(orderKey)) {
				// if the sorted keys' values is changed
				// change the prevKey to this tuple's sorted keys' values
				prevKey.clear();
				prevKey.addAll(orderKey);
				// clear the cacheTupleset to release memory
				cacheTupleSet.clear();
				// add this tuple into the cacheTupleset
				cacheTupleSet.add(tuple.toList());
				return tuple;
			} else if(!cacheTupleSet.contains(tuple.toList())) {	
				// if there is no same tuple in the cache
				// add this tuple in to the cache
				cacheTupleSet.add(tuple.toList());
				return tuple;
			}
		}
		
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
			close();
			open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void open() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		child.open();
		state = true;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (state == true) {
		    child.close();
		}
		this.state = false;		
	}

}

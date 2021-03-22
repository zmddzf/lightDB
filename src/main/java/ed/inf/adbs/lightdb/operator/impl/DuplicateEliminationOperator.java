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

public class DuplicateEliminationOperator extends Operator {
	
	private HashSet<List> cacheTupleSet = new HashSet<List>();
	private List<Integer> prevKey = new ArrayList<Integer>();
	
	private Operator child;
	private List<Integer> indexList = new ArrayList<Integer>();
	
	public DuplicateEliminationOperator(Operator child, Catalog catalog, 
			List<OrderByElement> orderByElements) {
		this.child = child;
		this.tableName = child.getTableName();
		if(orderByElements == null) {
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
			List<Integer> orderKey = new ArrayList<Integer>();
			for(int index: indexList) {
				orderKey.add((Integer) tuple.get(index));
			}
			
			if(!prevKey.equals(orderKey)) {
				prevKey.clear();
				prevKey.addAll(orderKey);
				cacheTupleSet.clear();
				cacheTupleSet.add(tuple.toList());
				return tuple;
			} else if(!cacheTupleSet.contains(tuple.toList())) {				
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

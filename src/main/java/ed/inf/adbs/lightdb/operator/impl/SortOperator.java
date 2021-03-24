package ed.inf.adbs.lightdb.operator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.utils.SelectionVisitor;
import ed.inf.adbs.lightdb.tuple.Tuple;
import ed.inf.adbs.lightdb.tuple.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class SortOperator extends Operator {
	
	private Operator child;
	private List<Integer> indexList = new ArrayList<Integer>();
	private List<Tuple<Integer>> tupleList;
	private Catalog catalog;
	private int pointer;
	
	public SortOperator(Operator child, Catalog catalog, List<OrderByElement> orderByElements) {
		this.child = child;
		this.tableName = child.getTableName();
		this.catalog = catalog;

		
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
		if(pointer < tupleList.size()) {
			Tuple<Integer> tuple =  tupleList.get(pointer);
			pointer += 1;
			return tuple;
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
		tupleList = new ArrayList<Tuple<Integer>>();
		
		Tuple<Integer> tuple;
		while((tuple = child.getNextTuple())!=null) {			
			tupleList.add(tuple);
		}
		
		
		Collections.sort(tupleList, new TupleComparator(indexList));
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (state == true) {
		    child.close();
		}
		this.state = false;		
		pointer = 0;
		//tupleList = new ArrayList<Tuple<Integer>>();
	}

}

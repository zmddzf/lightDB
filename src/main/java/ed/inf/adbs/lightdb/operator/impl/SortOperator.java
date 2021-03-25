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

/**
 * A block sort operator, when it is opened, read all the tuples and sort in memory.
 * @author zmddzf
 *
 */
public class SortOperator extends Operator {
	
	private Operator child;
	private List<Integer> indexList = new ArrayList<Integer>(); // indexList contains the order-by columns' index
	private List<Tuple<Integer>> tupleList;
	private Catalog catalog;
	private int pointer;
	
	/**
	 * Constructor
	 * @param child: The child of sort operator.
	 * @param catalog: database catalog.
	 * @param orderByElements: the sort key.
	 */
	public SortOperator(Operator child, Catalog catalog, List<OrderByElement> orderByElements) {
		this.child = child;
		this.tableName = child.getTableName();
		this.catalog = catalog;

		
		if(orderByElements == null) {
			// if there is no orderBy elements
			// use all the columns to do sort
			// this is designed for distinct operator
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
	

	/**
	 * Getting next tuple need to maintain a pointer
	 * Every time get a tuple, pointer add one
	 * Check the tuple list size to decide return null or not
	 */
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
	/**
	 * Sort is a block operator
	 * It needs to read all the data from the child
	 * and sort them all.
	 */
	public void open() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		child.open();
		state = true;
		tupleList = new ArrayList<Tuple<Integer>>();
		// read all the tuple into a tupleList
		// all the tuples are in the main memory
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
	}

}

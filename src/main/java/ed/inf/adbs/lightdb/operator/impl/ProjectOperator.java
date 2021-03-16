package ed.inf.adbs.lightdb.operator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectOperator extends Operator {
	private Operator child;
	private List<SelectItem> selectItems;
	private Catalog catalog;
	private String tableName;
	
	public ProjectOperator(Operator child, Catalog catalog, List<SelectItem> selectItems) {
		this.child = child;
		this.selectItems = selectItems;
		this.catalog = catalog;
		this.setTableName(child.getTableName());
	}
	
	@Override
	public void open() throws FileNotFoundException, IOException {
		this.state = true;
		child.open();
	}
	
	@Override
	public void close() throws IOException {
		if (state == true) {
		    child.close();
		}
		this.state = false;
	}
	
	@Override
	public void reset() {
		try {
			child.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			child.open();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Tuple getNextTuple() {
		Tuple<?> childTuple;
		Tuple<Integer> tuple;
		
		List<Integer> list = new ArrayList<Integer>();
		childTuple = child.getNextTuple();
		
		if(childTuple == null) {
			return null;
		}
		
		List<Integer> childList = childTuple.toList();
		
		for(SelectItem item: selectItems) {
			if(item instanceof AllColumns) {
				return childTuple;
			}
			
			int index = catalog.getIndex(tableName, item.toString());
			list.add(childList.get(index));
		}
		
		tuple = new Tuple<Integer>(list);
		return tuple;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}

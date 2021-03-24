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
	
	/**
	 * Constructor of ProjectOperator
	 * @param child
	 * @param catalog
	 * @param selectItems: a list of select items
	 */
	public ProjectOperator(Operator child, Catalog catalog, List<SelectItem> selectItems) {
		this.child = child;
		this.selectItems = selectItems;
		this.catalog = catalog;
		tempTable();  // create a temporary table info after project
	}
	
	/**
	 * Create a temporary table info
	 * The table name should be child table name + "_PROJ"
	 * The table info is stored in catalog
	 */
	private void tempTable() {
		String tableName = child.getTableName() + "_PROJ";
		this.setTableName(tableName);
		
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableName(tableName);
		List<String> columns = new ArrayList();
		for(SelectItem selectItem: selectItems) {
			columns.add(selectItem.toString());
		}
		
		tableInfo.setColumns(columns);
		
		// the table path is not actual exist
		// so use "//:inMemory" to mark it
		tableInfo.setTablePath("//:inMemory");
		// since it is a in memory table, set in memory mark as true
		tableInfo.setInMemory(true);
		
		catalog.tables.put(tableName, tableInfo);
		
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
		Tuple<Integer> childTuple;
		Tuple<Integer> tuple;
		
		List<Integer> list = new ArrayList<Integer>();
		childTuple = child.getNextTuple();  // read tuple from child
		
		if(childTuple == null) {
			// if child tuple is null, return null directly
			return null;
		}
		
		// get child tuple list
		List<Integer> childList = childTuple.toList();
		
		for(SelectItem item: selectItems) {
			if(item instanceof AllColumns) {
				// if all columns, return childTuple directly
				return childTuple;
			}
			
			// get index of the projected column
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

package ed.inf.adbs.lightdb.operator.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.utils.SelectionVisitor;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class JoinOperator extends Operator {
	private Operator leftChild;
	private Operator rightChild;
	private Catalog catalog;
	private SelectionVisitor visitor;
	private String tableName;
	private Tuple outerTuple;
	
	public JoinOperator(Operator leftChild, Operator rightChild, 
			Catalog catalog, Expression exp) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.catalog = catalog;
		tempTable();
		
		if(exp!=null) {
		    this.visitor = new SelectionVisitor(catalog, exp, tableName);
		} else {
			this.visitor = null;
		}
	}
	
	protected void tempTable() {
		
		String tableName1 = leftChild.getTableName();
		String tableName2 = rightChild.getTableName();
		tableName = tableName1 + " " + tableName2;
		this.setTableName(tableName);
		
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableName(tableName);
		
		List<String> columns1 = catalog.getTable(tableName1).getColumns();
		List<String> columns2 = catalog.getTable(tableName2).getColumns();
		
		List<String> columns = new ArrayList<String>();
		
		columns.addAll(columns1);
		columns.addAll(columns2);

		tableInfo.setColumns(columns);
		
		System.out.println(columns);
		tableInfo.setTablePath("//:inMemory");
		
		catalog.tables.put(tableName, tableInfo);
	}
	

	@Override
	public Tuple getNextTuple() {
		while(outerTuple != null) {
			Tuple innerTuple;
			while((innerTuple = rightChild.getNextTuple()) != null){
				Tuple cartesianTuple = outerTuple.concate(innerTuple);
				if(visitor == null) {
					return cartesianTuple;
				} else {				
				    if(visitor.check(cartesianTuple)) {
					    return cartesianTuple;
				    }
				}
			}
			rightChild.reset();
			outerTuple = leftChild.getNextTuple();
		}
		return null;
	}

	@Override
	public void open() throws FileNotFoundException, IOException {
		this.state = true;
		leftChild.open();
		rightChild.open();
		this.outerTuple = leftChild.getNextTuple();
	}
	
	@Override
	public void close() throws IOException {
		if (state == true) {
		    leftChild.close();
		    leftChild.close();
		}
		this.state = false;
	}
	
	@Override
	public void reset() {
		try {
			leftChild.close();
			rightChild.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			leftChild.open();
			rightChild.open();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableNames(String tableName) {
		this.tableName = tableName;
	}
	
}

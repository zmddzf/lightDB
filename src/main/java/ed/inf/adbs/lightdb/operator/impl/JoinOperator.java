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

/**
 * The join operation is rely on the join conditions. 
 * If we want to get `T1` and `T2`s' joint condition expression, 
 * we can use the `getExpression` method in `ExpressionVisitor` 
 * and take out the expression in the return HashMap, 
 * where the corresponding key is `"T1 T2"`. 
 * When the `JoinOperator` is opened, 
 * it will read one tuple `outerTuple` from his left child, 
 * which is the start point of outer loop. 
 * The inner loop is for reading tuple from the right child.
 * @author zmddzf
 *
 */
public class JoinOperator extends Operator {
	private Operator leftChild;
	private Operator rightChild;
	private Catalog catalog;
	private SelectionVisitor visitor;
	private String tableName;
	private Tuple outerTuple;
	
	/**
	 * Constructor of JoinOperator
	 * @param leftChild: the left join table operator
	 * @param rightChild: the right join table operator
	 * @param catalog: catalog of database
	 * @param exp: the join condition expression
	 */
	public JoinOperator(Operator leftChild, Operator rightChild, 
			Catalog catalog, Expression exp) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.catalog = catalog;
		tempTable();  // create a temporary table info in catalog
		
		if(exp!=null) {
			// designed for the situation that has no join condition
			// in this situation, return Cartesian tuple directly 
		    this.visitor = new SelectionVisitor(catalog, exp, tableName);
		} else {
			this.visitor = null;
		}
	}
	
	/**
	 * Add a temporary table info into catalog
	 * The temporary table name should be left table + " " + right table
	 */
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
		tableInfo.setTablePath("//:inMemory");
		tableInfo.setInMemory(true);
		
		catalog.tables.put(tableName, tableInfo);
	}
	
	/**
	 * When the `JoinOperator` is opened, it will read one tuple `outerTuple` from his left child, 
	 * which is the start point of outer loop. The inner loop is for reading tuple from the right child. 
	 * While the `outerTuple` is not `null`, the inner loop will start. 
	 * Inner loop first glue `outerTuple` and `innerTuple` together, and check whether it satisfies the
	 * join condition. If it does, return the glued tuple, otherwise, keep looping.  
	 * When the inner loop is finished, but the outer loop is not finished, 
	 * reset the right child and read next `outerTuple`.
	 */
	@Override
	public Tuple getNextTuple() {
		while(outerTuple != null) {
			// if outer tuple is not null, start inner loop
			Tuple innerTuple;
			while((innerTuple = rightChild.getNextTuple()) != null){
				// inner loop produce Cartesian tuple and check whether should return
				Tuple cartesianTuple = outerTuple.concate(innerTuple);
				if(visitor == null) {
					// if there is no join condition, return Cartesian
					return cartesianTuple;
				} else {
				    if(visitor.check(cartesianTuple)) {
				    	// if satisfy the condition, return
					    return cartesianTuple;
				    }
				}
			}
			// if no Cartesian tuple satisfy the condition
			// reset the inner loop and get next tuple for the outer loop
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
		// first record a outerTuple as a start point
		// this variable is also for recording the pointer
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
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			open();
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
	
	public Operator getLeftChild() {
		return leftChild;
	}
	
	public Operator getRightChild() {
		return rightChild;
	}
	
}

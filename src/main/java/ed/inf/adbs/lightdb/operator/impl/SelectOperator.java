package ed.inf.adbs.lightdb.operator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.utils.SelectionVisitor;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SelectOperator extends Operator {
	private Operator child;
	private SelectionVisitor visitor;
	private String tableName;
	
	public SelectOperator(Operator child, Catalog catalog, Expression exp) {
		this.child = child;
		this.visitor = new SelectionVisitor(catalog, exp);
		this.setTableName(child.getTableName());
	}
	
	public void open() throws FileNotFoundException, IOException {
		this.state = true;
		child.open();
	}
	
	public void close() throws IOException {
		if (state == true) {
		    child.close();
		}
		this.state = false;
	}
	
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
	
	public Tuple getNextTuple() {
		Tuple tuple;
		while((tuple = child.getNextTuple()) != null) {
			if(visitor.check(tuple)) {
				return tuple;
			}
	    }
		return null;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
}

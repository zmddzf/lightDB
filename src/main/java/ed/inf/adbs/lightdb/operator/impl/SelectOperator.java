package ed.inf.adbs.lightdb.operator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.utils.SelectionVisitor;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Filter operator.
 * If the tuple does not satisfies the predicate condition, drop it,
 * get next tuple from child until get one tuple satisfies and return it.
 * @author zmddzf
 *
 */
public class SelectOperator extends Operator {
	private Operator child;
	private SelectionVisitor visitor;
	private String tableName;
	
	/**
	 * Constructor of the filter
	 * @param child: the child of the filter, typically a instance of ScanOperator
	 * @param catalog: the catalog instance
	 * @param exp: filter expression
	 */
	public SelectOperator(Operator child, Catalog catalog, Expression exp) {
		this.child = child;
		// visitor is essential, while is used to check whether a tuple should be filted
		this.visitor = new SelectionVisitor(catalog, exp);
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
	
	/**
	 *  If the tuple does not satisfies the predicate condition, drop it,
	 *  get next tuple from child until get one tuple satisfies and return it.
	 */
	@Override
	public Tuple getNextTuple() {
		Tuple tuple;
		while((tuple = child.getNextTuple()) != null) {
			if(visitor.check(tuple)) {
				// check the tuple, if true, return
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

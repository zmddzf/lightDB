package ed.inf.adbs.lightdb.operator.utils;

import java.util.List;
import java.util.Stack;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;


/**
 * This is a class to visit the expression and check whether the expression is true.
 * Maintain a boolean stack and a long stack to record the intermediate results.
 * @author zmddzf
 */
public class SelectionVisitor extends ExpressionDeParser {
	private Catalog catalog;
	private Expression exp;
	private Tuple tuple;
	private String tableNames = null;
	
	private Stack<Boolean> boolStack = new Stack<Boolean>();
	private Stack<Long> longStack = new Stack<Long>();
	
	/**
	 * Constructor without the table name.
	 * @param catalog: database catalog.
	 * @param exp: the expression that extracted from where clause.
	 */
	public SelectionVisitor(Catalog catalog, Expression exp) {
		this.catalog = catalog;
		this.exp = exp;
	}
	
	/**
	 * Constructor with table name.
	 * @param catalog: database catalog.
	 * @param exp: the expression that extracted from where clause.
	 * @param tableName: table name.
	 */
	public SelectionVisitor(Catalog catalog, Expression exp, String tableNames) {
		this.catalog = catalog;
		this.exp = exp;
		this.tableNames = tableNames;
	}
	
	
	/**
	 * Pop two boolean values from boolStack and do and operation.
	 * Then push the result back again.
	 */
    @Override		
	public void visit(AndExpression andExpression) {
		super.visit(andExpression);
		boolean left = boolStack.pop();
		boolean right = boolStack.pop();		
		boolStack.push(left & right);
	}
    
    /**
     * When get a long value, push it into the longStack.
     */
    @Override
    public void visit(LongValue longValue) {    	
    	super.visit(longValue);
    	Long value = longValue.getValue();
    	longStack.push(value);
    }
    
    /**
     * When get a column, find the corresponding value and push into longStack.
     */
    @Override
    public void visit(Column column) {
    	super.visit(column);
    	String colName = column.toString();
    	String[] tableAndColumn = colName.split("\\.");
    	String tableName = tableNames == null? tableAndColumn[0]:tableNames;
    	
    	TableInfo tableInfo = (TableInfo) catalog.tables.get(tableName);
    	List<String> col = tableInfo.getColumns();
    	int index = col.indexOf(colName);
    	long value = (int) tuple.get(index);
    	longStack.push(value);
    }
    
    /**
     * When get a greaterThan operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    @Override
    public void visit(GreaterThan greaterThan) {
    	super.visit(greaterThan);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 < value2);
    	boolStack.push(e);
    }
    
    /**
     * When get a minorThan operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    @Override
    public void visit(MinorThan minorThan) {
    	super.visit(minorThan);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 > value2);
    	boolStack.push(e);
    	
    }
    
    /**
     * When get a notEqualsTo operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    public void visit(NotEqualsTo notEqualsTo) {
    	super.visit(notEqualsTo);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 != value2);
    	boolStack.push(e);
    }
    
    /**
     * When get a greaterThanEquals operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    public void visit(GreaterThanEquals greaterThanEquals) {
    	super.visit(greaterThanEquals);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 <= value2);
    	boolStack.push(e);
    	
    }
    
    /**
     * When get a minorThanEquals operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    public void visit(MinorThanEquals minorThanEquals) {
    	super.visit(minorThanEquals);

    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 >= value2);
    	boolStack.push(e);
    }
    
    
    /**
     * When get a equalsTo operator, pop two values from longStack,
     * compare them and push back to boolStack.
     */
    @Override
    public void visit(EqualsTo equalsTo) {
    	super.visit(equalsTo);

    	Long value1 = longStack.pop();
    	Long value2 = longStack.pop();
    	boolean e = (value1 == value2);
    	boolStack.push(e);
    }
    

	public boolean check(Tuple tuple) {
		this.tuple = tuple;
		
		exp.accept(this);
		Boolean result = boolStack.pop();
		
		boolStack.clear();
		longStack.clear();
		
		return result;
	}


}

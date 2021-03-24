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
	
	public SelectionVisitor(Catalog catalog, Expression exp) {
		this.catalog = catalog;
		this.exp = exp;
	}
	
	public SelectionVisitor(Catalog catalog, Expression exp, String tableNames) {
		this.catalog = catalog;
		this.exp = exp;
		this.tableNames = tableNames;
	}
	
	
    @Override		
	public void visit(AndExpression andExpression) {
		super.visit(andExpression);
		boolean left = boolStack.pop();
		boolean right = boolStack.pop();		
		boolStack.push(left & right);
	}
    
    @Override
    public void visit(LongValue longValue) {    	
    	super.visit(longValue);
    	Long value = longValue.getValue();
    	longStack.push(value);
    }
    
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
    
    @Override
    public void visit(GreaterThan greaterThan) {
    	super.visit(greaterThan);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 < value2);
    	boolStack.push(e);
    }
    
    @Override
    public void visit(MinorThan minorThan) {
    	super.visit(minorThan);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 > value2);
    	boolStack.push(e);
    	
    }
    
    public void visit(NotEqualsTo notEqualsTo) {
    	super.visit(notEqualsTo);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 != value2);
    	boolStack.push(e);
    }
    
    public void visit(GreaterThanEquals greaterThanEquals) {
    	super.visit(greaterThanEquals);
    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 <= value2);
    	boolStack.push(e);
    	
    }
    
    public void visit(MinorThanEquals minorThanEquals) {
    	super.visit(minorThanEquals);

    	long value1 = longStack.pop();
    	long value2 = longStack.pop();
    	boolean e = (value1 >= value2);
    	boolStack.push(e);
    }
    
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

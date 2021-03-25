/**
 * 
 */
package ed.inf.adbs.lightdb.operator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

/**
 * This is a class for extract conditions from the where clause
 * @author zmddzf
 */
public class ExpressionVisitor extends ExpressionDeParser {
	
	private Expression exp;
	private LinkedHashMap<String, ArrayList<Expression>> expMap;
	private List<String> tableOrder;
	
	/**
	 * Constructor
	 * @param exp: a where clause expression
	 * @param tableOrder: the from clause table order
	 */
	public ExpressionVisitor(Expression exp, List<String> tableOrder) {
		this.exp = exp;
		this.expMap = new LinkedHashMap<String, ArrayList<Expression>>();
		this.setTableOrder((ArrayList<String>) tableOrder);
		this.buildExpMap();
	}
	
	/**
	 * Build the expression Map
	 * A expMap is like {tableName=List<Expression>}
	 * The list contains all the expressions that correspond to table
	 */
	private void buildExpMap() {
		String tableName = tableOrder.get(0);
		expMap.put(tableName, new ArrayList<Expression>());
		for(int i=1; i < tableOrder.size(); i++) {
			expMap.put(tableOrder.get(i), new ArrayList<Expression>());
			tableName += " " + tableOrder.get(i);
			expMap.put(tableName, new ArrayList<Expression>());
		}
	}
	
	
	/**
	 * This designed for put expressions into the corresponded table.
	 * If the table name is not in expMap, create a new table name in the expMap.
	 * This is especially designed for join condition.
	 * If the table name has already existed, put it in directly.
	 * @param operator: The comparison operator interface type.
	 */
	private synchronized void putExp(ComparisonOperator operator) {
		String left = operator.getLeftExpression().toString();
		String right = operator.getRightExpression().toString();
		if((left.indexOf('.') != -1) && (right.indexOf('.') != -1)) {
			// if the left part is a column and right part is also a column
			// join operation, add a new table in expMap
			String table1 = left.split("\\.")[0];
			String table2 = right.split("\\.")[0];
			
			// if table1 is the left one, then table name is: table1 + " " + table2
			// when constrain the join oder [T1, T2], the T1.xx=T2.yy and T2.yy=T1.xx will
			// both generates a new table "T1 T2" 
			String tableName = (tableOrder.indexOf(table1) < tableOrder.indexOf(table2))?
					(table1 + " " + table2): (table2 + " " + table1);
			
			// traverse the expMap to find the proper ArrayList
			for(String key: expMap.keySet()) {
				if(key.contains(tableName)) {
					// if the key contains the table names
					// append the operator
					expMap.get(key).add(operator);
					return;
				}
			}
			    
		} else {
			if((left.indexOf('.') != -1) || (right.indexOf('.') != -1)) {
				String columnPart = (left.indexOf('.') != -1)? left : right;
				String table = columnPart.split("\\.")[0];
				
				if (!expMap.containsKey(table)) {
					expMap.put(table, new ArrayList<Expression>());
				}
				expMap.get(table).add(operator);
			} else {
				if (!expMap.containsKey("numeric")) {expMap.put("numeric", new ArrayList<Expression>());}
				expMap.get("numeric").add(operator);
			}
			
		}
				
	}
	
	
    public void visit(GreaterThan greaterThan) {
    	super.visit(greaterThan);
    	putExp(greaterThan);
    	
    }
    
    public void visit(MinorThan minorThan) {
    	super.visit(minorThan);
    	putExp(minorThan);
    }
    
    public void visit(NotEqualsTo notEqualsTo) {
    	super.visit(notEqualsTo);
    	putExp(notEqualsTo);
    }
    
    public void visit(GreaterThanEquals greaterThanEquals) {
    	super.visit(greaterThanEquals);
    	putExp(greaterThanEquals);
    }
    
    public void visit(MinorThanEquals minorThanEquals) {
    	super.visit(minorThanEquals);
    	putExp(minorThanEquals);
    }
    
    public void visit(EqualsTo equalsTo) {
    	super.visit(equalsTo);
    	putExp(equalsTo);
    }
    
    /**
     * This function is to return the expression for each table.
     * One table will get one expression.
     * The return hashMap is {tableName=expression}.
     * @return: hashMap
     */
    public HashMap<String, Expression> getExpressions() {
    	
    	exp.accept(this);
    	HashMap<String, Expression> hashMap = new HashMap<String, Expression>();
    	
    	for(Entry<String, ArrayList<Expression>> entry: expMap.entrySet()) {
    		ArrayList<Expression> expList = entry.getValue();
    		if(expList.size() < 1) {continue;}
    		
    	    
    	    if(expList.size() < 2) {
    	    	Expression and = expList.get(0);
    	    	hashMap.put(entry.getKey(), and);
    	    } else {
        	    AndExpression and = new AndExpression();
        	    and.setLeftExpression(expList.get(0));
        	    and.setRightExpression(expList.get(1));
        		for(int i=2; i < expList.size(); i++) {
        			Expression temp = new AndExpression(and.getLeftExpression(), and.getRightExpression());
        			and.setLeftExpression(temp);
        			and.setRightExpression(expList.get(i));
        		}
    	    	hashMap.put(entry.getKey(), and);
    	    	System.out.println(expMap.keySet());
    	    }
    	}
    	
		return hashMap;
    }
    
    public HashMap getExpMap() {
    	return expMap;
    }

	public List<String> getTableOrder() {
		return tableOrder;
	}


	public void setTableOrder(ArrayList<String> tableOrder) {
		this.tableOrder = tableOrder;
	}

}

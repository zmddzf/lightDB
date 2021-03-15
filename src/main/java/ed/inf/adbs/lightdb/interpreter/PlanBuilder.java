package ed.inf.adbs.lightdb.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.OperatorFactory;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.utils.ExpressionVisitor;
import ed.inf.adbs.lightdb.operator.utils.SelectionVisitor;
import ed.inf.adbs.lightdb.tuple.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class PlanBuilder {
	private Catalog catalog;
	
	public PlanBuilder(String dbPath) {
		this.catalog = Catalog.getInstance(dbPath);
	}
	
	
	public Operator buildTree(String sql) throws JSQLParserException {
		PlainSelect plain = parseSql(sql);
		OperatorFactory factory = new OperatorFactory();
		
		// create scan operator
		List<String> tableOrder = findTableOrder(plain);
		List<Operator> scanList = new ArrayList<Operator>();
		
		for(String tableName: tableOrder) {
			Operator scan = factory.getOperator(catalog, tableName);
			scanList.add(scan);
		}
		
		// find where clause
		HashMap<String, Expression> expMap = findWhereClause(plain, tableOrder);		
		
		// create predicate filters
		List<Operator> filterList;
		if(expMap == null) {
			filterList = scanList;
			
		} else {
			
			// deal with expression like 1 = 2 or 1 != 2
			if(expMap.containsKey("numeric")) {
				SelectionVisitor selectionVisitor = new SelectionVisitor(catalog, expMap.get("numeric"), null);
				// check whether the expression is true
				boolean flag = selectionVisitor.check(new Tuple(new ArrayList()));
				if(flag == false) {
					// if false, all expression is useless
					for(String tableName: expMap.keySet()) {
						expMap.put(tableName, expMap.get("numeric"));
					}
				}
			}
			
			// for those tables that constrained by expression
			filterList = new ArrayList<Operator>();
			for(Operator scan: scanList) {
				String tableName = scan.getTableName();
				if(expMap.containsKey(tableName)) {
					Operator filter = factory.getOperator(scan, catalog, expMap.get(tableName));  
					filterList.add(filter);
				} else {
					filterList.add(scan);
				}
			}
		}
		
		
		// create join operator
		Operator operator;  // the operator that after join
		
		if(filterList.size() == 1) {
			// if only involves 1 table
			// the operator is the only element of filterList
			operator = filterList.get(0);
		}
		
		if(filterList.size() > 1) {
			
			for(String tableName: tableOrder) {
				
			}
		}
		
		
		
		return null;
	}
	
	
	/***
	 * parse the sql string and return plain select structure
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public PlainSelect parseSql(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		if (statement != null) {
			Select select = (Select) statement;
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			return plain;
		}
		return null;
	}
	
	/***
	 * find out the table or tables in the select statement
	 * return the table name list
	 * the list defines the left deep join order.
	 * @param plain
	 * @return
	 */
	public List<String> findTableOrder(PlainSelect plain){
		String fromTable = plain.getFromItem().toString();
		List<String> tables = new ArrayList<String>();
		tables.add(fromTable);
		
		if(plain.getJoins() == null) {
			// if there is no join tables, directly return the tables
			return tables;
		}
		
		for(Join table:plain.getJoins()) {
			tables.add(table.toString());
		}
		return tables;
	}
	
	public List<SelectItem> findSelectItems(PlainSelect plain){
		List<SelectItem> selectItems = plain.getSelectItems();
		if(selectItems.size()==1 && selectItems.get(0) instanceof AllColumns) {
			// if select *, then return nothing
			// pass null to the next operations to handle.
			return null;
		}
		
		return selectItems;
	}
	
	public HashMap<String, Expression> findWhereClause(PlainSelect plain, List<String> tableOrder){
		Expression whereClause = plain.getWhere();
		if (whereClause == null) {
			return null;
		}
		ExpressionVisitor visitor = new ExpressionVisitor(whereClause, tableOrder);
		HashMap<String, Expression> expMap = visitor.getExpressions();
		
		return expMap;
		
	}
	
	
}

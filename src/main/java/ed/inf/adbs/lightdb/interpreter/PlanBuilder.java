package ed.inf.adbs.lightdb.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.operator.OperatorFactory;
import ed.inf.adbs.lightdb.operator.impl.DuplicateEliminationOperator;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.impl.SortOperator;
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
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class PlanBuilder {
	private Catalog catalog;
	
	public PlanBuilder(String dbPath) {
		this.catalog = Catalog.getInstance(dbPath);
	}
	
	public void dropInMemory() {
		catalog.dropInMemoryTable();
	}
	
	public List<String> handleAlias(List<String> tableOrder) {
		List<String> newTableOrder = new ArrayList<String>();
		
		for(String tableName: tableOrder) {
			if(tableName.contains(" ")) {
				String[] tableAlias = tableName.split(" ");
				
				// get the original table info
				TableInfo originalTable = catalog.getTable(tableAlias[0]);
				
				// new a aliasTable info
				TableInfo aliasTable = new TableInfo();
				// set tableName as the alias
				aliasTable.setTableName(tableAlias[1]);
				// change the table.column to alias.column
				List<String> columns = new ArrayList<String>();
				for(String col: originalTable.getColumns()) {
					columns.add(tableAlias[1] + "." + col.split("\\.")[1]);
				}
				aliasTable.setColumns(columns);
				// set table path
				aliasTable.setTablePath(originalTable.getTablePath());
				
				aliasTable.setInMemory(true);
				
				catalog.tables.put(tableAlias[1], aliasTable);
				
				newTableOrder.add(tableAlias[1]);
				
			} else {
				newTableOrder.add(tableName);
			}
		}
		
		return newTableOrder;
	}
	
	
	public Operator buildTree(String sql) throws JSQLParserException {
		PlainSelect plain = parseSql(sql);
		
		
		List<String> tableOrder = findTableOrder(plain);
		tableOrder = handleAlias(tableOrder); // handle alias

		List<Operator> scanList = createScanList(tableOrder);
		
		// find where clause
		HashMap<String, Expression> expMap = findWhereClause(plain, tableOrder);		
		List<Operator> filterList = createFilterList(expMap, scanList);
		
		// create join operator
		Operator operator = createJoinOperator(expMap, filterList);  // the operator that after join
		
		// create projection
		List<SelectItem> selectItems = findSelectItems(plain);
		Operator projectOperator = createProjectOperator(selectItems, operator);
		
		// create sortOperator
		List<OrderByElement> orderByElements = plain.getOrderByElements();
		boolean flag = (orderByElements == null)?false:true;
		Operator sortOperator = createSortOperator(orderByElements, projectOperator);
		
		// create distinct
		if(plain.getDistinct() == null) {
			return sortOperator;
		}
		
		if(flag) {
			Operator distinctOperator = new DuplicateEliminationOperator(sortOperator, 
					catalog, orderByElements);
			return distinctOperator;
		}
		
		List<OrderByElement> newOrderByList = new ArrayList<OrderByElement>();
		Operator newSortOperator = new SortOperator(sortOperator, catalog, null);
		Operator distinctOperator = new DuplicateEliminationOperator(newSortOperator, 
				catalog, orderByElements);
		
		
		return distinctOperator;
	}
	
	
	
	public List<Operator> createScanList(List<String> tableOrder) {
		// create scan operator
		List<Operator> scanList = new ArrayList<Operator>();
		for(String tableName: tableOrder) {
			Operator scan = OperatorFactory.getOperator(catalog, tableName);
			scanList.add(scan);
		}
		
		return scanList;
	}
	
	
	public List<Operator> createFilterList(HashMap<String, Expression> expMap,
			List<Operator> scanList) {
		
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
					// otherwise, the numeric exp is useless
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
					Operator filter = OperatorFactory.getOperator(scan, catalog, expMap.get(tableName));  
					filterList.add(filter);
				} else {
					filterList.add(scan);
				}
			}
		}
		
		
		return filterList;
		
	}
	
	
	public Operator createJoinOperator(HashMap<String, Expression> expMap, 
			List<Operator> filterList) {
		Operator operator;  // the operator that after join
		
		if(filterList.size() == 1) {
			// if only involves 1 table
			// the operator is the only element of filterList
			operator = filterList.get(0);
		} else {
			operator = filterList.get(0);
			for(int i=1; i < filterList.size(); i++) {
				String tableName = operator.getTableName() + " " + filterList.get(i).getTableName();
				
				Expression exp;
				if(expMap == null) {
					exp = null;
				} else {
					exp = expMap.get(tableName);
				}
				
				operator = OperatorFactory.getOperator(operator, filterList.get(i), catalog, exp);

			}
		}
		return operator;
		
	}
	
	
	public Operator createProjectOperator(List<SelectItem> selectItems, Operator operator) {
		if(selectItems == null) {
			return operator;
		}
		Operator projectOperator = OperatorFactory.getOperator(operator, catalog, selectItems);
		return projectOperator;	
	}
	
	
	
	public Operator createSortOperator(List<OrderByElement> orderByElements, 
			Operator operator) {
		if(orderByElements == null) {
			return operator;
		}
		
		Operator sortOperator = OperatorFactory.getOperator(operator, catalog, orderByElements);
		return sortOperator;
		
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

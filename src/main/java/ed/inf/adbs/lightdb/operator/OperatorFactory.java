package ed.inf.adbs.lightdb.operator;

import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.operator.impl.JoinOperator;
import ed.inf.adbs.lightdb.operator.impl.ProjectOperator;
import ed.inf.adbs.lightdb.operator.impl.ScanOperator;
import ed.inf.adbs.lightdb.operator.impl.SelectOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.SelectItem;

public class OperatorFactory {
	
	public static Operator getOperator(Catalog catalog, String tableName) {
		return new ScanOperator(catalog, tableName);
	}
	
	public static Operator getOperator(Operator child, Catalog catalog, 
			Expression exp) {
		return new SelectOperator(child, catalog, exp);
	}
	
	public static Operator getOperator(Operator child, Catalog catalog, List<SelectItem> selectItems) {
		return new ProjectOperator(child, catalog, selectItems);
	}
	
	public static Operator getOperator(Operator leftChild, Operator rightChild, 
			Catalog catalog, Expression exp) {
				return new JoinOperator(leftChild, rightChild, catalog, exp);
		
	}

}

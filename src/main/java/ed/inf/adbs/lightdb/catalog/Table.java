package ed.inf.adbs.lightdb.catalog;

import java.util.List;

public class Table {
	/**
	 * Table class to store table information 
	 */
	
	private String tableName;
	private String tablePath;
	private List<String> columns;

	public List<String> getColumns() {
		return columns;
	}
	
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTablePath() {
		return tablePath;
	}

	public void setTablePath(String tablePath) {
		this.tablePath = tablePath;
	}

}

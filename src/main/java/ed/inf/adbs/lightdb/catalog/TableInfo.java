package ed.inf.adbs.lightdb.catalog;

import java.util.List;

/**
 * Table class to store table information 
 * @author zmddzf
 *
 */
public class TableInfo {

	
	private String tableName;  // table name
	private String tablePath;  // the path table stored in
	private List<String> columns;  // list of column names
	private boolean inMemory = false;  // mark the temporary table during the operating process 

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

	public boolean getInMemory() {
		return inMemory;
	}

	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}

}

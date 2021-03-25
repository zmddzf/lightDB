/**
 * 
 */
package ed.inf.adbs.lightdb.operator.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.TableInfo;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.tuple.Tuple;

/**
 * @author zmddzf
 *
 */

/**
 * Scan operator is designed to read data from data files.
 * @author zmddzf
 *
 */
public class ScanOperator extends Operator {
	private TableInfo table;
	private BufferedReader data;
	private String tableName;
	
	/**
	 * Constructor
	 * @param catalog information about the database 
	 * @param tableName the table that applies scan
	 */
	public ScanOperator(Catalog catalog, String tableName) {
		this.table = (TableInfo) catalog.tables.get(tableName);
		this.setTableName(tableName);
	}
	
	
	
	/**
	 * Open the file and set the state true
	 */
	@Override
	public void open() throws FileNotFoundException, IOException {
    	FileReader f = new FileReader(table.getTablePath());
    	this.data = new BufferedReader(f);
    	this.state = true;
	}
	
	/**
	 * Close the file and set the state false
	 */
	@Override
	public void close() throws IOException {
		if (this.state == true) {
			this.data.close();
		}
		this.state = false;
	}
	
	/**
	 * Close the file and then reopen it
	 */
	@Override
	public void reset() {
		if (this.state == true) {
			try {
				this.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			this.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Read line for the data file
	 * return tuple
	 */
	@Override
	public Tuple getNextTuple() {
		try {
			String line = this.data.readLine();
			if (line != null) {
				// split the String to array 
				String[] str_list = line.split(",");
				List<Integer> values = new ArrayList<Integer>();
				for (int i = 0; i < str_list.length; i++) {
					values.add(Integer.valueOf(str_list[i]));
				}
				Tuple tuple = new Tuple(values);
				return tuple;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

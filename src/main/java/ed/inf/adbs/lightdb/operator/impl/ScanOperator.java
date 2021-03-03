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

import ed.inf.adbs.lightdb.catalog.Catalog;
import ed.inf.adbs.lightdb.catalog.Table;
import ed.inf.adbs.lightdb.operator.Operator;
import ed.inf.adbs.lightdb.tuple.Tuple;

/**
 * @author zmddzf
 *
 */
public class ScanOperator extends Operator {
	private Table table;
	private BufferedReader data;
	
	/**
	 * Constructor
	 * @param catalog information about the database 
	 * @param tableName the table that applies scan
	 */
	public ScanOperator(Catalog catalog, String tableName) {
		this.table = (Table) catalog.tables.get(tableName);
	}
	
	/**
	 * Open the file and set the state true
	 */
	public void open() throws FileNotFoundException, IOException {
    	FileReader f = new FileReader(table.getTablePath());
    	this.data = new BufferedReader(f);
    	this.state = true;
	}
	
	/**
	 * Close the file and set the state false
	 */
	public void close() throws IOException {
		if (this.state == true) {
			this.data.close();
		}
		this.state = false;
	}
	
	/**
	 * Close the file and then reopen it
	 */
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
	 * 
	 */
	public Tuple getNextTuple() {
		try {
			String line = this.data.readLine();
			if (line != null) {
				String[] str_list = line.split(",");
				Integer[] values = new Integer[str_list.length];
				for (int i = 0; i < str_list.length; i++) {
					values[i] = Integer.valueOf(str_list[i]);
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
	
}

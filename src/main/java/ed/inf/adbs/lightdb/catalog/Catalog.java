package ed.inf.adbs.lightdb.catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Lazy Singleton for Catalog
 * Use synchronized and volatile to ensure thread safty
 * @author zmddzf
 *
 */
public class Catalog {
	
	// ensure every thread read the instance from the main memory 
	private volatile static Catalog instance = null;
	private String dbPath;
	public Map<String, TableInfo> tables;
	
	/**
	 * Private constructor 
	 * avoid being initialized out of the class
	 * @param dbPath
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private Catalog(String dbPath) {
		this.dbPath = dbPath;
		try {
			this.tables = (this.readTables());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Given tableName and columnName, find out the column index in a tuple
	 * @param tableName
	 * @param columnName
	 * @return index
	 */
	public int getIndex(String tableName, String columnName) {
		int index = tables.get(tableName).getColumns().indexOf(columnName);
		return index;
	}
	
	/**
	 * Given a tableName, find out the table info instance
	 * @param tableName
	 * @return table
	 */
	public TableInfo getTable(String tableName) {
		TableInfo table = tables.get(tableName);
		return table;
	}
	
	/**
	 * Clear the in-memory table info
	 * This method is synchronized, to keep thread safety
	 * When SQL statement was executed, this method need to be called
	 */
	public synchronized void dropInMemoryTable() {
		Iterator<Entry<String, TableInfo>> iterator = tables.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry<String, TableInfo> m = iterator.next();
			if(tables.get(m.getKey()).getInMemory()) {
				iterator.remove();
			}
		}
	}
	
	
	/**
	 * Get the singleton instance
	 * @param dbPath the path of the database
	 * @return singleton instance
	 */
    public static Catalog getInstance(String dbPath){
        // check whether there is a instance
        if(instance == null){
            // keep thread safety
            synchronized (Catalog.class) {
                // check again, if null new a instance
                if(instance == null){
                    instance = new Catalog(dbPath);
                }
            }
        }
        return instance;
    }
    
    /**
     * Read tables info and reture the tables hashmap
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Map<String, TableInfo> readTables() throws FileNotFoundException, IOException {
    	String schemaPath = this.dbPath + "/schema.txt";  // schema path
    	String dataDir = this.dbPath + "/data/";  // datafile dir
    	Map<String, TableInfo> tables = new HashMap();  // return map
    	
    	// read schema
    	FileReader f = new FileReader(schemaPath);
    	BufferedReader schema = new BufferedReader(f);
    	
    	String str = null;
    	while((str = schema.readLine())!=null) {
    		
    		TableInfo table = new TableInfo();
    		
    		String[] info = str.split(" ");
    		String tableName = info[0];
    		String tablePath = dataDir + tableName + ".csv";
    		table.setTableName(tableName);
    		
    		List<String> columns = new ArrayList<String>();
    		for(int i = 1; i < info.length; i++) {
    			columns.add(tableName + "." + info[i]);
    		}
    		
    		table.setColumns(columns);
    		table.setTablePath(tablePath);
    		
    		tables.put(tableName, table);
    	}
    	
    	schema.close();
    	f.close();
    	
		return tables;
    	
    }
}

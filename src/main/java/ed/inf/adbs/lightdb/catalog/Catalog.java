package ed.inf.adbs.lightdb.catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map tables;
	
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
    private Map readTables() throws FileNotFoundException, IOException {
    	String schemaPath = this.dbPath + "/schema.txt";  // schema path
    	String dataDir = this.dbPath + "/data/";  // datafile dir
    	Map<String, Table> tables = new HashMap();  // return map
    	
    	// read schema
    	FileReader f = new FileReader(schemaPath);
    	BufferedReader schema = new BufferedReader(f);
    	
    	String str = null;
    	while((str = schema.readLine())!=null) {
    		
    		Table table = new Table();
    		
    		String[] info = str.split(" ");
    		String tableName = info[0];
    		String tablePath = dataDir + tableName + ".csv";
    		table.setTableName(tableName);
    		
    		List<String> columns = new ArrayList<String>();
    		for(int i = 1; i < info.length; i++) {
    			columns.add(info[i]);
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

/**
 * 
 */
package ed.inf.adbs.lightdb.operator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import ed.inf.adbs.lightdb.tuple.Tuple;

/**
 * @author zmddzf
 *
 */
public abstract class Operator {
	protected boolean state = false;
	protected String tableName;
	
	public Operator() {}
	
	/**
	 * Read the next line from the file and returns the next tuple
	 * @return Tuple type
	 */
	public abstract Tuple getNextTuple();
	
	/**
	 * Close and open again 
	 */
	public abstract void reset();
	
	public abstract void open() throws FileNotFoundException, IOException;
	
	public abstract void close() throws IOException;
	
	public void dump(String outputPath) {
		this.reset();
		if (outputPath==null) {
			Tuple tuple;
			while ((tuple = this.getNextTuple()) != null) {
				System.out.println(tuple.toString());
			}
		} else {
			try {
				BufferedWriter out=new BufferedWriter(new FileWriter(outputPath));
				Tuple tuple;
				while ((tuple = this.getNextTuple())!=null) {
					out.append(tuple.toString());
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}

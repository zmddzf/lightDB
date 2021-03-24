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
	
	// the state of the file, true means open, false means close
	protected boolean state = false;
	// every operator corresponds a table
	protected String tableName;
	
	public Operator() {}
	
	/**
	 * Read the next tuple and returns the tuple after being processed
	 * @return Tuple type
	 */
	public abstract Tuple getNextTuple();
	
	/**
	 * Reset means, close and open. 
	 */
	public abstract void reset();
	
	/**
	 * Open the corresponded file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public abstract void open() throws FileNotFoundException, IOException;
	
	/**
	 * Close the corresponded file
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Output the results
	 * if output path is null, print to the screen
	 * otherwise print to the file
	 * @param outputPath
	 */
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

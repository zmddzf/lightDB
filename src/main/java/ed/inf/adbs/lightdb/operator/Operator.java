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
	
	/**
	 * Read the next line from the file and returns the next tuple
	 * @return Tuple type
	 */
	public Tuple getNextTuple() {
		return null;
	}
	
	/**
	 * Close and open again 
	 */
	public void reset() {
		
	}
	
	public void open() throws FileNotFoundException, IOException {
		this.state = true;
	}
	
	public void close() throws IOException {
		this.state = false;
	}
	
	public void dump(String outputPath) {
		this.reset();
		if (outputPath==null) {
			Tuple tuple;
			while ((tuple = this.getNextTuple())!=null) {
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

}

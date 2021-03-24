package ed.inf.adbs.lightdb.interpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ed.inf.adbs.lightdb.operator.Operator;
import net.sf.jsqlparser.JSQLParserException;

public class Interpreter {
	private String inputPath;  // the path of input SQL
	private String outputPath;  // the path of output csv file
	private PlanBuilder planBuilder;  // the plan builder

	public Interpreter(String dbPath, String inputPath, String outputPath) {
		this.planBuilder = new PlanBuilder(dbPath);
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}
	
	
	/**
	 * Execute the SQL statement
	 * This method is synchronized, the SQL will be executed serially
	 * This is designed to keep thread safety
	 * because dropInMemory call in one thread will annoy other thread
	 */
	public synchronized void execute() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputPath));
			String sql = "";
			String str;
            while ((str = in.readLine()) != null) {
                sql += str;
            }
			
            Operator operator = planBuilder.buildTree(sql);
            operator.reset();
            operator.dump(outputPath);
            
            planBuilder.dropInMemory();
            in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
}

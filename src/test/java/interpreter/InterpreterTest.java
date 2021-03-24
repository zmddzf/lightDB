package interpreter;

import java.io.IOException;

import org.junit.Test;

import ed.inf.adbs.lightdb.interpreter.Interpreter;

public class InterpreterTest {
	
	@Test
	public void execute() {
		for(int i=1; i<=8; i++) {
		    Interpreter interpreter = new Interpreter("samples/db", "samples/input/query"+i+".sql",
			    	"samples/test/query" + i + ".csv");
		    interpreter.execute();
		}
	}
}

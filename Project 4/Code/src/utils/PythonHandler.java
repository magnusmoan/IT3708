package utils;

import java.io.IOException;

public class PythonHandler {

	 public void plotWithPython() {
		 try {
			 String s = Parameters.PYTHON_EXE + " " + Parameters.PYTHON_PATH + " " + Parameters.ALGORITHM + " " + Parameters.FILE_NAME;
			 Runtime.getRuntime().exec(s);
		 } catch (IOException e) {
			e.printStackTrace();
		 }
	 }
}

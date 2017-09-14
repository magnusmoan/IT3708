package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FileHandler {
	
	public ArrayList<String> getFileContent(String path) {
		
		ArrayList<String> content = new ArrayList<String>();
		InputStream input = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		String line;
		try {
			input = new FileInputStream(path);
			reader = new InputStreamReader(input, Charset.forName("UTF-8"));
			br = new BufferedReader(reader);
			while((line = br.readLine()) != null) {
				content.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) { try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} }
		}
		
		return content;
	}
	
	public void writeFile(ArrayList<String> content, String path) {
		
		FileOutputStream out = null;
		File file;
		
		try {
			file = new File(path);
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			out = new FileOutputStream(file);
			
			for(String s : content) {
				byte[] bytesArray = s.getBytes();
				out.write(bytesArray);
			}
			out.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public String[] getFileNames(String folder) {
		return (new File(folder)).list();
	}
	
	public String getMakespanFromFile(String file) {
		String path = Parameters.OUTPUT_FOLDER + file;
		
		return getFileContent(path).get(0).split(" ")[1];
	}
	
	
	public String[] getOptimalValues(int problemNo) {
		return getFileContent(Parameters.OPTIMAL_VALUES_PATH).get(problemNo).split(" ");
	}
	
	public void odtToTxt() {
		String[] fileNames = getFileNames(Parameters.INPUT_FOLDER);
		
		for(String s : fileNames) {
			if(s.contains(".odt")) {
				 try {
					 String cmd = "/usr/bin/textutil -convert txt " + Parameters.INPUT_FOLDER + s;
					 cmd = "textutil -convert txt ../Test Data/1.odt";
					 Runtime.getRuntime().exec(cmd);
				 } catch (Exception e) {
					e.printStackTrace();
				 }
			}
		}
	}
	
	
	
}

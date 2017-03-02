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
	
	public static ArrayList<String> getFileContent(String filename) {
		
		ArrayList<String> content = new ArrayList<String>();
		InputStream input = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		String line;
		try {
			input = new FileInputStream(filename);
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
	
	public static void writeFile(ArrayList<String> content, String filename) {
		
		FileOutputStream out = null;
		File file;
		
		try {
			file = new File(filename);
			
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
	
}

package ga;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class IOHandler {

	
public BufferedImage readImg(String fileName) {
	BufferedImage img = null;
	
	try {
		img = ImageIO.read(new File("../Images/" + Config.FOLDER + "/" + fileName));
	} catch (IOException e) {
		
	}
	
	return img;
}

public void clearDir() {
	File dir = new File(Config.RESULT_FOLDER);
	if(!dir.exists()) {
		dir.mkdir();
	} else {
		for(File file: dir.listFiles()) {
			file.delete();
		}
	};
}

public void writeImg(BufferedImage img, String fileName) {
	try {
		ImageIO.write(img, "png", new File(fileName));
	} catch (IOException e) {
		
	}
}

public void writeObjectives(double[][] obj, String filename) {
	ArrayList<String> output = new ArrayList<>();
	for(int i = 0; i < obj.length; i++) {
		output.add(i+1 + " " + obj[i][0] + " " + obj[i][1] + " " + obj[i][2] + "\n");
	}
	FileOutputStream out = null;
	File file;
	
	try {
		file = new File("../Pareto/" + filename);
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		out = new FileOutputStream(file);
		
		for(String s : output) {
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
	
}

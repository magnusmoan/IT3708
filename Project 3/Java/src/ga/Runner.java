package ga;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Runner {

	public static void main(String[] args) {
		
		IOHandler handler = new IOHandler();
		BufferedImage img = handler.readImg(Config.FILENAME);
		BufferedImage gtImg = null;
		ImgConverter converter = new ImgConverter(img);
		SPEA ga = new SPEA(converter);
		
		ArrayList<int[]> results = ga.run();
		double[][] objectives = ga.calculateObjectives(results);
		for(double[] obj : objectives) {
			for(double d : obj) {
				System.out.print(d + " ");
			}
		}
		handler.clearDir();
		handler.writeObjectives(objectives, Config.PARETO_FRONT);
		for(int i = 0; i < results.size(); i++) {
			int[] seg = ga.getSegmentation(results.get(i));
			converter.alterImg(seg);
			Set<Integer> unique = Arrays.stream(seg).boxed().collect(Collectors.toSet());
			handler.writeImg(img, Config.RESULT_FOLDER + unique.size() + " seg.png");
			gtImg = Helpers.deepCopy(img);
			converter = new ImgConverter(gtImg);
			converter.setGroundTruth();
			handler.writeImg(gtImg,  Config.RESULT_FOLDER + unique.size() + " ground truth.png");
			img = Helpers.deepCopy(img);
			converter = new ImgConverter(img);
		}
		Helpers.plotWithPython();
	}
	
}

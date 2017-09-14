package ga;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class ImgConverter {
	
	private BufferedImage img;
	private int width, height, pixels;
	private ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes;

	
	public ImgConverter(BufferedImage img) {
		this.img = img;
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.pixels = width*height;
		nodes = new ArrayList<>();
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color rgb = new Color(img.getRGB(x, y));
				int[] info = new int[4];
				info[0] = getNumberOfEdges(x, y);
				info[1] = rgb.getRed();
				info[2] = rgb.getGreen();
				info[3] = rgb.getBlue();
				
				NodeInfo<int[], ArrayList<Edge>> currNodeInfo = new NodeInfo<>(info, null);
				nodes.add(currNodeInfo);
			}
		}
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				nodes.get(y*width+x).setNeighbors(addEdges(x, y));
			}
		}
	}
	
	public ArrayList<NodeInfo<int[], ArrayList<Edge>>> getNodes() {
		return nodes;
	}
	
	public int getNumberOfPixels() {
		return pixels;
	}
	
	public ArrayList<Edge> getEdges(int n) {
		return nodes.get(n).getNeighbors();
	}
	
	public void alterImg(int[] nodeToSeg) {
		for(int node = 0; node < pixels; node++) {
			int currSeg = nodeToSeg[node];
			for(Edge edge : nodes.get(node).getNeighbors()) {
				if(nodeToSeg[edge.getEnd()] != currSeg) {
					img.setRGB(node%width, node/width, Config.EDGE_COLOR.getRGB());
					break;
				}
			}
		}
	}
	
	public void alterImg(HashMap<Integer, ArrayList<Integer>> kmeans) {
		Color[] colors = new Color[] {new Color(102, 205, 118), new Color(194, 172, 215), new Color(255, 190, 124), new Color(254, 255, 137), new Color(41, 107, 181),
				new Color(255, 0, 128), new Color(204, 84, 0), new Color(102, 102, 102)};
		int i = 0;
		for(int k : kmeans.keySet()) {
			Color c = colors[i];
			for(int node : kmeans.get(k)) {
				img.setRGB(node%width, node/width, c.getRGB());
			}
			i++;
		}
	}
	
	public void setGroundTruth() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color current = new Color(img.getRGB(x, y));
				if(current.equals(Config.EDGE_COLOR)) {
					img.setRGB(x,y, Config.BLACK.getRGB());
				} else {
					img.setRGB(x, y, Config.OFFWHITE.getRGB());
				}
			}
		}
	}
	
	private ArrayList<Edge> addEdges(int x, int y) {
		ArrayList<Edge> neighbors = new ArrayList<>();
		if(x == 0 && y == 0) {
			Edge first = new Edge(0, 1, getEdgeWeight(0, 1));
			Edge second = new Edge(0, width, getEdgeWeight(0, width));
			neighbors.add(first); neighbors.add(second);
		} else if(x == width-1 && y == 0) {
			Edge first = new Edge(width-1, width-2, getEdgeWeight(width-1, width-2));
			Edge second = new Edge(width-1, (2*width)-1, getEdgeWeight(width-1, (2*width)-1));
			neighbors.add(first); neighbors.add(second);
		} else if(x == 0 && y == height-1) {
			Edge first = new Edge(width*(height-1), (width*(height-1))+1, getEdgeWeight(width*(height-1), (width*(height-1))+1));
			Edge second = new Edge(width*(height-1), width*(height-2), getEdgeWeight(width*(height-1), width*(height-2)));
			neighbors.add(first); neighbors.add(second);
		} else if(x == width-1 && y == height-1) {
			Edge first = new Edge((width*height)-1, (width*height)-2, getEdgeWeight((width*height)-1, (width*height)-2));
			Edge second = new Edge((width*height)-1, width*(height-1)-1, getEdgeWeight((width*height)-1, width*(height-1)-1));
			neighbors.add(first); neighbors.add(second);
		} else if(y == 0) {
			Edge first = new Edge(x, x-1, getEdgeWeight(x, x-1));
			Edge second = new Edge(x, x+1, getEdgeWeight(x, x+1));
			Edge third = new Edge(x, x+width, getEdgeWeight(x, x+width));
			neighbors.add(first); neighbors.add(second); neighbors.add(third);
		} else if(y == height-1) {
			Edge first = new Edge(width*(height-1)+x, width*(height-1)+x-1, getEdgeWeight(width*(height-1)+x, width*(height-1)+x-1));
			Edge second = new Edge(width*(height-1)+x, width*(height-1)+x+1, getEdgeWeight(width*(height-1)+x, width*(height-1)+x+1));
			Edge third = new Edge(width*(height-1)+x, width*(height-2)+x, getEdgeWeight(width*(height-1)+x, width*(height-2)+x));
			neighbors.add(first); neighbors.add(second); neighbors.add(third);
		} else if(x == 0) {
			Edge first = new Edge(y*width, y*width-width, getEdgeWeight(y*width, y*width-width));
			Edge second = new Edge(y*width, y*width+width, getEdgeWeight(y*width, y*width+width));
			Edge third = new Edge(y*width, y*width+1, getEdgeWeight(y*width, y*width+1));
			neighbors.add(first); neighbors.add(second); neighbors.add(third);
		} else if((x+1) % width == 0) {
			Edge first = new Edge((y+1)*width-1, y*width-1, getEdgeWeight((y+1)*width-1, y*width-1));
			Edge second = new Edge((y+1)*width-1, (y+2)*width-1, getEdgeWeight((y+1)*width-1, (y+2)*width-1));
			Edge third = new Edge((y+1)*width-1, (y+1)*width-2, getEdgeWeight((y+1)*width-1, (y+1)*width-2));
			neighbors.add(first); neighbors.add(second); neighbors.add(third);
		} else {
			Edge first = new Edge(y*width+x, y*width+x-1, getEdgeWeight(y*width+x, y*width+x-1));
			Edge second = new Edge(y*width+x, y*width+x+1, getEdgeWeight(y*width+x, y*width+x+1));
			Edge third = new Edge(y*width+x, y*width+x-width, getEdgeWeight(y*width+x, y*width+x-width));
			Edge fourth = new Edge(y*width+x, y*width+x+width, getEdgeWeight(y*width+x, y*width+x+width));
			neighbors.add(first); neighbors.add(second); neighbors.add(third); neighbors.add(fourth);
		}
		return neighbors;
	}
	
	private int getNumberOfEdges(int x, int y) {
		if((x == 0 && (y == 0 || y == height-1)) || (x == width-1 && (y == 0 || y == height-1))) {
			return 2;
		} else if(y == 0 || y == height-1) {
			return 3;
		} else {
			return 4;
		}
	}
	
	private double getEdgeWeight(int n1, int n2) {
		int r1 = nodes.get(n1).getInfo()[1];
		int g1 = nodes.get(n1).getInfo()[2];
		int b1 = nodes.get(n1).getInfo()[3];
		int r2 = nodes.get(n2).getInfo()[1];
		int g2 = nodes.get(n2).getInfo()[2];
		int b2 = nodes.get(n2).getInfo()[3];
		return calcEdgeWeight(r1, r2, g1, g2, b1, b2);
	}
	
	private double calcEdgeWeight(int r1, int r2, int g1, int g2, int b1, int b2) {
		return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
	}	
}

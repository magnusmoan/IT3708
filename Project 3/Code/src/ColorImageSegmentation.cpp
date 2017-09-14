//============================================================================
// Name        : ColorImageSegmentation.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <string.h>
#include "JPEGReader.h"
#include "JPEGWriter.h"
#include "Node.h"
#include "Edge.h"
using namespace std;


vector<Node> addNodes(int width, int height, int size, unsigned char *data);
void addEdges(vector<Node> &nodes, int height, int width, int size);
unsigned char* nodesToChar(vector<Node> nodes);
void checkAndSetBorderAllNodes(vector<Node> &nodes, vector<int> segment);

int main() {
	const char* fileName = "../Test Image/1/Test image.jpg";
	JPEGReader reader(fileName);
	unsigned char * data = reader.getData();
	unsigned int size = reader.getSize();
	unsigned int width = reader.getWidth();
	unsigned int height = reader.getHeight();
	vector<Node> nodes = addNodes(width, height, size, data);
	addEdges(nodes, width, height, size);



	unsigned char * outdata = nodesToChar(nodes);
	/*
	char a = outdata[0];
	char b = outdata[1];
	char c = outdata[2];
	cout << (int)a << " " << (int)b << " " << (int)c << endl;*/
	//int array[27] = {7, 185, 245, 7, 90, 245, 62, 59, 97, 47, 217, 28, 179, 217, 28, 217, 129, 28, 217, 28, 210, 179, 134, 177, 77, 37, 75};
	//int foo[27] = {7, 185, 245, 7, 90, 245, 62, 59, 97, 47, 217, 287, 185, 245, 7, 90, 245, 180, 217, 28, 210, 179, 134, 177, 77, 37, 75};

	int array[12] = {8, 31, 47, 8, 31, 47, 8, 31, 47, 8, 31, 47};
	unsigned char * outdata1 = reinterpret_cast<unsigned char*>(array);
	cout << (int) outdata[2] << endl;
	cout << (int) outdata1[2] << endl;
	const char* outputFile = "test1";
	//JPEGWriter writer(outputFile, outdata, width, height, size);
	JPEGWriter writer(outputFile, outdata1, 2,2, 4);
	writer.write();
	//free(outdata);
	free(data);

	return 0;
}

vector<Node> addNodes(int width, int height, int size, unsigned char *data) {
	vector<Node> nodes;
	nodes.reserve(size/3);
	for(int i = 0; i < size; i += 3) {
		int r = data[i];
		int g = data[i+1];
		int b = data[i+2];
		int pixel = i/3;
		int nE;

		if(pixel == 0 || pixel == size-1 || pixel == width-1 || pixel == ((height-1)*width)) {
			nE = 2;
		} else if(pixel < width || pixel > (width*(height-1)) || pixel % width == 0 || (pixel+1) % width == 0) {
			nE = 3;
		} else {
			nE = 4;
		}
		Node node(r,g,b,nE,pixel);
		nodes.push_back(node);
	}

	return nodes;
}


void addEdges(vector<Node> &nodes, int height, int width, int size) {
	for(int n = 0; n < size/3; n += 1) {
		Node &node = nodes[n];
		int id = node.getId();
		if(id == 0) {
			Edge edge1(node, nodes[n+1]);
			Edge edge2(node, nodes[n+width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
		} else if(id == width-1) {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n+width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
		} else if(id == width*(height-1)) {
			Edge edge1(node, nodes[n+1]);
			Edge edge2(node, nodes[n-width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
		} else if(id == (size/3)-1) {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n-width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
		} else if(id % width == 0) {
			Edge edge1(node, nodes[n-width]);
			Edge edge2(node, nodes[n+width]);
			Edge edge3(node, nodes[n+1]);
			node.addEdge(edge1);
			node.addEdge(edge2);
			node.addEdge(edge3);
		} else if(id < width) {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n+width]);
			Edge edge3(node, nodes[n+1]);
			node.addEdge(edge1);
			node.addEdge(edge2);
			node.addEdge(edge3);
		} else if((id+1) % width == 0) {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n+width]);
			Edge edge3(node, nodes[n-width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
			node.addEdge(edge3);
		} else if(id >= (height-1)*width) {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n+1]);
			Edge edge3(node, nodes[n-width]);
			node.addEdge(edge1);
			node.addEdge(edge2);
			node.addEdge(edge3);
		} else {
			Edge edge1(node, nodes[n-1]);
			Edge edge2(node, nodes[n+width]);
			Edge edge3(node, nodes[n-width]);
			Edge edge4(node, nodes[n+1]);
			node.addEdge(edge1);
			node.addEdge(edge2);
			node.addEdge(edge3);
			node.addEdge(edge4);
		}
	}
}

unsigned char * nodesToChar(vector<Node> nodes) {
	unsigned char* data;
	data = (unsigned char *)malloc(nodes.size()*3);
	for(int i = 0; i < nodes.size(); i += 1) {
		Node &node = nodes[i];
		data[i*3] = (unsigned char) node.getR();
		data[(i*3)+1] = (unsigned char) node.getG();
		data[(i*3)+2] = (unsigned char) node.getB();
	}

	return data;
}

void checkAndSetBorderAllNodes(vector<Node> &nodes, vector<int> segment) {
	for(int n = 0; n < nodes.size(); n += 1) {
		Node &node = nodes[n];
		if(node.getId() == 77921) { cout << "BAZ" << endl; }
		node.checkAndSetBoundary(segment);
	}
}

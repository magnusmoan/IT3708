/*
 * Node.h
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#ifndef NODE_H_
#define NODE_H_

#include <vector>
class Edge;

class Node {
private:
	int r, g, b, nE, id;
	std::vector<Edge> edges;
	bool isInSegment(std::vector<int> segment, int node);
	bool isBoundary(std::vector<int> segment);
	void setBoundaryColor();
	bool isInSegment(std::vector<int> segment);

public:
	Node(int r, int g, int b, int nE, int id);
	void addEdge(Edge edge);
	int getR() const;
	int getG() const;
	int getB() const;
	void checkAndSetBoundary(std::vector<int> segment);
	std::vector<Edge> getEdges();
	void setId(int newId);
	int getId();
};

#endif /* NODE_H_ */

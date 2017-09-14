/*
 * Edge.h
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#ifndef EDGE_H_
#define EDGE_H_

class Node;

class Edge {
private:
	double weight;
	Node *start;
	Node *end;
	double calcWeight(const Node &start, const Node &end);

public:
	Edge(Node &start, Node &end);
	Node getStart();
	Node getEnd();
	double getWeight();
};

#endif /* EDGE_H_ */

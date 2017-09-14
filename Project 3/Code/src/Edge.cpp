/*
 * Edge.cpp
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#include <math.h>
#include "Edge.h"
#include "Node.h"


Edge::Edge(Node &start, Node &end) {
	this->start = &start;
	this->end = &end;
	this->weight = calcWeight(start, end);
}

Node Edge::getStart() {
	return *start;
}
Node Edge::getEnd() {
	return *end;
}
double Edge::getWeight() {
	return weight;
}

double Edge::calcWeight(const Node &start, const Node &end) {
	int r1, r2, g1, g2, b1, b2;
	r1 = start.getR();
	r2 = end.getR();
	g1 = start.getG();
	g2 = end.getG();
	b1 = start.getB();
	b2 = end.getB();

	return sqrt(pow(r2-r1,2) + pow(g2-g1,2) + pow(b2-b1,2));
}


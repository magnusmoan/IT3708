/*
 * Node.cpp
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#include <iostream>
#include "Node.h"
#include "Edge.h"

const int RB = 31;
const int GB = 230;
const int BB = 9;

Node::Node(int r, int g, int b, int nE, int id) {
	this->r = r;
	this->g = g;
	this->b = b;
	this->nE = nE;
	this->id = id;
	edges.reserve(nE);
}

void Node::addEdge(Edge edge) {
	edges.push_back(edge);
}

int Node::getR() const{
	return r;
}

int Node::getG() const{
	return g;
}

int Node::getB() const{
	return b;
}

int Node::getId() {
	return id;
}

void Node::setId(int newId) {
	id = newId;
}

std::vector<Edge> Node::getEdges() {
	return edges;
}

bool Node::isBoundary(std::vector<int> segment) {
	for(int e = 0; e < nE; e += 1) {
		int first = edges[e].getStart().getId();
		int second = edges[e].getEnd().getId();
		int curr;

		if(first == id) {curr = second;}
		else {curr = first;}

		//if(id == 77921) { std::cout << nE << std::endl; }

		if(!isInSegment(segment, curr)) {
			if(id == 77921) { std::cout << "Is boundary!" << std::endl; }
			return true;
		}
	}
	if(id == 77921) { std::cout << "Not boundary!" << std::endl; }
	return false;
}

bool Node::isInSegment(std::vector<int> segment, int node) {
	if(id == 77921) { std::cout << node << std::endl; }
	for(int n = 0; n < segment.size(); n+= 1) {
		int curr = segment[n];

		if(curr == node) {
			return true;
		}
	}
	return false;
}

void Node::checkAndSetBoundary(std::vector<int> segment) {
	if(isInSegment(segment)) {
		if(isBoundary(segment)) {
			setBoundaryColor();
		}
	}
}

void Node::setBoundaryColor() {
	r = RB;
	g = GB;
	b = BB;
}

bool Node::isInSegment(std::vector<int> segment) {
	for(int i = 0; i < segment.size(); i+=1) {
		if(segment[i] == id) {
			if(id == 77921) { std::cout << "foo" << std::endl; }
			return true;
		}
	}
	return false;
}

/*
 * Chromosome.h
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#ifndef CHROMOSOME_H_
#define CHROMOSOME_H_

#include <vector>
#include <string>

class Chromosome {
private:
	std::vector< std::vector<int> > segments;
	bool changed;
	double fitness;
	int nSeg;
public:
	void calcFitness();
	double getFitness();
	void randomInitialize();
	std::vector<std::string> getPrintableChromosome();
};

#endif /* CHROMOSOME_H_ */

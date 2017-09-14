/*
 * JPEGWriter.h
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#ifndef JPEGWRITER_H_
#define JPEGWRITER_H_

class JPEGWriter {
public:
	JPEGWriter(const char* fileName, const unsigned char * data, int width, int height, int size);
	void write();
private:
	const char* fileName;
	const unsigned char * data;
	int width, height, size;
};

#endif /* JPEGWRITER_H_ */

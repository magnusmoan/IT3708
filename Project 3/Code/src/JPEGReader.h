/*
 * JPEGReader.h
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#include <jpeglib.h>
#include <jerror.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>

#ifndef JPEGREADER_H_
#define JPEGREADER_H_

class JPEGReader {
public:
	JPEGReader(const char* FileName);
	unsigned char * getData();
	unsigned int getSize();
	unsigned int getWidth();
	unsigned int getHeight();

private:
	unsigned int x, y, data_size, type;
	int channels;
	unsigned char * rowptr[1];
	unsigned char * jdata;
	struct jpeg_decompress_struct info;
	struct jpeg_error_mgr err;


};


#endif /* JPEGREADER_H_ */

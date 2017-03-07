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
	JPEGReader(char* FileName);

private:
	unsigned long x, y;
	unsigned long data_size;
	int channels;
	unsigned int type;
	unsigned char * rowptr[1];
	unsigned char * jdata;
	struct jpeg_decompress_struct info;
	struct jpeg_error_mgr err;


};


#endif /* JPEGREADER_H_ */

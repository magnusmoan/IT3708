/*
 * JPEGReader.cpp
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#include <jpeglib.h>
#include <jerror.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include "JPEGReader.h"

JPEGReader::JPEGReader(char* FileName) {
	FILE* file = fopen(FileName, "rb");
	info.err = jpeg_std_error(& err);
	jpeg_create_decompress(& info);

	if(!file) {
		fprintf(stderr, "Error reading JPEG file %s!", FileName);
	}

	jpeg_stdio_src(&info, file);
	jpeg_read_header(&info, TRUE);
	jpeg_start_decompress(&info);

	x = info.output_width;
	y = info.output_height;
	channels = info.num_components;
	type = 3;
	if(channels == 4) type = 4;

	data_size = x * y * 3;

	jdata = (unsigned char *)malloc(data_size);
	while (info.output_scanline < info.output_height) {
		rowptr[0] = (unsigned char *)jdata +
	            3* info.output_width * info.output_scanline;

	    jpeg_read_scanlines(&info, rowptr, 1);
	  }

	jpeg_finish_decompress(&info);
	jpeg_destroy_decompress(&info);
	fclose(file);
	free(jdata);
}

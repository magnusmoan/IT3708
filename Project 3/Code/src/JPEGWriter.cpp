/*
 * JPEGWriter.cpp
 *
 *  Created on: Mar 7, 2017
 *      Author: Moan
 */

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include "JPEGWriter.h"

JPEGWriter::JPEGWriter(const char * fileName, const unsigned char * data, int width, int height, int size) {
	this->fileName = fileName;
	this->data = data;
	this->width = width;
	this->height = height;
	this->size = size;
}

void JPEGWriter::write() {
	FILE *imageFile;

	char str1[30];
	strcpy(str1, fileName);
	strcat(str1, ".ppm");

	imageFile=fopen(str1,"wb");
	if(imageFile == NULL) {
		perror("ERROR: Cannot open output file");
	    exit(EXIT_FAILURE);
	}

	fprintf(imageFile,"P6\n");
	fprintf(imageFile,"%d %d\n", width, height);
	fprintf(imageFile,"255\n");

	fwrite(data, 1, size, imageFile);
	fclose(imageFile);

	char str4[30];
	strcpy(str4, "rm ");
	strcat(str4, fileName);
	strcat(str4, ".jpg");
	system(str4);

	char str2[90];
	strcpy (str2,"sips -s format jpeg ");
	strcat (str2,str1);
	strcat (str2," --out ");
	strcat (str2,fileName);
	strcat (str2,".jpg");
	strcat (str2," > /dev/null");
	puts (str2);
	system(str2);

	char str3[30];
	strcpy(str3, "rm ");
	strcat(str3, str1);
	system(str3);
}

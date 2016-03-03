#ifndef _LIB_IO_FILE_
#define _LIB_IO_FILE_

// Add #include statements and constants here

int io_fileOpen_TT(char* fileName, char* Mode) ;

int io_fileClose_I(int file) ;

int io_fileWrite_IT(int file, char* data) ;

int io_fileWrite_II(int file, int data) ;

int io_fileWrite_IF(int file, float data) ;

int io_fileWrite_IJ(int file, long data) ;

int io_fileWrite_ID(int file, double data) ;

int io_fileWrite_IS(int file, short data) ;

#endif


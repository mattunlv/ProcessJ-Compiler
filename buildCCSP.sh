#!/bin/bash

#Compile and install CCSP library for ProcessJ compiler to use.
#Untar Library.
tar -xzvf CCSP.tar.gz

#Compile CCSP Library.
cd CCSP/
ccspPath="$(realpath ../lib/CCSP)"
./configure --build=i686-pc-linux-gnu "CFLAGS=-m32" "CXXFLAGS=-m32" "LDFLAGS=-m32" --prefix=$ccspPath
make
make install # :)
cd ../

#Clean up:
rm -rf CCSP

echo "done"

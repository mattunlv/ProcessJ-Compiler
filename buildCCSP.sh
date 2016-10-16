#!/bin/bash

#Compile and install CCSP library for ProcessJ compiler to use.

# We require the absolute path for the --prefix flag of configure below. If realpath is not available
# please erase this error check and type entire path yourself.
msg="Error: Build requires realpath but it's not avaliable."

tar -xzvf CCSP.tar.gz

# This is necessary as git does not allow empty directories to be commited. Create directory on the fly.
mkdir lib/CCSP

# This will fail if realpath is not avaliable...
command -v realpath >/dev/null 2>&1 || { echo >&2 $msg; exit 1; }
ccspPath="$(realpath lib/CCSP)"
cd CCSP/

#Compile CCSP Library.
./configure --build=i686-pc-linux-gnu "CFLAGS=-m32" "CXXFLAGS=-m32" "LDFLAGS=-m32" --prefix=$ccspPath
make
make install # :)
cp include/cif.h $ccspPath/include/kroc/
cd ../

#Clean up:
rm -rf CCSP

echo "done"

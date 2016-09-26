#!/bin/bash

function help() {
  printf "The options are:\n"
  printf "\n"
  printf "    JVM  : produces a java tar file to be run on the JVM.\n"
  printf "    C    : produces binary executables using gcc and the CCSP occam runtime.\n"
  printf "    C++  : produces binary executables using cooperative ProcessJ scheduler.\n"
}

homedir=~
eval homedir=$homedir

if [ "$#" -eq 0 ] ; then
  if [ -f "$homedir/.pjconfig" ] ; then
    printf "\nThe current target is: "
    grep target $homedir/.pjconfig | cut -d '=' -f 2
    printf "\n"
    help
    printf "\nTo change the target specify one of the options on the command line to switchPJ.\n"
  else
    printf "ProcessJ is not installed for you. Please run the following command:\n"
    #more to come
  fi
else
  if [ "$1" = "C" ] || { [ "$1" = "C++" ] || [ "$1" == "JVM" ]; } ; then
    grep -v "target" $homedir/.pjconfig > $homedir/.pjconfig-new
    echo "target=$1" >> $homedir/.pjconfig-new
    rm $homedir/.pjconfig
    mv $homedir/.pjconfig-new $homedir/.pjconfig
    printf "The new target is: "
    grep target $homedir/.pjconfig | cut -d '=' -f 2
  else
    echo "'$1' is not a valid target - try again.\n"
  fi
fi

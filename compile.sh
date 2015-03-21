#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "$DIR"
if [ -d classes ]; then
	echo "If you just downloaded the git repo, please move all of the files from the classes folder into the current directory or compile the 
Compiler.java yourself."
	echo "Once you've done that, delete the folder."
else
	java Compiler $@
fi

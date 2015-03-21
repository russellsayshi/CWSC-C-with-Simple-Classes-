# CWSC-C-with-Simple-Classes-
A short one-day project I hacked together in Java to allow simple class creation in C. It allows compilation of files to C.

##Warnings
CWSC does not yet have preprocessor support. To compile properly with imported classes and files, as well as preprocessor definitions, I recommend using `gcc -E` to generate preprocessed file output first. MSVC also has an `/E` option for getting preprocessor output. That can then be fed to this application.

Additionally, as this application does not have preprocessor support, it won't detect included files, but depends on `stdlib.h`. Make sure to include that before attempting to compile.

##Usage
Usage of CWSC is very simple.

Simply type `java Compiler <files...>`, or, if you hate typing `java` like I do, use the `compile.sh` wrapper included.

##Syntax
To create a class, use `class <name> {...}`. It's pretty simple.

Once in a class, you can add instance variables and functions.

Demo:
```
class neat {
  int a;
  char b(int c) {
    return 'd';
  }
}
```

That's all there is to class creation.
From another function, you can do the following:
```
neat varName;
varName->a = 5;
varName.b(4);
free(varName);
```
Notice how functions are called with dot notation but variables with the arrow notation.

Additionally, all classes are dynamically allocated when created, and can be freed just like anything else with the free function.

###Context
From within a function, a the class's instance variables can be referenced.

This can be done with the `self` variable. For example, we could rewrite the function `b` on our `neat` class above to be the following:
```
int b(int c) {
  return self->a;
}
```
Now it will return the value of item `a` stored in the class.

##Examples
The following demonstrates the compilation from CWSC to C.

This file (samplestart.c):
```C
#include <stdio.h>
#include <stdlib.h>

class neat {
	int wow(char a) {
		if(a == 'a') {
			return 3;
		} else {
			return 2;
		}
	}
	int socool(int b) {
		printf("%d\n", b);
		printf("How cool! %d\n", self->var);
	}
	int var;
}
int main(int argc, char* argv) {
        neat a;
        if(a.wow('a')==3) {
		printf("NEAT");
	}
	a.socool(53);
	a->var = 5;
	a.socool(54);
	return 0;
}
```
becomes when compiled:
```C
#include <stdio.h>
#include <stdlib.h>
struct neat {
int var;
};
int __neat_wow(struct neat* self, char a) { 
if(a == 'a') {
return 3;
} else {
return 2;
}
}

int __neat_socool(struct neat* self, int b) { 
printf("%d\n", b);
printf("How cool! %d\n", self->var);
}

int main(int argc, char* argv) {
struct neat* a = malloc(sizeof(struct neat));
if( __neat_wow(a, 'a')==3) {
printf("NEAT");
}
 __neat_socool(a, 53);
a->var = 5;
 __neat_socool(a, 54);
return 0;
}
```
Notice that all of the spacing is removed, and that class functions get named `__className_functionName`. Also notice how the `self` value is passed to all functions.

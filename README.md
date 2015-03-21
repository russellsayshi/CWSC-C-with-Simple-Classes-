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
```
Notice how functions are called with dot notation but variables with the arrow notation.

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

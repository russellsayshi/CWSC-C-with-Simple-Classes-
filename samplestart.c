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

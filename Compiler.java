import java.util.Scanner;
import java.util.ArrayList;
import java.util.Stack;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class Compiler {
	public static void main(String[] args) {
		/*Scanner s = new Scanner(System.in);
		while(s.hasNextLine()) {
			/*String str = s.nextLine();
			if(str.equals("EOF")) {
				break;
			}
			boolean a = str.charAt(0) == 'T';
			boolean b = str.charAt(1) == 'T';
			System.out.println(stripOfComments(a, str.substring(2), b));
			* /
		}*/
		if(args.length < 1) {
			System.out.println("Please specify filenames as arguments.");
		} else {
			for(int i = 0; i < args.length; i++) {
				try {
					compileFile(args[i]);
				} catch(FileNotFoundException fnfe) {
					System.out.println("File " + args[i] + " not found.");
					System.exit(2);
				}
			}
		}
	}
	/*public static void compileFile(String filename) {
		Scanner s = new Scanner(new File(filename));
		
	}*/
	private static String stripOfComments(boolean isAlreadyInComment, String line, boolean wasAlreadyInComment) { //first character returned is error code, ! if none, second character returned is . if block comment ends, ! if no, , if it begins
		int singline = line.indexOf("//");
		int multiline = line.indexOf("/*");
		int endmultiline = line.indexOf("*/");
		char doesBlockCommentEnd = wasAlreadyInComment? '.' : '!';
		if(isAlreadyInComment || wasAlreadyInComment) {
			if(endmultiline != -1) {
				return stripOfComments(false, line.substring(endmultiline+2), true);
			}
		}
		if(endmultiline != -1) {
			if(endmultiline < multiline) {
				if(!isAlreadyInComment) {
					return "0!" + line;
				} else {
					return stripOfComments(false, line.substring(endmultiline+2), true);
				}
			} else {
				if(multiline == -1) {
					return "1!" + line;
				}
				return stripOfComments(isAlreadyInComment, line.substring(0, multiline) + line.substring(endmultiline+2), false);
			}
		}
		if(multiline != -1) {
			return "" + '!' + ',' + line.substring(multiline+2);
		}
		if(singline != -1) {
			return "" + '!' + doesBlockCommentEnd + line.substring(0, singline);
		}
		if(isAlreadyInComment) {
			return "" + '!' + '.';
		}
		return "" + '!' + doesBlockCommentEnd + line;
	}
	public static String compileFile(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new File(filename));
		//Scanner s = new Scanner(System.in);
		boolean isInBlockComment = false;
		String line;
		//System.out.println("REALLY NEAT");
		int lineNum = 0;
		boolean isInClassDeclaration = false;
		ArrayList<ClsStr> classes = new ArrayList<ClsStr>();
		int nextClassIndex = 0;
		int curlyBraceCounter = 0;
		Stack<String> nextStatementStack = new Stack<String>();
		Stack<VariableScopeStruct> currentScope = new Stack<VariableScopeStruct>();
		VariableScopeStruct global = new VariableScopeStruct();
		currentScope.push(global);
		boolean statementIsUnaltered;
		boolean isInMethodDeclaration = false;
		String currentClassName = "";
		PrintWriter output = new PrintWriter(new File(filename + ".compiled.c"));
		ArrayList<StringBuffer> methodBuffers = new ArrayList<StringBuffer>();
		while(s.hasNextLine() || !nextStatementStack.isEmpty()) {
			//System.out.println("WHAT AN IDEA");
			if(nextStatementStack.isEmpty()) {
				line = s.nextLine();
				statementIsUnaltered = true;
			} else {
				line = nextStatementStack.pop();
				statementIsUnaltered = false;
			}
			//System.out.println("NEAT");
			line = stripOfComments(isInBlockComment, line, false);
			if(line.charAt(0) != '!') { //comment error
				System.out.println("Error on line " + lineNum + ": comment error " + line.charAt(0));
				System.exit(1);
			}
			if(line.charAt(1) != '!') { //block comment began or ended
				isInBlockComment = line.charAt(1) == ',';
			}
			line = line.substring(2).trim();
			if(line.equals("")) {
				continue;
			}
			if(line.length() > 10 && line.charAt(0) == '#') {
				if(line.substring(1, 8).equals("include")) {
					char[] include = line.substring(8).trim().toCharArray();
					if(include[0] == '"') {
						for(int i = 1; i < include.length; i++) {
							if(include[i] == '"') {
								System.out.println("Note: this does not process included files. To do so, (assuming gcc), use the -E flag to get preprocessor output, and then run the result of that through this compiler. Line number: " + line.substring(8).trim().substring(1, i));
								continue;
							}
						}
					}
				}
			}
			//System.out.println("COOL");
			/*if(statementIsUnaltered) {
				int semicolon = line.indexof(";");
				if(semicolon != -1) {
					
				} else {
					while(s.hasNextLine()) {
						line += s.nextLine();
						lineNum++;
						char[] carr = line.toCharArray();
						/*while(carr[carr.length-1] == '\\') {
							if(s.hasNextLine()) {
								line += s.nextLine();
								lineNum++;
								carr = line.toCharArray();
							}
						}* /
						for(int i = 0; i < carr.length; i++) {
							if(carr[i] == '\'') {
								if(i < carr.length - 1) {
									if(carr[i+1] == '\\') {
										i++;
									}
								}
								if(i < carr.length - 1) { //not redundant
									if(carr[i+1] != '\'') {
										System.out.println("Invalid character declaration.");
										System.exit(7);
									}
								}
							} else if(carr[i] == '"') {
								while(i < carr.length) {
									if(carr[i] == '\'') {
										i++;
										continue;
									}
									if(carr[i] == '"') {
										break;
									}
								}
							}
							if(carr[i] == ';' && i > 0 && carr[i-1] != '\\') {
								
							}
						semicolon = line.indexOf(";");
					}
					continue;
				}
			}*/
			boolean hasAlreadyPrinted = false;
			String[] strarray = line.split(" ");
			//if(isInClassDeclaration) {
			//	System.out.println(java.util.Arrays.toString(strarray));
			//}
			if(strarray[0].equals("class")) {
				if(isInClassDeclaration) {
					System.out.println("No nested class declarations allowed");
					System.exit(4);
				}
				curlyBraceCounter = 1;
				isInClassDeclaration = true;
				ClsStr cls = new ClsStr();
				VariableScopeStruct vss = new VariableScopeStruct();
				vss.parent = cls;
				cls.name = strarray[1];
				currentClassName = strarray[1];
				classes.add(cls);
				if(!strarray[2].equals("{")) {
					output.println("Error on line " + lineNum + ": missing curly brace");
					System.exit(3);
				}
				output.println("struct " + strarray[1] + " {");
				hasAlreadyPrinted = true;
			}
			if(isInClassDeclaration && !hasAlreadyPrinted) {
				char[] carr = line.toCharArray();
				for(int i = 0; i < carr.length; i++) {
					if(carr[i] == '\'') {
						if(i+1 < carr.length && carr[i+1] == '\\') {
							i++;
						}
						i+=2;
						if(i < carr.length && carr[i] != '\'') {
							System.out.println("Crazy character declaration on line: " + lineNum + ". No idea what's happening.");
							System.exit(9);
						}
					}
					if(carr[i] == '"') {
						for(int o = i+1; o < carr.length; o++) {
							if(carr[o] == '"' && carr[o+1] != '\\') {
								break;
							}
						}
					}
					if(carr[i] == '{') {
						if(curlyBraceCounter++ == 1) {
							isInMethodDeclaration = true;
							methodBuffers.add(new StringBuffer());
							int bufferIndex = methodBuffers.size()-1;
							StringBuffer currentBuffer = methodBuffers.get(bufferIndex);
							currentBuffer.append(strarray[0] + " __" + currentClassName + "_");
							int p;
							for(p = 1; p < strarray.length; p++) {
								int ind = strarray[p].indexOf("(");
								if(ind != -1) {
									currentBuffer.append(strarray[p].substring(0, ind+1) + "struct " + currentClassName + "* self, " + strarray[p].substring(ind+1) + " ");
								} else {
									currentBuffer.append(strarray[p] + " ");
								}
							}
							for(; p < strarray.length; p++) {
								currentBuffer.append(strarray[p] + " ");
							}
							currentBuffer.append("\n");
							hasAlreadyPrinted = true;
						}
					}
					if(carr[i] == '}') {
						curlyBraceCounter--;
						if(curlyBraceCounter == 1) {
							if(!isInMethodDeclaration) {
								System.out.println("Unknown error on line: " + lineNum);
								System.exit(12);
							}
							isInMethodDeclaration = false;
							int bufferIndex = methodBuffers.size()-1;
							StringBuffer currentBuffer = methodBuffers.get(bufferIndex);
							currentBuffer.append(line.substring(0, i) + "}\n");
							hasAlreadyPrinted = true;
						}
						if(curlyBraceCounter <= 0) {
							curlyBraceCounter = 0;
							if(isInMethodDeclaration) {
								System.out.println("Error on line " + lineNum + ": method declaration unfinished");
								System.exit(10);
							}
							isInClassDeclaration = false;
							output.println("};");
							for(int p = 0; p < methodBuffers.size(); p++) {
								output.println(methodBuffers.get(p).toString());
							}
							hasAlreadyPrinted = true;
						}
					}
				}
			}
			if(strarray.length == 2 && strarray[1].charAt(strarray[1].length()-1) == ';') { //potential class variable declaration
				//System.out.print("Potential! ");
				String strname = strarray[1].substring(0, strarray[1].length()-1);
				//System.out.print(strname + " ");
				for(int i = 0; i < classes.size(); i++) {
					//System.out.print("Loop " + i + " classname: " + classes.get(i).name);
					if(classes.get(i).name.equals(strarray[0])) {
						//ClsStr cls = new ClsStr();
						//VariableScopeStruct vss = new VariableScopeStruct();
						//vss.parent = cls;
						//cls.name = strarray[1];
						output.println("struct " + strarray[0] + "* " + strname + " = malloc(sizeof(struct " + strarray[0] + "));");
						VariableScopeStruct cs = currentScope.peek();
						VarStr vs = new VarStr(strname, strarray[0]);
						vs.isClass = true;
						cs.variables.add(vs);
						hasAlreadyPrinted = true;
					}
				}
			}
			if(!hasAlreadyPrinted) {
				String temp = replaceFunctionCallsInLine(line, currentScope, classes);
				if(temp != null) {
					line = temp;
				}
				if(isInMethodDeclaration) {
						int currentBufferIndex = methodBuffers.size()-1;
						if(currentBufferIndex < 0) {
							System.out.println("Unknown error on line: " + lineNum);
							System.exit(13);
						}
						StringBuffer currentBuffer = methodBuffers.get(currentBufferIndex);
						currentBuffer.append(line + "\n");
						hasAlreadyPrinted = true;
				}
			}
			if(!hasAlreadyPrinted) output.println(line);
			lineNum++;
		}
		s.close();
		output.close();
		return "";
	}
	private static String replaceFunctionCallsInLine(String line, Stack<VariableScopeStruct> currentScope, ArrayList<ClsStr> classes) {
		char[] carr = line.toCharArray();
		StringBuffer sb = new StringBuffer();
		int stringbstart = 0;
		for(int i = 0; i < carr.length; i++) {
			if(carr[i] == ' ' || carr[i] == '(') {
				sb = new StringBuffer();
				stringbstart = i+1;
			} else if(carr[i] == '.') {
				VariableScopeStruct cs = currentScope.peek();
				//System.out.println(cs.variables);
				int ind = -1;
				for(int o = 0; o < cs.variables.size(); o++) {
					if(cs.variables.get(o).name.equals(sb.toString())) {
						ind = o;
						break;
					}
				}
				if(ind != -1) {
					//int ind = cs.variables.indexOf(sb.toString());
					VarStr vs = cs.variables.get(ind);
					if(vs.isClass) {
						String className = vs.type;
						for(int o = 0; o < classes.size(); o++) {
							if(classes.get(o).name.equals(className)) {
								int p;
								int q = -1;
								for(p = i; p < carr.length; p++) {
									if(carr[p] == ')') {
										break;
									}
									if(q == -1 && carr[p] == '(') {
										q = p;
									}
								}
								if(q != -1) {
									//System.out.println("LINE: " + line);
									return line.substring(0, stringbstart) + " __" + classes.get(o).name + "_" + line.substring(vs.name.length()+1+stringbstart, q) + "(" + vs.name + ", " + line.substring(q+1);
									//break;
								}
							}
						}
					}
				}
				break;
			} else {
				sb.append(carr[i]);
			}
			
		}
		return null;
	}
	private static class VariableScopeStruct {
		ArrayList<VarStr> variables = new ArrayList<VarStr>();
		ItemStr parent;
		public String toString() {
			return "[variableScopeStruct: " + super.toString() + " parent: " + parent.toString() + " variables: " + variables.toString() + "]";
		}
	}
	private abstract static class ItemStr {
		String name;
		public String toString() {
			return "[itemStr: " + name + "]";
		}
	}
	private abstract static class ItemDeclarationStr extends ItemStr {
		String type;
		public String toString() {
			return "[itemDeclarationStr: " + super.toString() + " type: " + type + "]";
		}
	}
	private static class ClsStr extends ItemStr { //class struct
		ArrayList<MethodStr> methods;
		//ArrayList<VarStr> variables;
		VariableScopeStruct variables;
		public String toString() {
			return "[clsStr: " + super.toString() + " methods: " + methods.toString() + " variables: " + variables.toString() + "]";
		}
	}
	private static class MethodStr extends ItemDeclarationStr { //method struct
		VarStr[] arguments;
		public String toString() {
			return "[methodStr: " + super.toString() + " arguments: " + java.util.Arrays.toString(arguments) + "]";
		}
	}
	private static class VarStr extends ItemDeclarationStr { //variable struct
		boolean isClass = false;
		VarStr(String name, String type) {
			NoPointer temp;
			NoPointer temp2;
			temp = dePointerify(name);
			temp2 = dePointerify(type);
			if(temp.isPointer || temp2.isPointer) {
				this.name = temp.name + "*";
			} else {
				this.name = temp.name;
			}
			this.type = temp2.name;
		}
		public String toString() {
			return "[varStr: " + super.toString() + "]";
		}
	}
	private static class NoPointer extends ItemStr {
		boolean isPointer;
		public String toString() {
			return "[noPointer: " + super.toString() + "]";
		}
	}
	private static NoPointer dePointerify(String name) {
		int ptrind = name.indexOf("*");
		boolean isPtr = false;
		if(ptrind != -1) {
			name = name.substring(0, ptrind) + (ptrind < name.length()-1 ? name.substring(0, ptrind+1) : "");
			isPtr = true;
		}
		NoPointer a = new NoPointer();
		a.isPointer = isPtr;
		a.name = name;
		return a;
	}
	/*public static void compilezFile(String filename) {
		Scanner s = new Scanner(new File(filename));
		boolean isInClass = false;
		boolean isInBlockComment = false;
		String className;
		int lineNum = -1;
		StringBuilder buff = new StringBuilder("");
		while(s.hasNextLine()) {
			lineNum++;
			String line = s.nextLine();
			String str = line.trim();
			if(isInBlockComment) {
				char[] linearr = str.toCharArray();
				}}}}}}}}}}}}}}}}}}}}}}}}}}}}})))))))));
				int in = linearr.indexOf("*HEY YOU REMOVE THIS/"); //check if there's a block comment end
				if(in == -1) { //block comment doesn't end
					continue; //go to next line
				} else { //block comment ends this line
					isInBlockComment = false; //hey we're done
					line = line.substring(in); //remove up until the block comment so that processing below ignores it
					str = line.trim(); //trim off whitespace
				}
			}
			String[] array = str.split(" ");
			if(array.length < 1) {
				buff.append(str + "\n");
				continue;
			}
			if(isInClass) {
				char[] linearr = str.toCharArray();
				for(int i = 0; i < linearr.length; i++) {
					if(linearr[i] == '"') { //we've caught us a string!
						while(i < linearr.length) {
							if(linearr[i] == '"' && linearr[i-1] != '\') {
								break;
							}
							i++;
						}
					} else if(linearr[i] == '/' && i < linearr.length-1 && linearr[i+1] == '*') { //what a block comment
						isInBlockComment = true; //in case it's multi-line it'll be handled
						boolean blockCommentEndFoundInThisLine = false; //this and the following loop checks if the block comment ends this line
						while(i < linearr - 1) {
							if(linearr[i] == '*' && linearr[i+1] == '/') {
								isInBlockComment = false;
								blockCommentEndFoundInThisLine = true; //I love self-explanatory variable names
								break;
							}
							i++;
						}
						if(!blockCommentEndFoundInThisLine) {
							break;
						}
					} else if(linearr[i] == '/' && i < linearr.length-1 && linearr[i+1] == '/') { //single line comment
						break;
					} else if(linearr[i] == '\'' && i < linearr.length - 2) {
						if(linearr[i+2] != '\'') {
							if(!(i < linearr.length - 3 && linearr[i+1] == '\\' && linearr[i+3] == '\'')) { //we've got a character that is not escaped and is the wrong length
								System.out.println("Error on line " + lineNum + ": Invalid character declaration");
							}
						}
					}
			} else if(array[0] == "class") {
				if(array.length < 3) {
					System.out.println("Error on line " + lineNum + ": Class declaration cut short.");
					System.exit(2);
				}
				className = array[1];
				if(array[2] != "{") {
					System.out.println("Error on line " + lineNum + ": No opening curly brace found!");
					System.exit(1);
				}
				buff.append("struct " + className + " {\n");
				isInClass = true;
			} else {
				buff.append(str + "\n");
			}
		}
		s.close();
		System.out.println(buff);
	}*/
}

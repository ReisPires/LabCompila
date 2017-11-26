/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.util.*;
import comp.CompilationError;

public class Program {

	public Program(ArrayList<KraClass> classList, ArrayList<MetaobjectCall> metaobjectCallList, 
			       ArrayList<CompilationError> compilationErrorList) {
		this.classList = classList;
		this.metaobjectCallList = metaobjectCallList;
		this.compilationErrorList = compilationErrorList;
	}


	public void genKra(PW pw) {
            for (MetaobjectCall moc : metaobjectCallList) {
                moc.genKra(pw);
                pw.println();                
            }
            for (KraClass kraClass : classList) {
                kraClass.genKra(pw);
            }
            pw.println();
	}

	public void genC(PW pw) {
            pw.println("#include <iostream>");
            pw.println("#include <string>");
            pw.println("using namespace std;");
                
            for (KraClass kraClass : classList) {
                kraClass.genC(pw);
            }
            
            pw.println("int main() {");
            pw.add();
            pw.printlnIdent("Program *p = new Program();");
            pw.printlnIdent("p->run();");
            pw.printlnIdent("return 0;");
            pw.sub();
            pw.print("}");
	}
	
	public ArrayList<KraClass> getClassList() {
		return classList;
	}


	public ArrayList<MetaobjectCall> getMetaobjectCallList() {
		return metaobjectCallList;
	}
	

	public boolean hasCompilationErrors() {
		return compilationErrorList != null && compilationErrorList.size() > 0 ;
	}

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}

	
	private ArrayList<KraClass> classList;
	private ArrayList<MetaobjectCall> metaobjectCallList;
	
	ArrayList<CompilationError> compilationErrorList;

	
}
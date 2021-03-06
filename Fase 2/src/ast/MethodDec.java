/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.util.Iterator;

public class MethodDec {

    public MethodDec(Variable variable) {
        this.variable = variable;        
    }

    public Variable getVariable() {
        return variable;
    }

    public StatementList getStatementList() {
        return statementList;
    }

    public void setStatementList(StatementList statementList) {
        this.statementList = statementList;
    }
    
    public void genKra(PW pw) {
        variable.genKra(pw);
        pw.print("(");
        if (variable.getParam() != null) {            
            Iterator itr = variable.getParam().elements();
            int i = 0;
            while (itr.hasNext()) {                                
                Parameter parameter = (Parameter) itr.next();
                parameter.genKra(pw);              
                if (i++ < variable.getParam().getSize() - 1)
                    pw.print(", ");
            }
        }
        pw.println(") {");  
        pw.add();
        statementList.genKra(pw);
        pw.sub();
        pw.printlnIdent("}");
    }
    
    public void genC(PW pw) {
        variable.genC(pw);
        pw.print("(");
        if (variable.getParam() != null) {            
            Iterator itr = variable.getParam().elements();
            int i = 0;
            while (itr.hasNext()) {                                
                Parameter parameter = (Parameter) itr.next();
                parameter.genC(pw);              
                if (i++ < variable.getParam().getSize() - 1)
                    pw.print(", ");
            }
        }
        pw.println(") {");  
        pw.add();
        statementList.genC(pw);
        pw.sub();
        pw.printlnIdent("}");
    }

    
    private Variable variable;
    private StatementList statementList;

} 

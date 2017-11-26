/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class CompositeStatement extends Statement {

    public CompositeStatement(StatementList stmtList) {
        this.stmtList = stmtList;
    }
    
    public StatementList getStatementList() {
        return this.stmtList;
    }
       
    @Override
    public void genC(PW pw) {  
        pw.println(" {");
        pw.add();
        stmtList.genC(pw);
        pw.sub();
        pw.printIdent("}");
    }
       
    @Override
    public void genKra(PW pw) {
        pw.println(" {");
        pw.add();
        stmtList.genKra(pw);
        pw.sub();
        pw.printIdent("}");
    }
    
    private StatementList stmtList;
}

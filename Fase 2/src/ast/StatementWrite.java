/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementWrite extends Statement {

    public StatementWrite(ExprList exprList, boolean hasLineBreak) {
        this.exprList = exprList;
        this.hasLineBreak = hasLineBreak;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("cout << ");        
        exprList.genC(pw, true);
        if (hasLineBreak)
            pw.print("<< endl");
        pw.println(";");
    }                    
    
    @Override
    public void genKra(PW pw) {
        if (hasLineBreak)
            pw.printIdent("writeln( ");
        else
            pw.printIdent("write( ");
        exprList.genKra(pw);
        pw.println(" );");
    }
    
    private ExprList exprList;
    private boolean hasLineBreak;
   
}

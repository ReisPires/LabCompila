/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementReturn extends Statement {
    public StatementReturn(Expr expr) {
        this.expr = expr;
    }
    
    public Expr getExpr() {
        return this.expr;
    }
        
    @Override
    public void genC(PW pw) {        
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("return ");
        if (expr != null)
            expr.genKra(pw);
        pw.println(";");
    }
    
    private Expr expr;
}

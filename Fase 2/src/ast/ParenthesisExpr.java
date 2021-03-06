/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw ) {
        pw.print("(");
        expr.genC(pw);
        pw.print(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
        
    @Override
    public void genKra(PW pw) {
        pw.print("(");
        expr.genKra(pw);
        pw.print(")");
    }
    
    private Expr expr;
}
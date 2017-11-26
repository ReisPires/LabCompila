/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent( v.getName() );
    }
    
    public Type getType() {
        return v.getType();
    }
    
    @Override
    public void genKra(PW pw) {
        pw.print(v.getName());
    }
    
    private Variable v;
}
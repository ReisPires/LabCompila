/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import lexer.*;

public class SignalExpr extends Expr {

    public SignalExpr( Symbol oper, Expr expr ) {
       this.oper = oper;
       this.expr = expr;
    }

    @Override
	public void genC( PW pw, boolean putParenthesis ) {
       if ( putParenthesis )
          pw.printIdent("(");
       pw.printIdent( oper == Symbol.PLUS ? "+" : "-" );
       expr.genC(pw, true);
       if ( putParenthesis )
          pw.printIdent(")");
    }

    @Override
	public Type getType() {
       return expr.getType();
    }
        
    @Override
    public void genKra(PW pw) {
        pw.print(oper.toString());
        expr.genKra(pw);
    }

    private Expr expr;
    private Symbol oper;

    
}

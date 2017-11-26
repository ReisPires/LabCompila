/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import lexer.*;

public class UnaryExpr extends Expr {

	public UnaryExpr(Expr expr, Symbol op) {
		this.expr = expr;
		this.op = op;
	}

	@Override
	public void genC(PW pw, boolean putParenthesis) {
		switch (op) {
		case PLUS:
			pw.printIdent("+");
			break;
		case MINUS:
			pw.printIdent("-");
			break;
		case NOT:
			pw.printIdent("!");
			break;
		default:
			pw.printIdent(" internal error at UnaryExpr::genC");

		}
		expr.genC(pw, false);
	}

	@Override
	public Type getType() {
		return expr.getType();
	}
        
        @Override
        public void genKra(PW pw) {
            pw.print(op.toString());
            expr.genKra(pw);
        }

	private Expr	expr;
	private Symbol	op;
    
}

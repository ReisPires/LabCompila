/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import lexer.*;
import java.util.HashMap;

public class CompositeExpr extends Expr {

    public CompositeExpr( Expr pleft, Symbol poper, Expr pright ) {
        left = pleft;
        oper = poper;
        right = pright;
    }
    
    @Override
	public void genC( PW pw, boolean putParenthesis ) {
        if ( putParenthesis )
          pw.printIdent("(");
        left.genC(pw, true);
        String strSymbol = arrayOper.get(oper);
        if ( strSymbol == null ) {
        	pw.printlnIdent("internal error in CompositeExpr::genC");
        }
        else
            pw.printIdent(" " + strSymbol + " ");
        right.genC(pw, true);
        if ( putParenthesis )
          pw.printIdent(")");
    }

    @Override
	public Type getType() {
          // left and right must be the same type
       if ( oper == Symbol.EQ || oper == Symbol.NEQ || oper == Symbol.LE || oper == Symbol.LT ||
            oper == Symbol.GE || oper == Symbol.GT )
            return Type.booleanType;
       else if ( oper == Symbol.AND || oper == Symbol.OR )
            return Type.booleanType;
       else
            return Type.intType;
    }
        
    @Override
    public void genKra(PW pw) {
        left.genKra(pw);
        if (right != null) {
            pw.print(" " + oper.toString() + " ");
            right.genKra(pw);
        }
    }

    private Expr left, right;
    private Symbol oper;
    private static HashMap<Symbol, String> arrayOper;
    static {
        arrayOper = new HashMap<Symbol, String>();
        arrayOper.put(Symbol.PLUS, "+");
        arrayOper.put(Symbol.MINUS, "-");
        arrayOper.put(Symbol.MULT, "*");
        arrayOper.put(Symbol.DIV, "/");
        arrayOper.put(Symbol.LT, "<");
        arrayOper.put(Symbol.LE, "<=");
        arrayOper.put(Symbol.GT, ">");
        arrayOper.put(Symbol.GE, ">=");
        arrayOper.put(Symbol.NEQ, "!=");
        arrayOper.put(Symbol.EQ, "==");
        arrayOper.put(Symbol.ASSIGN, "=");
        arrayOper.put(Symbol.AND, "&&");
        arrayOper.put(Symbol.OR, "||");
    }    
}

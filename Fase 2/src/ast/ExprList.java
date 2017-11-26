/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.util.*;

public class ExprList {

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add(expr);
    }
    
    public ArrayList<Expr> getExpr(){
        
        return this.exprList;
    }
    
    public void genC( PW pw, boolean isOutput ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
                if (e instanceof LiteralString && !isOutput)
                    pw.print("new string(");                
                e.genC(pw);
                if (e instanceof LiteralString && !isOutput)
                    pw.print(")");                
            if ( --size > 0 )
                if (isOutput)
                    pw.print(" << ");
                else
                    pw.print(", ");
        }
    }
    
    public void genKra(PW pw) {
        for(int i = 0; i < exprList.size(); ++i) {
            exprList.get(i).genKra(pw);
            if (i < exprList.size() - 1)
                pw.print(", ");
        }
    }

    private ArrayList<Expr> exprList;

}

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
    
    public void genC( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genC(pw, false);
            if ( --size > 0 )
                pw.printIdent(", ");
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

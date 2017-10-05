package ast;

public class AssignExpr extends AssignExprLocalDec {

    public AssignExpr(Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public void genC(PW pw) {        
    }
    
    private Expr expr1, expr2;
}

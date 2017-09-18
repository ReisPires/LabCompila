package ast;

public class ReturnStatement extends Statement {
    public ReturnStatement(Expr expr) {
        this.expr = expr;
    }
    
    public Expr getExpr() {
        return this.expr;
    }
        
    @Override
    public void genC(PW pw) {        
    }
    
    private Expr expr;
}

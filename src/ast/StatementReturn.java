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
    
    private Expr expr;
}

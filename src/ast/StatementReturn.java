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
    
    @Override
    public void genKra(PW pw) {
        pw.print("return ");
        if (expr != null)
            expr.genKra(pw);
        pw.println(";");
    }
    
    private Expr expr;
}

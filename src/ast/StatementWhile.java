package ast;

public class StatementWhile extends Statement {

    public StatementWhile(Expr expr, Statement stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }
    
    @Override
    public void genC(PW pw) {        
    }
    
    Expr expr;
    Statement stmt;
}

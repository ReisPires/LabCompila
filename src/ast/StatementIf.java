package ast;

public class StatementIf extends Statement {

    public StatementIf(Expr expr, Statement thenStmt, Statement elseStmt) {
        this.expr = expr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
    
    @Override
    public void genC(PW pw) {        
    }

    public Statement getThenStmt() {
        return thenStmt;
    }

    public Statement getElseStmt() {
        return elseStmt;
    }        
    
    private Expr expr;
    private Statement thenStmt;
    private Statement elseStmt;
}

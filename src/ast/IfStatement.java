package ast;

public class IfStatement extends Statement {

    public IfStatement(Expr expr, Statement thenStmt, Statement elseStmt) {
        this.expr = expr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
    
    @Override
    public void genC(PW pw) {        
    }
    
    Expr expr;
    Statement thenStmt;
    Statement elseStmt;
}

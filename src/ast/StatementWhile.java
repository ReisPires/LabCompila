package ast;

public class StatementWhile extends Statement {

    public StatementWhile(Expr expr, Statement stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }
    
    @Override
    public void genC(PW pw) {        
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("while (");
        expr.genKra(pw);
        pw.print(")");
        if (!(stmt instanceof CompositeStatement)) {
            pw.println();
            pw.add();
        }
        if (stmt != null)
            stmt.genKra(pw);        
        if (!(stmt instanceof CompositeStatement)) {            
            pw.sub();
        } else {
            pw.println();
        }
    }
    
    Expr expr;
    Statement stmt;
}

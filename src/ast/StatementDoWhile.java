package ast;

public class StatementDoWhile extends Statement {

    public StatementDoWhile(CompositeStatement compositeStatement, Expr expr) {
        this.compositeStatement = compositeStatement;
        this.expr = expr;
    }
    
    @Override
    public void genC(PW pw) {        
    }

    @Override
    public void genKra(PW pw) {
        pw.printIdent("do ");        
        compositeStatement.genKra(pw);
        pw.print(" while (");
        expr.genKra(pw);
        pw.println(");");
    }           
    
    private CompositeStatement compositeStatement;
    private Expr expr;
  
}

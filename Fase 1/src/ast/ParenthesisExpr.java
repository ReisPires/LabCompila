package ast;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent("(");
        expr.genC(pw, false);
        pw.printIdent(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
        
    @Override
    public void genKra(PW pw) {
        pw.print("(");
        expr.genKra(pw);
        pw.print(")");
    }
    
    private Expr expr;
}
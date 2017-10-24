
package ast;

public class PrimaryExpr extends Expr{
       
    public PrimaryExpr(String[] idList, ExprList exprList, Type type) {
      this.idList = idList;
      this.exprList = exprList;
      this.type = type;      
    }
   
    @Override
    public void genC(PW pw, boolean putParenthesis) {
    }

    @Override
    public Type getType() {
        return this.type;
    }
    
    private String[] idList;
    private ExprList exprList;
    private Type type;    
}

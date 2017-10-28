
package ast;

public class PrimaryExpr extends Expr{
       
    public PrimaryExpr(String[] idList, ExprList exprList, Type type, boolean isMethod) {
      this.idList = idList;
      this.exprList = exprList;
      this.type = type;      
      this.isMethod = isMethod;
    }
   
    @Override
    public void genC(PW pw, boolean putParenthesis) {
    }

    @Override
    public Type getType() {
        return this.type;
    }

    public String[] getIdList() {
        return idList;
    }

    public ExprList getExprList() {
        return exprList;
    }        

    public boolean isMethod() {
        return isMethod;
    }       
    
    @Override
    public void genKra(PW pw) {
        for (int i = 0; i < 3 && idList[i] != null; i++) {
            pw.print(idList[i]);
            if (i != 2 && idList[i+1] != null)
                pw.print(".");
        }
        if (isMethod) {
            pw.print("(");
            if (exprList != null)
                exprList.genKra(pw);
            pw.print(")");
        }
    }
    
    private String[] idList;
    private ExprList exprList;
    private Type type;    
    private boolean isMethod;
}

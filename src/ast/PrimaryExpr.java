
package ast;

public class PrimaryExpr extends Expr{

    public PrimaryExpr(boolean namethis) {
        this.isThis = namethis;
    }
    
    public PrimaryExpr(String name, Type type) {
      this.name = name;
      this.type = type;
    }

    

    @Override
    public void genC(PW pw, boolean putParenthesis) {
    }

    @Override
    public Type getType() {
        return this.type;
    }
    
    private String name;
    private Type type;
    private boolean isThis = false;
    private boolean isSuper = false;
}

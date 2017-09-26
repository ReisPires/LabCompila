
package ast;

public class PrimaryExpr extends Expr{

    public PrimaryExpr(String name, Type type) {
      this.name = name;
      this.type = type;
    }

    

    @Override
    public void genC(PW pw, boolean putParenthesis) {
    }

    @Override
    public Type getType() {
        return null;
    }
    
    private String name;
    private Type type;
}


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
        return this.type;
    }
    
    private String name;
    private Type type;
}

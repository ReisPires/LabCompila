package ast;

public class NewExpr extends Expr{    
   
    public NewExpr(KraClass newClass ){
        this.newClass = newClass;
    }
    @Override
    public void genC(PW pw, boolean putParenthesis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Type getType() {
        return this.newClass; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void genKra(PW pw) {
        pw.print("new " + newClass.getCname() + "()");
    }
   
    private KraClass newClass;
}

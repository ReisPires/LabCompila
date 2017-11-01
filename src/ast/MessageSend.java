package ast;


abstract class MessageSend  extends Expr  {
    
    public abstract String getPrimaryExprName();        

    public abstract boolean isMethod();
}


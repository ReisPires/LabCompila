package ast;

public class LiteralString extends Expr {
    
    public LiteralString( String literalString ) { 
        this.literalString = literalString;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent(literalString);
    }
    
    public Type getType() {
        return Type.stringType;
    }
    
    @Override
    public void genKra(PW pw) {
        pw.print("\"" + literalString + "\"");
    }
    
    private String literalString;
    
}

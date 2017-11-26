/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class LiteralBoolean extends Expr {

    public LiteralBoolean( boolean value ) {
        this.value = value;
    }

    @Override
	public void genC( PW pw, boolean putParenthesis ) {
       pw.printIdent( value ? "1" : "0" );
    }

    @Override
	public Type getType() {
        return Type.booleanType;
    }
        
    @Override
    public void genKra(PW pw) {
        if (value)
            pw.print("true");
        else
            pw.print("false");
    }

    public static LiteralBoolean True  = new LiteralBoolean(true);
    public static LiteralBoolean False = new LiteralBoolean(false);

    private boolean value;

    
}

/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class LiteralString extends Expr {
    
    public LiteralString( String literalString ) { 
        this.literalString = literalString;
    }
    
    public void genC( PW pw ) {
         pw.print("\"" + literalString + "\"");
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

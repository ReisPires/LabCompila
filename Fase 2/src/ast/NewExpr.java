/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class NewExpr extends Expr{    
   
    public NewExpr(KraClass newClass ){
        this.newClass = newClass;
    }
    @Override
    public void genC(PW pw) {
        pw.print("new " + newClass.getCname() + "()");
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

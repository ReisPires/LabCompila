/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementNull extends Statement {

    @Override
    public void genC(PW pw) {
        pw.printlnIdent(";");
    }

    @Override
    public void genKra(PW pw) {
        pw.printlnIdent(";");
    }
    
}

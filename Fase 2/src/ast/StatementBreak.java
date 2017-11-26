/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementBreak extends Statement {
        
    @Override
    public void genC(PW pw) {  
        pw.printlnIdent("break;");
    }

    @Override
    public void genKra(PW pw) {
        pw.printlnIdent("break;");
    }
    
}

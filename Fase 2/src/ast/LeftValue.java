/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class LeftValue {

    public LeftValue(Variable v, boolean hasThis) {
        this.v = v;
        this.hasThis = hasThis;
    }        
    
    public void genKra(PW pw) {
        if (hasThis)
            pw.print("this.");
        pw.print(v.getName());
    }
    
    private Variable v;
    private boolean hasThis;
}

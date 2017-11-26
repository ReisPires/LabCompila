/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type, "private");                
    }
    
    @Override
    public void genKra(PW pw) {
        super.genKra(pw);
        pw.println(";");
    }
}
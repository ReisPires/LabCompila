/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

abstract public class Statement {

	abstract public void genC(PW pw);

        
        abstract public void genKra(PW pw);
}

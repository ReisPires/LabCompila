/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

abstract public class Expr {
    abstract public void genC( PW pw );
      // new method: the type of the expression
    abstract public Type getType();
    
    abstract public void genKra(PW pw);

}
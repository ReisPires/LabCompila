/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;


abstract class MessageSend  extends Expr  {
    
    public abstract String getPrimaryExprName();        

    public abstract boolean isMethod();
}


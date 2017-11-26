/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class TypeIdent extends Type {

    public TypeIdent() {
        super("ident");
    }
    
   public String getCname() {
      return super.getName();
   }
   
}

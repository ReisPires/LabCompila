package ast;

public class TypeIdent extends Type {

    public TypeIdent() {
        super("ident");
    }
    
   public String getCname() {
      return super.getName();
   }
   
}

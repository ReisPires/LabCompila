package ast;


public class Parameter extends Variable {

    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    @Override
    public void genKra(PW pw) {
        if (super.getType() instanceof KraClass) {
            pw.print(super.getType().getCname() + " " + super.getName());
        } else {
            pw.print(super.getType().getName() + " " + super.getName());  
        }
    }
}
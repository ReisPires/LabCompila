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

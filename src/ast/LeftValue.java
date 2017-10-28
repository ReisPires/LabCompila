package ast;

public class LeftValue {

    public LeftValue(Variable v, boolean hasThis) {
        this.v = v;
        this.hasThis = hasThis;
    }        
    
    private Variable v;
    private boolean hasThis;
}

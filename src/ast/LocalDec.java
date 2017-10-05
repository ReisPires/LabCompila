package ast;

public class LocalDec extends AssignExprLocalDec {

    public LocalDec (Type type, VariableList varList) {
        this.type = type;
        this.varList = varList;
    }
           
    @Override
    public void genC(PW pw) {        
    }
    
    private Type type;
    private VariableList varList;
}

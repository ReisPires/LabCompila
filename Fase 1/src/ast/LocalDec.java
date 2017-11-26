package ast;

import java.util.Iterator;

public class LocalDec extends AssignExprLocalDec {

    public LocalDec (Type type, VariableList varList) {
        this.type = type;
        this.varList = varList;
    }
           
    @Override
    public void genC(PW pw) {        
    }
    
    @Override
    public void genKra(PW pw) {
        if (type instanceof KraClass)
            pw.printIdent(type.getCname() + " ");
        else
            pw.printIdent(type.getName() + " ");
        
        if (varList != null) {
            Iterator itr = varList.elements();
            int i = 0;
            while (itr.hasNext()) {
                Variable v = (Variable)itr.next();
                pw.print(v.getName());
                if (i++ < varList.getSize() - 1)
                    pw.print(", ");
            }
        }
        pw.println(";");
    }
    
    private Type type;
    private VariableList varList;    
}

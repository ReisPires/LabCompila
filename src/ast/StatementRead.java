package ast;

import java.util.ArrayList;

public class StatementRead extends Statement {

    public StatementRead(ArrayList<LeftValue> leftValues) {
        this.leftValues = leftValues;
    }            

    @Override
    public void genC(PW pw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public void genKra(PW pw) {
        pw.print("read(");
        for (int i = 0; i < leftValues.size(); ++i) {
            leftValues.get(i).genKra(pw);
            if (i < leftValues.size() - 1)
                pw.print(",");
        }
        pw.println(");");
    }
    
    private ArrayList<LeftValue> leftValues;
}

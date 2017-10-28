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
    
    private ArrayList<LeftValue> leftValues;
}

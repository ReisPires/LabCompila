package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class VariableList {
    
    public VariableList() {
       varList = new ArrayList<Variable>();
    }

    public void addElement(Variable v) {
       varList.add(v);
    }

    public Iterator<Variable> elements() {
        return varList.iterator();
    }

    public int getSize() {
        return varList.size();
    }

    private ArrayList<Variable> varList;
}

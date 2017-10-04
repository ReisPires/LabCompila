package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class StatementList {
    
    public StatementList() {
       this.statementList = new ArrayList<Statement>();
    }

    public void addElement(Statement s) {
       this.statementList.add(s);
    }

    public Iterator<Statement> elements() {
        return this.statementList.iterator();
    }
    
    public ArrayList<Statement> getList() {
        return this.statementList;
    }

    public int getSize() {
        return this.statementList.size();
    }

    private ArrayList<Statement> statementList;
    
}

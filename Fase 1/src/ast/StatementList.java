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
    
    public void genKra(PW pw) {
        boolean isLocalDec = false;
        for (Statement stmt : statementList) {
            if (stmt instanceof LocalDec) {
                isLocalDec = true;
            }
            else if (isLocalDec) {
                pw.println();
                isLocalDec = false;
            }
            
            stmt.genKra(pw);
            if (stmt instanceof CompositeStatement)
                pw.println();
        }
    }

    private ArrayList<Statement> statementList;
    
}

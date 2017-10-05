package ast;

public class CompositeStatement extends Statement {

    public CompositeStatement(StatementList stmtList) {
        this.stmtList = stmtList;
    }
    
    public StatementList getStatementList() {
        return this.stmtList;
    }
       
    @Override
    public void genC(PW pw) {        
    }
   
    private StatementList stmtList;
}

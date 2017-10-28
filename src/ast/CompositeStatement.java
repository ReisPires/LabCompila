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
       
    @Override
    public void genKra(PW pw) {
        pw.println(" {");
        stmtList.genKra(pw);
        pw.println("}");
    }
    
    private StatementList stmtList;
}

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
        pw.add();
        stmtList.genKra(pw);
        pw.sub();
        pw.printIdent("}");
    }
    
    private StatementList stmtList;
}

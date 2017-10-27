package ast;

public class StatementWrite extends Statement {

    public StatementWrite(ExprList exprList, boolean hasLineBreak) {
        this.exprList = exprList;
        this.hasLineBreak = hasLineBreak;
    }

    @Override
    public void genC(PW pw) {    
    }                    
    
    private ExprList exprList;
    private boolean hasLineBreak;
}

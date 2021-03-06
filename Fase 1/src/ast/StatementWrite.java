package ast;

public class StatementWrite extends Statement {

    public StatementWrite(ExprList exprList, boolean hasLineBreak) {
        this.exprList = exprList;
        this.hasLineBreak = hasLineBreak;
    }

    @Override
    public void genC(PW pw) {    
    }                    
    
    @Override
    public void genKra(PW pw) {
        if (hasLineBreak)
            pw.printIdent("writeln( ");
        else
            pw.printIdent("write( ");
        exprList.genKra(pw);
        pw.println(" );");
    }
    
    private ExprList exprList;
    private boolean hasLineBreak;
   
}

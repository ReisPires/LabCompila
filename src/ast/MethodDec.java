package ast;

public class MethodDec {

    public MethodDec(Variable variable) {
        this.variable = variable;        
    }

    public Variable getVariable() {
        return variable;
    }

    public StatementList getStatementList() {
        return statementList;
    }

    public void setStatementList(StatementList statementList) {
        this.statementList = statementList;
    }        
    
    private Variable variable;
    private StatementList statementList;
}

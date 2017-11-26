package ast;

public class StatementBreak extends Statement {
        
    @Override
    public void genC(PW pw) {        
    }

    @Override
    public void genKra(PW pw) {
        pw.printlnIdent("break;");
    }
    
}

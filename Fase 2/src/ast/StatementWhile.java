/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementWhile extends Statement {

    public StatementWhile(Expr expr, Statement stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }
    
    @Override
    public void genC(PW pw) { 
        pw.printIdent("while (");
        expr.genC(pw);
        pw.print(")");
        if (!(stmt instanceof CompositeStatement)) {
            pw.println();
            pw.add();
        }
        if (stmt != null)
            stmt.genC(pw);        
        if (!(stmt instanceof CompositeStatement)) {            
            pw.sub();
        } else {
            pw.println();
        }
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("while (");
        expr.genKra(pw);
        pw.print(")");
        if (!(stmt instanceof CompositeStatement)) {
            pw.println();
            pw.add();
        }
        if (stmt != null)
            stmt.genKra(pw);        
        if (!(stmt instanceof CompositeStatement)) {            
            pw.sub();
        } else {
            pw.println();
        }
    }
    
    Expr expr;
    Statement stmt;
}

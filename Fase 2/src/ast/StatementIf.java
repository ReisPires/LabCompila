/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class StatementIf extends Statement {

    public StatementIf(Expr expr, Statement thenStmt, Statement elseStmt) {
        this.expr = expr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
    
    @Override
    public void genC(PW pw) { 
        pw.printIdent("if (");
        expr.genC(pw);
        pw.print(")");
        if (!(thenStmt instanceof CompositeStatement)) {
            pw.println();
            pw.add();
        }        
        thenStmt.genC(pw);          
        if (!(thenStmt instanceof CompositeStatement)) {            
            pw.sub();
        } else {
            pw.println();
        }
        if (elseStmt != null) {
            pw.printIdent("else");            
            if (!(elseStmt instanceof CompositeStatement)) {
                pw.println();
                pw.add();
            }
            elseStmt.genC(pw);             
            if (!(elseStmt instanceof CompositeStatement)) {             
                pw.sub();
            } else {
                pw.println();
            }
        }
    }

    public Statement getThenStmt() {
        return thenStmt;
    }

    public Statement getElseStmt() {
        return elseStmt;
    }        
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("if (");
        expr.genKra(pw);
        pw.print(")");
        if (!(thenStmt instanceof CompositeStatement)) {
            pw.println();
            pw.add();
        }        
        thenStmt.genKra(pw);          
        if (!(thenStmt instanceof CompositeStatement)) {            
            pw.sub();
        } else {
            pw.println();
        }
        if (elseStmt != null) {
            pw.printIdent("else");            
            if (!(elseStmt instanceof CompositeStatement)) {
                pw.println();
                pw.add();
            }
            elseStmt.genKra(pw);             
            if (!(elseStmt instanceof CompositeStatement)) {             
                pw.sub();
            } else {
                pw.println();
            }
        }
    }
    
    private Expr expr;
    private Statement thenStmt;
    private Statement elseStmt;
}

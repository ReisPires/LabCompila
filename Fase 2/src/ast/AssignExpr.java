/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class AssignExpr extends AssignExprLocalDec {

    public AssignExpr(Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public void genC(PW pw) {        
        if (expr1 instanceof MessageSendToVariable || expr1 instanceof MessageSendToSuper || expr1 instanceof MessageSendToSelf)
            pw.printIdent("");
        if (expr1.getType() instanceof TypeString && !(expr1 instanceof LiteralString) && expr2 != null && !(expr2.getType() instanceof TypeUndefined)) {
            pw.print("*(");   
        }
        expr1.genC(pw);
        if (expr1.getType() instanceof TypeString && !(expr1 instanceof LiteralString) && expr2 != null && !(expr2.getType() instanceof TypeUndefined)) {
            pw.print(")");   
        }
        if (expr2 != null) {
            pw.print(" = ");
            if (expr2.getType() instanceof TypeString && !(expr2 instanceof LiteralString)) {
                pw.print("*(");   
            }
            expr2.genC(pw);
            if (expr2.getType() instanceof TypeString && !(expr2 instanceof LiteralString)) {
                pw.print(")");   
            }
        }
        pw.println(";");
    }    
        
    @Override
    public void genKra(PW pw) {
        if (expr1 instanceof MessageSendToVariable || expr1 instanceof MessageSendToSuper || expr1 instanceof MessageSendToSelf)
            pw.printIdent("");
        expr1.genKra(pw);
        if (expr2 != null) {
            pw.print(" = ");
            expr2.genKra(pw);
        }
        pw.println(";");
    }
    
    private Expr expr1, expr2;
}

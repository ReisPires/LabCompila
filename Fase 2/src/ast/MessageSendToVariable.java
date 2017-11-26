/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;


public class MessageSendToVariable extends MessageSend { 

    /* Id */
    public MessageSendToVariable(String firstId, Type type) {
        this.firstId = firstId;
        this.secondId = null;
        this.thirdId = null;
        this.isMethod = false;
        this.exprList = null;
        this.type = type;
    }
    
    /* Id.Id */
    public MessageSendToVariable(String firstId, String secondId, Type type) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.thirdId = null;
        this.isMethod = false;
        this.exprList = null;
        this.type = type;
    }

    /* Id.Id(exprList) */
    public MessageSendToVariable(String firstId, String secondId, ExprList exprList, Type type) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.thirdId = null;
        this.isMethod = true;
        this.exprList = exprList;
        this.type = type;
    }
    
    /* Id.Id.Id(exprList) */
    public MessageSendToVariable(String firstId, String secondId, String thirdId, ExprList exprList, Type type) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.thirdId = thirdId;
        this.isMethod = true;
        this.exprList = exprList;
        this.type = type;
    }
    
    @Override
    public boolean isMethod() {
        return this.isMethod;
    }
    
    @Override
    public String getPrimaryExprName() {
        String primaryExprName = firstId;
        if (secondId != null) primaryExprName += "." + secondId;
        if (thirdId != null) primaryExprName += "." + thirdId;
        if (isMethod) { 
            primaryExprName += "(";
            if (exprList != null) {
                for (int i = 0; i < exprList.getExpr().size(); ++i) {                    
                    if (exprList.getExpr().get(i).getType() instanceof KraClass)
                        primaryExprName += exprList.getExpr().get(i).getType().getCname();
                    else
                        primaryExprName += exprList.getExpr().get(i).getType().getName();
                    if (i < exprList.getExpr().size() - 1) primaryExprName += ", ";
                }
            }
            primaryExprName += ")";
        }        
        return primaryExprName;
    }
    
    @Override
    public Type getType() { 
        return this.type;
    }
    
    @Override
    public void genC( PW pw ) {
        pw.print(firstId);
        if (secondId != null) pw.print("->" + secondId);
        if (thirdId != null) pw.print("->" + thirdId);
        if (isMethod) { 
            pw.print("(");
            if (exprList != null)
                exprList.genC(pw, false);            
            pw.print(")");
        }
    }

    @Override
    public void genKra(PW pw) {
        pw.print(firstId);
        if (secondId != null) pw.print("." + secondId);
        if (thirdId != null) pw.print("." + thirdId);
        if (isMethod) { 
            pw.print("(");
            if (exprList != null)
                exprList.genKra(pw);            
            pw.print(")");
        }        
    }

    private String firstId, secondId, thirdId;
    private boolean isMethod;
    private ExprList exprList;
    private Type type;
}    
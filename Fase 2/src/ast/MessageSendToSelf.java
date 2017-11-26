/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;


public class MessageSendToSelf extends MessageSend {

    /* 'this' */
    public MessageSendToSelf(Type type) {
        this.firstId = null;
        this.secondId = null;
        this.isMethod = false;
        this.exprList = null;
        this.type = type;
    } 
    
    /* this.id */
    public MessageSendToSelf(String firstId, Type type) {
        this.firstId = firstId;
        this.secondId = null;
        this.isMethod = false;
        this.exprList = null;
        this.type = type;
    } 
    
    /* this.id(exprList) */
    public MessageSendToSelf(String firstId, ExprList exprList, Type type) {
        this.firstId = firstId;
        this.secondId = null;
        this.isMethod = true;
        this.exprList = exprList;
        this.type = type;
    }        
    
    /* this.id.id(exprList) */
    public MessageSendToSelf(String firstId, String secondId, ExprList exprList, Type type) {
        this.firstId = firstId;
        this.secondId = secondId;
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
        String primaryExprName = "this";
        if (firstId != null) primaryExprName += "." + firstId;
        if (secondId != null) primaryExprName += "." + secondId;
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
        pw.print("this");
        if (firstId != null) pw.print("->" + firstId);
        if (secondId != null) pw.print("->" + secondId);
        if (isMethod) { 
            pw.print("(");
            if (exprList != null)
                exprList.genC(pw, false);            
            pw.print(")");
        }                
    }

    @Override
    public void genKra(PW pw) {
        pw.print("this");
        if (firstId != null) pw.print("." + firstId);
        if (secondId != null) pw.print("." + secondId);
        if (isMethod) { 
            pw.print("(");
            if (exprList != null)
                exprList.genKra(pw);            
            pw.print(")");
        }                
    }
    
    private String firstId, secondId;
    private boolean isMethod;
    private ExprList exprList;
    private Type type;

    

    
}
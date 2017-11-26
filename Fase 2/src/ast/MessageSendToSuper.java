/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

public class MessageSendToSuper extends MessageSend { 

    public MessageSendToSuper(String id, ExprList exprList, Type type) {
        this.id = id;
        this.exprList = exprList;
        this.type = type;
    }   
    
    @Override
    public boolean isMethod() {
        return true;
    }
    
    @Override
    public String getPrimaryExprName() {
        String primaryExprName = "super." + this.id + "(";                        
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
        return primaryExprName;
    }
    
    @Override
    public Type getType() { 
        return this.type;
    }

    @Override
    public void genC( PW pw ) {
        pw.print("super::" + this.id + "(");                        
        if (exprList != null)
            exprList.genC(pw, false);        
        pw.print(")");                  
    }

    @Override
    public void genKra(PW pw) {
        pw.print("super." + this.id + "(");                        
        if (exprList != null)
            exprList.genKra(pw);        
        pw.print(")");                        
    }
    
    private String id;
    private ExprList exprList;
    private Type type;
}
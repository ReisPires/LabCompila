/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.util.ArrayList;

public class StatementRead extends Statement {

    public StatementRead(ArrayList<LeftValue> leftValues) {
        this.leftValues = leftValues;
    }            

    @Override
    public void genC(PW pw) {
        pw.printIdent("cin >> ");
        for (int i = 0; i < leftValues.size(); ++i) {
            leftValues.get(i).genC(pw);
            if (i < leftValues.size() - 1)
                pw.print(" >> ");
        }
        pw.println(";");
    }
        
    @Override
    public void genKra(PW pw) {
        pw.printIdent("read(");
        for (int i = 0; i < leftValues.size(); ++i) {
            leftValues.get(i).genKra(pw);
            if (i < leftValues.size() - 1)
                pw.print(",");
        }
        pw.println(");");
    }
    
    private ArrayList<LeftValue> leftValues;
}

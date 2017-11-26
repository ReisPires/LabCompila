/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/
    
package ast;

import java.util.ArrayList;

/** This class represents a metaobject call as <code>{@literal @}ce(...)</code> in <br>
 * <code>
 * @ce(5, "'class' expected") <br>
 * clas Program <br>
 *     public void run() { } <br>
 * end <br>
 * </code>
 * 
   @author Jos�
   
 */
public class MetaobjectCall {

	public MetaobjectCall(String name, ArrayList<Object> paramList) {
		this.name = name;
		this.paramList = paramList;
	}
	
	public ArrayList<Object> getParamList() {
		return paramList;
	}
	public String getName() {
		return name;
	}
        
        public void genKra(PW pw) {
            pw.printIdent("@" + name);
            if (paramList != null && paramList.size() != 0) pw.printIdent("(");
            for (int i = 0; i < paramList.size(); ++i) {
                if (paramList.get(i) instanceof String) { 
                    pw.printIdent("\"" + (String)paramList.get(i) + "\"");                    
                    if(i != paramList.size() - 1) 
                        pw.printlnIdent(",");                    
                }
                else {
                    pw.printIdent(paramList.get(i).toString());                                       
                    if(i != paramList.size() - 1) 
                        pw.printIdent(",");                    
                }                                
            }
            if (paramList != null && paramList.size() != 0) pw.printlnIdent(")");
            else pw.println();
        }


	private String name;
	private ArrayList<Object> paramList;

}

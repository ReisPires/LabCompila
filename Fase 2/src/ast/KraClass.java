/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
   }
   
   public String getCname() {
      return getName();
   }

    public KraClass getSuperclass() {
        return superclass;
    }

    public void setSuperclass(KraClass superclass) {
        this.superclass = superclass;
    }

    public InstanceVariableList getInstanceVariableList() {
        return instanceVariableList;
    }

    public void setInstanceVariableList(InstanceVariableList instanceVariableList) {
        this.instanceVariableList = instanceVariableList;
    }
    
    
   
    public ArrayList<MethodDec> getMethodList() {
        return methodList;
    }

    public void setMethodList(MethodDec methodDec) {
        methodList.add(methodDec);
    }
    
    public int getSize(){
        return methodList.size();
    }
    
    public void genKra(PW pw) {
        pw.println();
        pw.printIdent("class " + getCname());
        if (superclass != null) {
            pw.printIdent(" extends " + superclass.getCname());
        }
        pw.printlnIdent(" {");
        pw.add();        
        if (instanceVariableList != null) {
            Iterator itr = instanceVariableList.elements();
            while(itr.hasNext()) {
                InstanceVariable instanceVariable = (InstanceVariable)itr.next();
                instanceVariable.genKra(pw);
            }
        }
        pw.println();
        for (MethodDec md : methodList) {
            md.genKra(pw);
        }
        pw.sub();
        pw.println();
        pw.printlnIdent("}");
        pw.println();
    }
    
    public void genC(PW pw) {
        boolean hasPublic = false, hasConstructor = false;
        
        pw.println();                
        pw.printIdent("class " + getCname());
        if (superclass != null) {
            pw.printIdent(" : public " + superclass.getCname());
        }
        pw.printlnIdent(" {");
        pw.add();        
        
        /* Exibe atributos e metodos privados */            
        pw.add();
        if (instanceVariableList != null) {            
            Iterator itr = instanceVariableList.elements();
            while(itr.hasNext()) {
                InstanceVariable instanceVariable = (InstanceVariable)itr.next();
                if (instanceVariable.getQualifier().compareTo("private") == 0) {
                    instanceVariable.genC(pw, false);
                }
                else {
                    hasPublic = true;
                }               
                if (!hasConstructor && instanceVariable.getType() instanceof TypeString){
                    hasConstructor = true;
                }
            }
            pw.println();
        }        
        for (MethodDec md : methodList) {
            if (md.getVariable().getQualifier().compareTo("private") == 0) {
                md.genC(pw);
            } 
            else {
                hasPublic = true;
            }
        }
        pw.sub();
        
        /* Exibe atributos e metodos publicos */    
        if (hasPublic || (this.superclass != null) || hasConstructor) {
           pw.printlnIdent("public: ");
           pw.add();
           /* Super */
           if (this.superclass != null) {
               pw.printlnIdent("typedef " + this.superclass.getName() + " super;");               
           }                        
           
           /* Construtor */
           if (hasConstructor) {
               pw.printlnIdent(getCname() + "() {");
               pw.add();               
               Iterator itr = instanceVariableList.elements();
               while(itr.hasNext()) {
                   InstanceVariable instanceVariable = (InstanceVariable)itr.next();
                   if (instanceVariable.getType() instanceof TypeString) {
                       pw.printlnIdent("this->" + instanceVariable.getName() + " = new string();");
                   }                      
               }               
               pw.sub();
               pw.printlnIdent("}");
           }
           
           /* Metodos */
           for (MethodDec md : methodList) {               
               md.genC(pw);               
           }
           pw.sub();
        }
        
        pw.sub();
        pw.println();
        pw.printlnIdent("};");
        pw.println();
    }
    
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private ArrayList<MethodDec> methodList = new ArrayList<MethodDec>(); 
   
   // private MethodList publicMethodList, privateMethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos

  


}

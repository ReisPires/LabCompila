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
    
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private ArrayList<MethodDec> methodList = new ArrayList<MethodDec>(); 
   // private MethodList publicMethodList, privateMethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos


}

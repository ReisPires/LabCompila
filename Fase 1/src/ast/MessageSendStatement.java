package ast;

public class MessageSendStatement extends Statement { 


   public void genC( PW pw ) {
      pw.printIdent("");
      // messageSend.genC(pw);
      pw.printlnIdent(";");
   }   

    @Override
    public void genKra(PW pw) {        
    }

    private MessageSend  messageSend;
}



/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package ast;

import java.io.*;


public class PW {
   
   public void add() {
      currentIndent += step;
   }
   public void sub() {
	   if ( currentIndent < step ) {
		   System.out.println("Internal compiler error: step (" + step + ") is greater then currentIndent (" + currentIndent + ") in method sub of class PW");
	   }
      currentIndent -= step;
   }
   
   public void set( PrintWriter out ) {
      this.out = out;
      currentIndent = 0;
   }
   
   public void set( int indent ) {
      currentIndent = indent;
   }
   
   public void printIdent( String s ) {
      out.print( space.substring(0, currentIndent) );
      out.print(s);
   }
   
   public void printlnIdent( String s ) {
      out.print( space.substring(0, currentIndent) );
      out.println(s);
   }

   public void print( String s ) {
      out.print(s);
   }
   
   public void println( String s ) {
      out.println(s);
   }

   public void println() {
	      out.println("");
	   }
   
   
   int currentIndent = 0;
   public int step = 3;
   private PrintWriter out;
         
   
   static final private String space = "                                                                                                        ";

}
      
       

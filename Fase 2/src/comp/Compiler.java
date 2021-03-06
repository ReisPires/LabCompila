/*
    Fase 1 - Trabalho Laboratorio de Compiladores
    Gabriela Ramos      620360
    Pedro Reis Pires    620068
*/

package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new ErrorSignaller(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= KraClass { KraClass }
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		ArrayList<KraClass> kraClassList = new ArrayList<>();
		Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
		try {
			while ( lexer.token == Symbol.MOCall ) {
				metaobjectCallList.add(metaobjectCall());
			}
                        
			kraClassList.add(classDec());
                        
			while ( lexer.token == Symbol.CLASS )
				kraClassList.add(classDec());
                        
                        if(symbolTable.getInGlobal("Program") == null){
                            signalError.showError("Source code without a class 'Program'");
                        }

			if ( lexer.token != Symbol.EOF ) {
				signalError.showError("End of file expected");
			}
		}
                catch( CompilerError e) {
                 // if there was an exception, there is a compilation signalError
                }
                catch ( RuntimeException e ) {
                    e.printStackTrace();
                }
		return program;
	}

	/**  parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     *     public void run() { } <br>
     * end <br>
     * </code>
     *

	 */
	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA )
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR )
				signalError.showError("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.showError("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.showError("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.showError("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.showError("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )
				signalError.showError("The fourth parameter of metaobject 'ce' should be a literal string");

		}

		return new MetaobjectCall(name, metaobjectParamList);
	}

	private KraClass classDec() {
		// Note que os m�todos desta classe n�o correspondem exatamente �s
		// regras
		// da gram�tica. Este m�todo classDec, por exemplo, implementa
		// a produ��o KraClass (veja abaixo) e partes de outras produ��es.

		/*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member }
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";"
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}"
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
		 */
                boolean isRun = false;

		if ( lexer.token != Symbol.CLASS ) signalError.showError("'class' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.IDENT )
			signalError.show(ErrorSignaller.ident_expected);
		String className = lexer.getStringValue();
                
                KraClass kClass = new KraClass(className);
		symbolTable.putInGlobal(className, kClass);                
                curClass = className;

		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);
			String superclassName = lexer.getStringValue();
                        
                        KraClass superClass = symbolTable.getInGlobal(superclassName);
                        if (superClass == null){
                            signalError.showError("Class " + superclassName + " not defined");
                        }
                        else{                                                        
                            if (className.compareTo(superclassName) != 0){
                                  kClass.setSuperclass(superClass);                                  
                            }
                            else{
                                signalError.showError("Class '" + className + "' is inheriting from itself");
                            }
                        }

			lexer.nextToken();
		}
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("'{'e expected", true);
		lexer.nextToken();
                                
                
		while (lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC) {

			Symbol qualifier;

			switch (lexer.token) {
			case PRIVATE:
				lexer.nextToken();
                                
				qualifier = Symbol.PRIVATE;

				break;
			case PUBLIC:
				lexer.nextToken();
				qualifier = Symbol.PUBLIC;
				break;
			default:
				signalError.showError("private, or public expected");
				qualifier = Symbol.PUBLIC;
			}

                        if(lexer.token == Symbol.STATIC){
                            signalError.showError("Identifier expected");
                        }

			Type t = type();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			String name = lexer.getStringValue();
                                                
			lexer.nextToken();                                                
                        
			if ( lexer.token == Symbol.LEFTPAR ) {
                            if (name.compareTo("run") == 0){
                                isRun = true;
                            }
                            methodDec(t, name, qualifier);    
                        }
			else if ( qualifier != Symbol.PRIVATE )
                            signalError.showError("Attempt to declare a public instance variable");
			else
                            instanceVarDec(t, name);
                        
		}                
                
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("'public', 'private' or '}' expected");
		
                if ((className.compareTo("Program") == 0) && isRun == false ){
                    signalError.showError("Method 'run' was not found in class 'Program");
                }
                
                lexer.nextToken();
                                
                curClass = "";
                
                return kClass;
	}

	private void instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"                           
                
                KraClass KClass = symbolTable.getInGlobal(curClass);
                
                if (KClass.getInstanceVariableList() == null) {                    
                    InstanceVariableList instance = new InstanceVariableList();
                    instance.addElement(new InstanceVariable(name, type));                   
                    KClass.setInstanceVariableList(instance);                   
                }
                else {
                    Iterator<InstanceVariable> itr;
                    InstanceVariableList instancias = KClass.getInstanceVariableList();                  
                    itr = instancias.elements();
                                        
                    while(itr.hasNext()){                                           
                        if (itr.next().getName().compareTo(name)== 0){
                            signalError.showError("Variable '" + name +"' is being redeclared");                           
                        }
                    } 
                    InstanceVariable instanceVariable = new InstanceVariable(name, type);
                    instancias.addElement(instanceVariable);                   
                }                                    
                
		while (lexer.token == Symbol.COMMA) {                    
                    lexer.nextToken();
                    if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");
                        
                    String variableName = lexer.getStringValue();
                    Iterator<InstanceVariable> itr;
                    InstanceVariableList instancias = KClass.getInstanceVariableList();                  
                    itr = instancias.elements();                                        
                    while(itr.hasNext()){                                           
                        if (itr.next().getName().compareTo(variableName)== 0){
                            signalError.showError("Variable '" + variableName +"' is being redeclared");                           
                        }
                    } 
                    InstanceVariable instanceVariable = new InstanceVariable(variableName, type);
                    instancias.addElement(instanceVariable);                   
                    lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
	}
        
        private boolean checkReturnStatement(StatementReturn returnStmt, Type type) {            
            Expr e = returnStmt.getExpr();
            if (e == null)
                signalError.showError("Illegal 'return' statement. Method returns '" + type.getName() + "'");           
            if (e != null)
                if (e.getType() instanceof KraClass && type instanceof KraClass) {
                    if (!checkInheritance((KraClass)e.getType(), (KraClass)type))
                        signalError.showError("Illegal 'return' statement. Method returns '" + type.getName() + "'");           
                } else if (e.getType() != type)
                        signalError.showError("Illegal 'return' statement. Method returns '" + type.getName() + "'");           
                
            return true;
        }
        
        private boolean hasReturn(StatementIf ifStmt, Type type) {
            boolean thenHasReturn = false, elseHasReturn = false;
            
            if (ifStmt.getThenStmt() instanceof StatementReturn) {
                thenHasReturn = checkReturnStatement((StatementReturn)ifStmt.getThenStmt(), type);
            } else if (ifStmt.getThenStmt() instanceof StatementIf) {
                thenHasReturn = hasReturn((StatementIf)ifStmt.getThenStmt(), type);
            } else if (ifStmt.getThenStmt() instanceof CompositeStatement) {
                thenHasReturn = hasReturn(((CompositeStatement)ifStmt.getThenStmt()).getStatementList(), type);
            }
            
            if (ifStmt.getElseStmt() instanceof StatementReturn) {                
                elseHasReturn = checkReturnStatement((StatementReturn)ifStmt.getElseStmt(), type);
            } else if (ifStmt.getElseStmt() instanceof StatementIf) {
                elseHasReturn = hasReturn((StatementIf)ifStmt.getElseStmt(), type);
            } else if (ifStmt.getElseStmt() instanceof CompositeStatement) {
                elseHasReturn = hasReturn(((CompositeStatement)ifStmt.getElseStmt()).getStatementList(), type);
            }
            
            return (thenHasReturn && elseHasReturn);
        }
        
        private boolean hasReturn(StatementList stmts, Type type) {                    
            boolean stmtHasReturn = false;
            if (stmts != null) {
                for (int i = 0; i < stmts.getList().size() && !stmtHasReturn; ++i) {                    
                    if (stmts.getList().get(i) != null){
                        if (stmts.getList().get(i) instanceof  StatementReturn) {                                                       
                            stmtHasReturn = checkReturnStatement((StatementReturn)stmts.getList().get(i), type);
                        } else if (stmts.getList().get(i) instanceof StatementIf) {                         
                            stmtHasReturn = hasReturn((StatementIf)stmts.getList().get(i), type);
                        } else if (stmts.getList().get(i) instanceof CompositeStatement) {                            
                            stmtHasReturn = hasReturn(((CompositeStatement)stmts.getList().get(i)).getStatementList(), type);
                        }
                    }
                }
            }
            return stmtHasReturn;
        }

	private void methodDec(Type type, String name, Symbol qualifier) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
                                
                boolean isProgramRun = false;
                if( curClass.compareTo("Program") == 0 && name.compareTo("run") == 0){

                    if (qualifier == Symbol.PRIVATE){
                        signalError.showError("Method '" +  name + "' of class '" + curClass +"' cannot be private");
                    }
                    if (type != Type.voidType){
                        signalError.showError("Method '" +  name + "' of class '" + curClass + "' with a return value type different from 'void'");
                    }
                    isProgramRun = true;
                }

                /* Setar os metodos */
                KraClass cClass = symbolTable.getInGlobal(curClass);
                
                KraClass superClasses = cClass.getSuperclass();
                InstanceVariableList instancias = cClass.getInstanceVariableList();
		lexer.nextToken();
                
                /*Verificar se o metodo nao possui o mesmo nome da instancia de variavel*/
                if (instancias != null){
                    Iterator<InstanceVariable> itr;
                    itr = instancias.elements();
                    while(itr.hasNext()){
                        Variable v = itr.next();
                        if (v.getName().compareTo(name) == 0){
                            signalError.showError("Method '"+name+"' has name equal to an instance variable");
                        }
                    }
                }
                ParamList params = null;
		if ( lexer.token != Symbol.RIGHTPAR ){ 
                    params = formalParamDec();
                    
                    if (isProgramRun == true && params.getSize() != 0){
                        signalError.showError("Method '" +  name + "' of class '" + curClass + "' cannot take parameters");
                    }
                }
                Variable var = new Variable(name, type, qualifier.toString(), params);
                
                ArrayList<MethodDec> methods = cClass.getMethodList();
                Iterator<Variable> itr;
                Iterator<Variable> parametros;
             
                /* Possui super classe. Verificar se o metodo será redefinido */
                if(superClasses != null) {
                    do {
                        ArrayList<MethodDec> superClassMethods = superClasses.getMethodList();
                        if (superClassMethods != null) {
                            for (MethodDec md : superClassMethods) {
                                Variable v = md.getVariable();
                                if(v.getName().compareTo(name) == 0){
                                    /*comparar os parametro*/
                                    if (params != null && v.getParam() != null){
                                        itr = v.getParam().elements();
                                        parametros = params.elements();
                                        while(itr.hasNext() && parametros.hasNext()) {
                                           Variable element = itr.next();
                                           Variable pElement = parametros.next();

                                           if(element.getType().getName() != pElement.getType().getName()){
                                               signalError.showError("Method '"+ name +"' is being redefined in subclass '" + curClass +"' with a signature different from the method of superclass '"+ superClasses.getCname() +"'");
                                               break;
                                           }
                                        }
                                    }
                                    if (v.getType() != type){
                                        signalError.showError("Method '"+ name +"' of subclass '"+ curClass +"' has a signature different from method inherited from superclass '" +superClasses.getCname() +"'");
                                    }
                                }
                            }
                        }
                        superClasses =  superClasses.getSuperclass();
                    } while (superClasses != null);
                    
                }
               
                for(MethodDec md : methods){
                    Variable v = md.getVariable();
                    if (v.getName().compareTo(var.getName()) == 0){
                        signalError.showError("Method '" + name + "' is being redefined");
                        break;
                    }
                }   
                
                cClass.setMethodList(new MethodDec(var));
                
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.showError("'{' expected");

		lexer.nextToken();               
                curMethod = var;                
                StatementList stmts = statementList();                                 
                curMethod = null;
                                   
                // Check if 'return' is missing
                 if (!hasReturn(stmts, type) && type != Type.voidType)
                    signalError.showError("Missing 'return' statement in method '" + name + "'");                 
                
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.showError("'}' expected");

		lexer.nextToken();
                                                
                symbolTable.removeLocalIdent();                                

                cClass.getMethodList().get(cClass.getMethodList().size()-1).setStatementList(stmts);
        
	}

	private LocalDec localDec() {
		// LocalDec ::= Type IdList ";"
                
		Type type = type();
                
                VariableList varList = new VariableList();
                
		if ( lexer.token != Symbol.IDENT ) signalError.showError("Identifier expected");
		Variable v = new Variable(lexer.getStringValue(), type);                
                
                if(symbolTable.getInLocal(v.getName()) != null){
                    signalError.showError("Variable '" + v.getName() + "' is being redeclared", true);
                } else{
                    symbolTable.putInLocal(v.getName(), v);
                    varList.addElement(v);
                }

		lexer.nextToken();

                if(lexer.token != Symbol.COMMA && lexer.token != Symbol.SEMICOLON){
                     signalError.showError("Missing ';'", true);
                }
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			v = new Variable(lexer.getStringValue(), type);
                        
                        if(symbolTable.getInLocal(v.getName()) != null){
                            signalError.showError("Variable '" + v.getName() + "'is being redeclared");
                        } else{
                            symbolTable.putInLocal(v.getName(), v);
                            varList.addElement(v);
                        }
			lexer.nextToken();

		}                
                lexer.nextToken();
                
                return new LocalDec(type, varList);
	}

	private ParamList formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }
                ParamList paramsDec = new ParamList();
		paramsDec.addElement(paramDec());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			paramsDec.addElement(paramDec());
		}
                return paramsDec;
	}

	private Variable paramDec() {
		// ParamDec ::= Type Id

		Type t = type();
                
		if ( lexer.token != Symbol.IDENT ) signalError.showError("Identifier expected");
                Variable v = new Variable(lexer.getStringValue(), t);
                symbolTable.putInLocal(v.getName(), v);

		lexer.nextToken();
                
                return new Parameter(v.getName(), v.getType());
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result;
                
		switch (lexer.token) {
                    
		case VOID:
			result = Type.voidType;
			break;
		case INT:
			result = Type.intType;
			break;
		case BOOLEAN:
                     
			result = Type.booleanType;
                        
			break;
		case STRING:
			result = Type.stringType;
			break;
		case IDENT:
			// # corrija: fa�a uma busca na TS para buscar a classe
			// IDENT deve ser uma classe. (No sentido de criar uma classe ou classe da ast?
                        
                        KraClass classe = symbolTable.getInGlobal(lexer.getStringValue());                        
			result = classe;
                        if (result == null) {
                            signalError.showError("Class '" + lexer.getStringValue() + "' not defined");
                        }
			break;
		default:
			signalError.showError("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private CompositeStatement compositeStatement() {
		if (lexer.token != Symbol.LEFTCURBRACKET)
                    signalError.showError("'{' expected");
                lexer.nextToken();
		StatementList stmtList = statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("'}' expected");
		else
			lexer.nextToken();
                return new CompositeStatement(stmtList);
	}

	private StatementList statementList() {
		// CompStatement ::= "{" { Statement } "}"
		
		StatementList stmts = new StatementList();

                Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			stmts.addElement(statement());
                
                return stmts;
	}

	private Statement statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER: 
		case INT:
		case BOOLEAN:
		case STRING:
			return assignExprLocalDec();			
		case ASSERT:
			return assertStatement();			
		case RETURN:
			return returnStatement();			
		case READ:
			return readStatement();			
		case WRITE:
			return writeStatement(false);			
		case WRITELN:
			return writeStatement(true);			
		case IF:
			return ifStatement();			
		case BREAK:
			return breakStatement();			
		case WHILE:
			return whileStatement();	
                //case DO:
                        //return doWhileStatement();
		case SEMICOLON:
			return nullStatement();			
		case LEFTCURBRACKET:                    
			return compositeStatement();	                
		default:
			signalError.showError("Statement expected");
		}
                
                return null;
	}

	private StatementAssert assertStatement() {
		lexer.nextToken();
		int lineNumber = lexer.getLineNumber();
		Expr e = expr();
		if ( e.getType() != Type.booleanType )
			signalError.showError("boolean expression expected");
		if ( lexer.token != Symbol.COMMA ) {
			this.signalError.showError("',' expected after the expression of the 'assert' statement");
		}
		lexer.nextToken();
		if ( lexer.token != Symbol.LITERALSTRING ) {
			this.signalError.showError("A literal string expected after the ',' of the 'assert' statement");
		}
		String message = lexer.getLiteralStringValue();
		lexer.nextToken();
		if ( lexer.token == Symbol.SEMICOLON )
			lexer.nextToken();

		return new StatementAssert(e, lineNumber, message);
	}

	/*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private AssignExprLocalDec assignExprLocalDec() {
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token � uma classe declarada textualmente antes desta
				// instru��o
				(lexer.token == Symbol.IDENT) && isType(lexer.getStringValue()) && 
                                symbolTable.getInLocal(lexer.getStringValue()) == null) {
                        
			/*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 *
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
			 * LocalDec ::= Type IdList ``;''
			 */
                        
			return localDec();
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
                        
			Expr expr1 = expr();
                        Expr expr2 = null;
                        
			if ( lexer.token == Symbol.ASSIGN ) {
			lexer.nextToken();
				expr2 = expr();
                                
                                /* Verificar os tipos básicos */
                                if ((expr1.getType() instanceof TypeInt) || (expr1.getType() instanceof TypeBoolean)){
                                    if(expr2 instanceof NullExpr){
                                        signalError.showError("Type error: 'null' cannot be assigned to a variable of a basic type");
                                    }
                                    if (expr1.getType() instanceof TypeInt && (expr2.getType() instanceof TypeBoolean)) {
                                        signalError.showError("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
                                    }
                                    if (expr1.getType() instanceof TypeInt && expr2.getType() instanceof KraClass) {
                                        signalError.showError("Type error: type of the left-hand side of the assignment is a basic type and the type of the right-hand side is a class");
                                    }
                                }
                                if (expr1.getType() instanceof TypeBoolean) {
                                    if (!(expr2.getType() instanceof TypeBoolean)) {
                                        signalError.showError("'"+ expr2.getType().getCname() + "' cannot be assigned to 'boolean'");
                                    }
                                }
                                if (expr1.getType() instanceof KraClass && !(expr2 instanceof NullExpr)) {
                                    
                                    if (expr1.getType().getCname().compareTo(expr2.getType().getCname()) != 0) {
                                        if (expr2.getType() instanceof KraClass) {
                                        KraClass bClass = symbolTable.getInGlobal(expr2.getType().getCname());

                                            if (bClass.getSuperclass() == null) {
                                                signalError.showError("Type error: type of the right-hand side of the assignment is not a subclass of the left-hand side");
                                            }
                                            else {
                                               boolean haveSuper = false;
                                               do{
                                                if (bClass.getCname().compareTo(expr1.getType().getCname()) == 0){
                                                    haveSuper = true;
                                                }

                                                bClass =  bClass.getSuperclass();

                                                }while (bClass != null);
                                               if (!haveSuper ) {
                                                   signalError.showError("Type error: type of the right-hand side of the assignment is not a subclass of the left-hand side");
                                               }
                                            }
                                        } 
                                        else {
                                            signalError.showError("Type error: the type of the expression of the right-hand side is a basic type and the type of the variable of the left-hand side is a class");
                                        }
                                    }
                                }
                                if (expr2 != null) {
                                    
                                   if ( expr2.getType() instanceof TypeVoid) {
                                           signalError.showError("Expression expected in the right-hand side of assignment");
                                    }
                                }				                                
			}                                                           
                                                
                        if ((expr1 instanceof MessageSendToVariable || expr1 instanceof MessageSendToSuper || expr1 instanceof MessageSendToSelf) && expr2 == null) {                            
                            if (expr1 instanceof MessageSendToVariable && ((MessageSendToVariable)expr1).isMethod() && ((MessageSendToVariable)expr1).getType() != Type.voidType)                                 
                                signalError.showError("Message send '" + ((MessageSendToVariable)expr1).getPrimaryExprName() + "' returns a value that is not used");
                            else if (expr1 instanceof MessageSendToSuper && ((MessageSendToSuper)expr1).isMethod() && ((MessageSendToSuper)expr1).getType() != Type.voidType)                                 
                                signalError.showError("Message send '" + ((MessageSendToSuper)expr1).getPrimaryExprName() + "' returns a value that is not used");
                            else if (expr1 instanceof MessageSendToSelf && ((MessageSendToSelf)expr1).isMethod() && ((MessageSendToSelf)expr1).getType() != Type.voidType)                                 
                                signalError.showError("Message send '" + ((MessageSendToSelf)expr1).getPrimaryExprName() + "' returns a value that is not used");
                        }
                        
                        if ( lexer.token != Symbol.SEMICOLON )
                            signalError.showError("';' expected", true);
			else
                            lexer.nextToken();
                                    
                        return new AssignExpr(expr1, expr2);
		}	
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		return anExprList;
	}

	private Statement whileStatement() {
            whileStmt.push("true");
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
		lexer.nextToken();
		Expr e = expr();
                if (!(e.getType() instanceof TypeBoolean)){
                    signalError.showError("non-boolean expression in 'while' command");
                }
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		Statement stmt = statement();
            whileStmt.pop();
            
            return new StatementWhile(e, stmt);
	}
        
    /*    private Statement doWhileStatement() {
            whileStmt.push("true");    
                lexer.nextToken();
                CompositeStatement compositeStatement = compositeStatement();
                if (lexer.token != Symbol.WHILE)
                    signalError.showError("'while' expected");
                lexer.nextToken();
                if (lexer.token != Symbol.LEFTPAR)
                    signalError.showError("'(' expected");
                lexer.nextToken();
                Expr e = expr();
                if (!(e.getType() instanceof TypeBoolean)){
                    signalError.showError("non-boolean expression in 'do while' command");
                }
                if (lexer.token != Symbol.RIGHTPAR)
                    signalError.showError("')' expected");
                lexer.nextToken();
                if (lexer.token != Symbol.SEMICOLON)
                    signalError.showError("';' expected");
                lexer.nextToken();
            whileStmt.pop();
            
            return new StatementDoWhile(compositeStatement, e);
        }
    */
        
	private StatementIf ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
		lexer.nextToken();
		Expr e = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		Statement thenStmt = statement();
                Statement elseStmt = null;
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			elseStmt = statement();
		}
                
                return new StatementIf(e, thenStmt, elseStmt);
	}

	private StatementReturn returnStatement() {                
                
                if (curMethod.getType() instanceof TypeVoid){
                   signalError.showError("Illegal 'return' statement. Method returns 'void'");
                }
                
		lexer.nextToken();
		Expr e = expr();
                if (curMethod.getType() instanceof KraClass) {
                   KraClass aClass = symbolTable.getInGlobal(curMethod.getType().getName());
                   KraClass bClass = symbolTable.getInGlobal(e.getType().getName());
                   
                   if (bClass == null)
                       signalError.showError("Type error: type of the expression returned is not a class");
                   
                   if (aClass.getCname().compareTo(bClass.getCname()) != 0){
                       
                    if (aClass.getSuperclass() == null && bClass.getSuperclass() == null) {
                         signalError.showError("Type error: type of the expression returned is not subclass of the method return type");
                    }
                    else {
                          if (bClass.getSuperclass() != null) {
                             boolean haveSuper = false;
                             bClass =  symbolTable.getInGlobal(bClass.getSuperclass().getCname());

                            /* fazer while ate que nao tenha mais super classe */
                            do{
                                if (bClass.getCname().compareTo(aClass.getCname()) == 0){
                                    haveSuper = true;
                                }

                                bClass =  bClass.getSuperclass();

                            }while (bClass != null);

                            if (!haveSuper) {
                                 signalError.showError("Type error: type of the expression returned is not subclass of the method return type");
                               }
                          }
                          else {
                              signalError.showError("Type error: type of the expression returned is not subclass of the method return type");
                          }
                          
                       }

                   }
                }
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
                return new StatementReturn(e);
	}

	private StatementRead readStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
		lexer.nextToken();
                
                ArrayList<LeftValue> leftValues = new ArrayList<>();
                
		while (true) {
                        boolean hasThis = false;
                        
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError("'.' expected");
				lexer.nextToken();
                                hasThis = true;
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Command 'read' without arguments");

			String name = lexer.getStringValue();
                        Variable v = symbolTable.getInLocal(name);
                        if (v == null) {                            
                            KraClass kraClass = symbolTable.getInGlobal(curClass);
                            InstanceVariableList instanceVariableList = kraClass.getInstanceVariableList();
                            if (instanceVariableList != null) {
                                Iterator itr = instanceVariableList.elements();
                                while (itr.hasNext()) {
                                    InstanceVariable instanceVariable = (InstanceVariable)itr.next();
                                    if (instanceVariable.getName().equals(name)) {
                                        v = (Variable)instanceVariable;
                                        break;
                                    }
                                }
                            }                           
                        }
                        if (v == null) {
                            signalError.showError("Variable '" + name + "' not declared");
                        }
                        
                        if (v.getType() instanceof TypeBoolean){
                            signalError.showError("Command 'read' does not accept 'boolean' variables");
                        }
                        
                        leftValues.add(new LeftValue(v, hasThis));
                        
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;                                                
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
                return new StatementRead(leftValues);
	}

        //“write” “(” ExpressionList “)”
	private StatementWrite writeStatement(boolean hasLineBreak) {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
		lexer.nextToken();
                ExprList exprList = exprList();                               
                
                ArrayList<Expr> arrayExpr = new ArrayList<Expr>();
                arrayExpr = exprList.getExpr();                                
                
                for(Expr e : arrayExpr){
                  
                    if (e.getType() instanceof KraClass){
                        signalError.showError("Command 'write' does not accept objects");
                        
                    }
                    if (e.getType() instanceof TypeBoolean){
                        signalError.showError("Command 'write' does not accept 'boolean' expressions");
                    }
                }
                
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
                return new StatementWrite(exprList, hasLineBreak);
	}

	private StatementBreak breakStatement() {

            if(whileStmt.empty()){

                signalError.showError("Command 'break' outside a command 'while'");
            }
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
            return new StatementBreak();
	}

	private StatementNull nullStatement() {
		lexer.nextToken();
                return new StatementNull();
	}

	private ExprList exprList() {
		// ExpressionList ::= Expression { "," Expression }

		ExprList anExprList = new ExprList();
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		return anExprList;
	}

	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
                        
                        if (left.getType() instanceof KraClass) {
                           
                            if (!(right instanceof NullExpr) && left.getType().getName().compareTo(right.getType().getName()) != 0) {
                              KraClass aClass = symbolTable.getInGlobal(left.getType().getName()); 
                              KraClass bClass = symbolTable.getInGlobal(right.getType().getName());
                             
                              if (aClass.getSuperclass() == null && bClass.getSuperclass() == null) {
                                  
                                  signalError.showError("Incompatible types cannot be compared with '"+ op.toString() +"' because the result will always be 'false'");
                              }
                              else {
                                  if (aClass.getSuperclass() != null){
                                      boolean haveSuper = false;
                                     aClass =  symbolTable.getInGlobal(aClass.getSuperclass().getCname());
                                    
                                    /* fazer while ate que nao tenha mais super classe */
                                    do{
                                        if (aClass.getCname().compareTo(bClass.getCname()) == 0){
                                            haveSuper = true;
                                        }
    
                                        aClass =  aClass.getSuperclass();
                                       
                                    }while (aClass != null);
                                    
                                    if (!haveSuper) {
                                        signalError.showError("Incompatible types cannot be compared with '"+ op.toString() +"' because the result will always be 'false'");
                                    }
                                  }
                                  else if (bClass.getSuperclass() != null && aClass.getSuperclass() == null) {
                                     boolean haveSuper = false;
                                     bClass =  symbolTable.getInGlobal(bClass.getSuperclass().getCname());
                                    
                                    /* fazer while ate que nao tenha mais super classe */
                                    do{
                                        if (bClass.getCname().compareTo(aClass.getCname()) == 0){
                                            haveSuper = true;
                                        }
    
                                        bClass =  bClass.getSuperclass();
                                       
                                    }while (bClass != null);
                                    
                                    if (!haveSuper) {
                                        signalError.showError("Incompatible types cannot be compared with '"+ op.toString() +"' because the result will always be 'false'");
                                    }
                                  } 
                                  else {
                                    boolean haveSuper = false;
                                    bClass =  symbolTable.getInGlobal(bClass.getSuperclass().getCname());
                                    KraClass cClass =  symbolTable.getInGlobal(aClass.getSuperclass().getCname());
                                    /* fazer while ate que nao tenha mais super classe */
                                    do{
                                       do {
                                        if (cClass.getCname().compareTo(bClass.getCname()) == 0){
                                            haveSuper = true;
                                        }
                                        cClass =  aClass.getSuperclass();
                                       } while (cClass != null);
                                       bClass =  bClass.getSuperclass();
                                       cClass =  symbolTable.getInGlobal(aClass.getSuperclass().getCname());
                                    }while (bClass != null);
                                    
                                    if (!haveSuper) {
                                        signalError.showError("Incompatible types cannot be compared with '"+ op.toString() +"' because the result will always be 'false'");
                                    }
                                  }
                              }
                            }
                        }
			left = new CompositeExpr(left, op, right);
		}
                
		return left;
	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
                        /*VERIFICAR SE O OR ENTRA NA CONDIÇÃO ABAIXO*/
                        if(op == Symbol.MINUS || op == Symbol.PLUS){
                            if (right.getType() != left.getType()){
                                signalError.showError("operator '"+ op.toString() +"' of '" + left.getType().getName() + "' expects an '"+left.getType().getName()+"' value");
                            }
                            if (!(right.getType() instanceof TypeInt) || !(left.getType() instanceof TypeInt)){
                                signalError.showError("type boolean does not support operation '"+ op.toString() +"'");
                            }
                        }
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
                        if (op == Symbol.AND){
                            if (!(left.getType() instanceof TypeBoolean) || !(right.getType() instanceof TypeBoolean)){
                                signalError.showError("type 'int' does not support operator '&&'");
                            }
                        }
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr signalFactor() {
		Symbol op;
               
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else                                               
			return factor();
	}
        
        // Check if A is subclass of B 
        private boolean checkInheritance(KraClass A, KraClass B) {
            do {
                if (A.getCname().equals(B.getCname())) return true;
                A = A.getSuperclass();
            } while (A != null);
            return false;
        }
                
        
        private boolean checkMethodParameters(ExprList exprList, Variable variable) {
            ParamList paramList = variable.getParam();
            if (paramList == null && exprList == null)                
                return true;                
            
            if (paramList != null && exprList != null) {
                Iterator itr = paramList.elements();
                int i;
                for (i = 0; i < exprList.getExpr().size() && itr.hasNext(); ++i) {                   
                    Expr e = exprList.getExpr().get(i);
                    Variable v = (Variable)itr.next();
                    if (e.getType() instanceof KraClass && v.getType() instanceof KraClass) {
                        if (!(checkInheritance((KraClass) e.getType(), (KraClass) v.getType())))
                            return false;
                    }
                    else if (e.getType() != v.getType()) {
                        if (!(e.getType() == Type.undefinedType && (v.getType() instanceof KraClass || v.getType() instanceof TypeString)))
                            return false;                 
                    }
                }

                if (i == exprList.getExpr().size() && !itr.hasNext())
                    return true;
            }
            return false;
        }
        

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 *
	 * BasicValue ::= IntValue | BooleanValue | StringValue
	 * BooleanValue ::=  "true" | "false"
	 * ObjectCreation ::= "new" Id "(" ")"
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  |
	 *                 Id  |
	 *                 Id "." Id |
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" |
	 *                 "this" "." Id |
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {
            
		Expr anExpr;
		ExprList exprList;
		String className;                
                KraClass kraClass;                                
                Variable methodVariable = null, varVariable = null;                
                String[] idList = new String[3];                
                
		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
			// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;
			// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);
			// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			anExpr = expr();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
			lexer.nextToken();
			return new ParenthesisExpr(anExpr);

			// "null"
		case NULL:
			lexer.nextToken();
                        
			return new NullExpr();
			// "!" Factor
		case NOT:
			lexer.nextToken();
			anExpr = expr();
                        if (anExpr.getType().getName().compareTo("int") == 0){
                            signalError.showError("Operator '!' does not accepts 'int' values");
                        }
			return new UnaryExpr(anExpr, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			className = lexer.getStringValue();


                        // encontre a classe className in symbol table KraClass
                        KraClass aClass = symbolTable.getInGlobal(className);

                        if ( aClass == null ) {
                            signalError.showError("Class '" + className + "' was not found");
                        }


			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("'(' expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
			lexer.nextToken();
			/*
			 * return an object representing the creation of an object
			 */
                        if( aClass != null){
                           
                            return new NewExpr(aClass);
                        }
			return null;
			/*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 Id  |
          	 *                 Id "." Id |
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" |
          	 *                 "this" "." Id |
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
		case SUPER:
                    
			// "super" "." Id "(" [ ExpressionList ] ")"                                            
                        
                        // Verify if class has super                         
                        kraClass =  symbolTable.getInGlobal(curClass);                                           
                        if (kraClass.getSuperclass()== null){
                            signalError.showError("'super' used in class '" + curClass + "' that does not have a superclass");
                        }
                                               
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT )
                            signalError.showError("'.' expected");                            
			                       
                        lexer.nextToken();
                        idList[0] = lexer.getStringValue();                            

                        if ( lexer.token != Symbol.IDENT )
                            signalError.showError("Identifier expected"); 
                        
                        // Search for method
                        methodVariable = null;
                        if (kraClass.getSuperclass() != null){
                            kraClass =  symbolTable.getInGlobal(kraClass.getSuperclass().getCname());
                            /* fazer while ate que nao tenha mais super classe */
                            do{
                                for(MethodDec md : kraClass.getMethodList()){
                                    Variable v = md.getVariable();
                                    if (v.getName().compareTo(idList[0]) == 0){
                                        if (v.getQualifier().compareTo("private") == 0) {
                                            signalError.showError("Method '"+ idList[0] + "' was not found in the public interface of '" + kraClass.getCname() + "' or its superclasses");
                                            break;
                                        }
                                        methodVariable = v;                                        
                                        break;
                                    }
                                }
                                kraClass =  kraClass.getSuperclass();
                            }while (kraClass != null && methodVariable == null);

                            if (methodVariable == null)
                                signalError.showError("Method '" + idList[0] + "' was not found in superclass '" + curClass + "' or its superclasses");                            
                        }
                                                                                               			
			lexer.nextToken();
			exprList = realParameters();
                        if (!checkMethodParameters(exprList, methodVariable))
                            signalError.showError("Wrong parameters for method '" + methodVariable.getName() +"'");
                        return new MessageSendToSuper(idList[0], exprList, methodVariable.getType());						
		case IDENT:
			/*
          	 * PrimaryExpr ::=
          	 *                 Id  |
          	 *                 Id "." Id |
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */
			
                        idList[0] = lexer.getStringValue();
                        
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
                            // Id
                            // retorne um objeto da ASA que representa um identificador
                                varVariable = symbolTable.getInLocal(idList[0]);
                                if(varVariable == null){
                                   signalError.showError("Variable '" + idList[0] + "' was not declared");
                                }                                
                                
				return new MessageSendToVariable(idList[0], varVariable.getType());
			}
			else { // Id "."
                                
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT )
                                    signalError.showError("Identifier expected");				
				
                                // Id "." Id
                                lexer.nextToken();
                                idList[1] = lexer.getStringValue();                                        
                                Variable variable = symbolTable.getInLocal(idList[0]);
                                
                                if (!(variable.getType() instanceof KraClass)){
                                    signalError.showError("Message send to a non-object receiver");
                                }                                        

                                kraClass = symbolTable.getInGlobal(variable.getType().getCname());
                                className = kraClass.getName();                                                                           

                                if ( lexer.token == Symbol.LEFTPAR ) {                                                                                                                                        
                                    // Id "." Id "(" [ ExpressionList ] ")"

                                    // Verifica se o segundo Id eh um metodo do primeiro Id e o captura                                                                                                                                                                                                        
                                    methodVariable = null;
                                    do {
                                        for (MethodDec md : kraClass.getMethodList()) {
                                            Variable v = md.getVariable();
                                            if (v.getName().compareTo(idList[1]) == 0){
                                                if (v.getQualifier().compareTo("private") == 0) {
                                                    signalError.showError("Method '"+ idList[1] + "' was not found in the public interface of '" + className + "' or its superclasses");
                                                    break;
                                                }
                                                methodVariable = v;                                                        
                                                break;
                                            } 
                                        }
                                        kraClass = kraClass.getSuperclass();
                                    } while (kraClass != null && methodVariable == null);    

                                    if (methodVariable == null)
                                        signalError.showError("Method '" + idList[1] + "' was not found in the public interface of '" + className +"' or its superclasses");

                                    exprList = this.realParameters();						                                                                                                                                                                                                                                                                                                                                                          

                                    if (!checkMethodParameters(exprList, methodVariable))
                                        signalError.showError("Wrong parameters for method '" + methodVariable.getName() + "'");                                                
                                    return new MessageSendToVariable(idList[0], idList[1], exprList, methodVariable.getType());
                                } 
                                else {
                                    /* Se chegou aqui o segundo Id tem que ser var */

                                    // Verifica se o segundo Id é uma variável do primeiro Id e a captura 
                                    varVariable = null;
                                    if (className.equals(curClass) && kraClass.getInstanceVariableList() != null) {
                                        Iterator itr = kraClass.getInstanceVariableList().elements();
                                        while(itr.hasNext()) {
                                            Variable v = (InstanceVariable)itr.next();                                                                  
                                            if (v.getName().equals(idList[1])) {
                                                varVariable = v;
                                                break;
                                            }
                                        }
                                    }                                                                                                          

                                    if (varVariable == null)
                                        signalError.showError("Identifier '" + idList[1] + "' was not found in the public interface of '" + className + "' or its superclasses");

                                    if ( lexer.token == Symbol.DOT ) {
                                    // Id "." Id "." Id "(" [ ExpressionList ] ")"
                                    /*
                                     * se o compilador permite vari�veis est�ticas, � poss�vel
                                     * ter esta op��o, como
                                     *     Clock.currentDay.setDay(12);
                                     * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
                                     * sinalize um erro neste ponto.
                                     */                                      

                                    if (!(varVariable.getType() instanceof KraClass)){
                                        signalError.showError("Message send to a non-object receiver");
                                    }

                                    lexer.nextToken();                                                                                                                                    
                                    if ( lexer.token != Symbol.IDENT )
                                        signalError.showError("Identifier expected");
                                    idList[2] = lexer.getStringValue();

                                    // Verifica se o terceiro Id eh um metodo do segundo Id
                                    kraClass = symbolTable.getInGlobal(varVariable.getType().getCname());
                                    className = kraClass.getName();

                                    methodVariable = null;
                                    do {
                                        for(MethodDec md : kraClass.getMethodList()){
                                               Variable v = md.getVariable();
                                               if(v.getName().compareTo(idList[2]) == 0){
                                                   if(v.getQualifier().compareTo("private") == 0){
                                                       signalError.showError("Method '"+ v.getName() +"' was not found in the public interface of '" + className +"' or its superclasses");
                                                   }                                                       
                                                   methodVariable = v;
                                                   break;
                                               }
                                           }
                                        kraClass = kraClass.getSuperclass();
                                    } while(kraClass != null && methodVariable == null);

                                    if (methodVariable == null) {
                                        signalError.showError("Method '" + idList[2] + "' was not found in the public interface of '" + className + "'");
                                    }

                                    lexer.nextToken();                                                                                                                                                                                
                                    exprList = this.realParameters();  
                                    if (!checkMethodParameters(exprList, methodVariable))
                                        signalError.showError("Wrong parameters for method '" + methodVariable.getName());
                                    return new MessageSendToVariable(idList[0], idList[1], idList[2], exprList, methodVariable.getType());
                                }                                        
                                else {
                                        // retorne o objeto da ASA que representa Id "." Id                                            
                                        return new MessageSendToVariable(idList[0], idList[1], varVariable.getType());                                                
                                }
                            }                                                           
			}			
		case THIS:
			/*
			 * Este 'case THIS:' trata os seguintes casos:
          	 * PrimaryExpr ::=
          	 *                 "this" |
          	 *                 "this" "." Id |
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  |
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */                        
                    
			lexer.nextToken();                        
                        kraClass = symbolTable.getInGlobal(curClass);                        
                        
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
                                /*Retorna apenas um this*/                                
                                return new MessageSendToSelf(kraClass);
			}
			else {
                            
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.showError("Identifier expected");
				idList[0] = lexer.getStringValue();
				lexer.nextToken();                                                                                                                                                                                                                                                  
                                				
				if ( lexer.token == Symbol.LEFTPAR ) {
                                    
                                    // "this" "." Id "(" [ ExpressionList ] ")"
                                    /*
                                     * Confira se a classe corrente possui um m�todo cujo nome �
                                     * 'ident' e que pode tomar os par�metros de ExpressionList
                                     */                                    
                                    
                                    // Verifica se o primeiro Id eh um metodo da classe o captura                                                                                                                                                                                                        
                                    methodVariable = null;
                                    do {
                                        for (MethodDec md : kraClass.getMethodList()) {
                                            Variable v = md.getVariable();
                                            if (v.getName().compareTo(idList[0]) == 0){                                                                                           
                                                methodVariable = v;                                                
                                                break;
                                            } 
                                        }                                        
                                        kraClass = kraClass.getSuperclass();
                                    } while (kraClass != null && methodVariable == null); 
                                    
                                    if (methodVariable == null)
                                        signalError.showError("Method '" + idList[0] + "' was not found in the public interface of '" + curClass + "' or its superclasses");
                                    exprList = this.realParameters();                                                                       
                                                                                                          
                                    if (!checkMethodParameters(exprList, methodVariable))
                                        signalError.showError("Type error: the type of the real parameter is not subclass of the type of the formal parameter");
                                    return new MessageSendToSelf(idList[0], exprList, methodVariable.getType());                                    
				}
                                else { 
                                    
                                    // Verifica se o primeiro Id é uma variável da classe e a captura
                                    varVariable = null;
                                    if (kraClass.getInstanceVariableList() != null) {
                                        Iterator itr = kraClass.getInstanceVariableList().elements();
                                        while(itr.hasNext()) {
                                            Variable v = (InstanceVariable)itr.next();                                                                  
                                            if (v.getName().equals(idList[0])) {
                                                varVariable = v;
                                                break;
                                            }
                                        }
                                    }  
                                    
                                    if (varVariable == null)
                                        signalError.showError("Variable '" + idList[0] + "' was not found in the public interface of '" + curClass + "'");                                   
                                    
                                    if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
                                        
                                        if (!(varVariable.getType() instanceof KraClass))
                                            signalError.showError("Message send to a non-object receiver");                                        
                                        
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
                                            signalError.showError("Identifier expected");
					lexer.nextToken();
                                                                                
                                        idList[1] = lexer.getStringValue();                                               
                                        // Verifica se o segundo Id eh um metodo do primeiro Id
                                        kraClass = symbolTable.getInGlobal(varVariable.getType().getCname());
                                        className = kraClass.getName();
                                        
                                        methodVariable = null;
                                        do {
                                            for(MethodDec md : kraClass.getMethodList()){
                                                   Variable v = md.getVariable();
                                                   if(v.getName().compareTo(idList[1]) == 0){
                                                       if(v.getQualifier().compareTo("private") == 0){
                                                           signalError.showError("Method '"+ v.getName() + "' was not found in the public interface of '" + className +"' or its superclasses");
                                                       }                                                       
                                                       methodVariable = v;                                                       
                                                       break;
                                                   }
                                               }
                                            kraClass = kraClass.getSuperclass();
                                        } while(kraClass != null && methodVariable == null);
                                        
                                        if (methodVariable == null) {
                                            signalError.showError("Method '" + idList[1] + "' was not found in the public interface of '" + className);
                                        }
                                        
					exprList = this.realParameters();
                              
                                        if (!checkMethodParameters(exprList, methodVariable))
                                            signalError.showError("Type error: the type of the real parameter is not subclass of the type of the formal parameter");
                                        return new MessageSendToSelf(idList[0], idList[1], exprList, methodVariable.getType());
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
                                       
					// retorne o objeto da ASA que representa Id "." Id                                        
                                        return new MessageSendToSelf(idList[0], varVariable.getType());                                                
				}
                            }
			}			
		default:
			signalError.showError("Expression expected");
		}
                
		return null;
	}

	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private ErrorSignaller	signalError;        
        private Stack whileStmt = new Stack();        
        private String curClass;
        private Variable curMethod;
}


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
                        
			classDec();
                        
			while ( lexer.token == Symbol.CLASS )
				classDec();
                        
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

	private void classDec() {
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
                
                
		symbolTable.putInGlobal(className, new KraClass(className));
                curClass.push(className);

		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);
			String superclassName = lexer.getStringValue();
                        
                        KraClass superClass = symbolTable.getInGlobal(superclassName);
                        if (superClass == null){
                            System.out.println("Erro");
                        }
                        else{
                            KraClass aClass = symbolTable.getInGlobal(className);
                            
                            if (aClass.getCname().compareTo(superclassName) != 0){
                                  aClass.setSuperclass(superClass);
                                  
                            }
                            else{
                                signalError.showError("Class '"+ aClass.getCname() +"' is inheriting from itself");
                            }
                        }

			lexer.nextToken();
		}
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("{ expected", true);
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
                        
                        if (name.compareTo("run") == 0){
                            isRun = true;
                        }
			lexer.nextToken();
                        
			if ( lexer.token == Symbol.LEFTPAR ){
                                
				methodDec(t, name, qualifier);
                                
                        }
			else if ( qualifier != Symbol.PRIVATE )
				signalError.showError("Attempt to declare a public instance variable");
			else
				instanceVarDec(t, name);
                        
		}

                if ((className.compareTo("Program") == 0) && isRun == false ){
                    signalError.showError("Method 'run' was not found in class 'Program");
                }
                
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("public/private or \"}\" expected");
		lexer.nextToken();
                
                curClass.pop();
	}

	private void instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
                String classe = (String) curClass.pop();
                KraClass KClass = symbolTable.getInGlobal(classe);
                
                curClass.push(classe);
                if (KClass.getInstanceVariableList() == null) {
                    
                    InstanceVariableList instance = new InstanceVariableList();
                    instance.addElement(new InstanceVariable(name, type));
                   
                    KClass.setInstanceVariableList(instance);
                   
                }
                else {
                    Iterator<InstanceVariable> itr;
                    InstanceVariableList instancias = KClass.getInstanceVariableList();
                  
                    itr = instancias.elements();
                    boolean adc = false;
                    while(itr.hasNext() && !adc){
                        Variable v = itr.next();
                      
                        if (v.getName().compareTo(name)== 0){
                            signalError.showError("Variable '" + name +"' is being redeclared");
                           
                        }
                        else{
                           InstanceVariable instanceVariable = new InstanceVariable(name, type);
                           instancias.addElement(instanceVariable);
                           adc = true;
                          // instancias.addElement();
                        }
                    }
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
                            Variable v = itr.next();
                            
                            if (v.getName().compareTo(variableName)==0){
                                System.out.println("Instancia de variavel ja declarada");
                            }
                            else{
                                instancias.addElement(new InstanceVariable(variableName, type));
                            }
                        }
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
                
	}

	private void methodDec(Type type, String name, Symbol qualifier) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
                
                String classe;
                boolean isProgramRun = false;
                if( (classe = (String) curClass.pop()).compareTo("Program") == 0 && name.compareTo("run") == 0){

                    if (qualifier == Symbol.PRIVATE){
                        signalError.showError("Method '" +  name + "' of class '" + classe +"' cannot be private");
                    }
                    if (type != Type.voidType){
                        signalError.showError("Method '" +  name + "' of class '" + classe + "' with a return value type different from 'void'");
                    }
                    isProgramRun = true;
                }

                /* Setar os metodos */
                KraClass cClass = symbolTable.getInGlobal(classe);
                
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
                        signalError.showError("Method '" +  name + "' of class '" + classe + "' cannot take parameters");
                    }
                }
                Variable var = new Variable(name, type, qualifier.toString(), params);
                
                ArrayList<Variable> methods = cClass.getMethodList();
                Iterator<Variable> itr;
                Iterator<Variable> parametros;
             
                /* Possui super classe. Verificar se o metodo será redefinido */
                if(superClasses != null) {
                    do {
                        for (Variable v : superClasses.getMethodList()){
                            
                            if(v.getName().compareTo(name) == 0){
                                /*comparar os parametro*/
                                if (params != null && v.getParam() != null){
                                    itr = v.getParam().elements();
                                    parametros = params.elements();
                                    while(itr.hasNext() && parametros.hasNext()) {
                                       Variable element = itr.next();
                                       Variable pElement = parametros.next();
                                      
                                       if(element.getType().getName() != pElement.getType().getName()){
                                           signalError.showError("Method '"+ name +"' is being redefined in subclass '" + classe +"' with a signature different from the method of superclass '"+ superClasses.getCname() +"'");
                                           break;
                                       }
                                    }
                                }
                                if (v.getType() != type){
                                    signalError.showError("Method '"+ name +"' of subclass '"+ classe +"' has a signature different from method inherited from superclass '" +superClasses.getCname() +"'");
                                }
                            }
                        }
                        superClasses =  superClasses.getSuperclass();
                    } while (superClasses != null);
                    
                }
               
                if(methods.size() == 0){
                    
                    methods.add(var); 
                    
                }
                else{
                    
                    for(Variable v : methods){
                        
                        if (v.getName().compareTo(var.getName()) != 0){
                           
                             methods.add(var);
                             break;
                        }
                        else{
                            signalError.showError("Method '" + name + "' is being redefined");
                            break;
                        }
                    }
                }   
                
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.showError("{ expected");

		lexer.nextToken();

                curClass.push(classe);
                
                curMethod.push(var);
                
                StatementList stmts = statementList(); 
    
                System.out.println(name);
                curMethod.pop();
    
               // Iterates over statements
                Boolean haveReturn = false;
                if (stmts != null) {
                    for (int i = 0; i < stmts.getList().size(); ++i) {
                        // Check if it's a 'return'
                        if (stmts.getList().get(i) != null){
                        
                            if ("StatementReturn".equals(stmts.getList().get(i).getClass().getSimpleName())) {
                                haveReturn = true;
                                StatementReturn returnStmt = (StatementReturn) stmts.getList().get(i);
                                if (returnStmt.getExpr() == null || 
                                    (returnStmt.getExpr() != null && !returnStmt.getExpr().getType().getName().equals(type.getName())))
                                    signalError.showError("Illegal 'return' statement. Method returns '" + type.getName() + "'");                        
                            }
                        }
                    }
                }
                
                // Check if 'return' is missing
                 if (!haveReturn && !"void".equals(type.getName()))
                    signalError.showError("Missing 'return' statement in method '" + name + "'"); 
                
                
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.showError("} expected");

		lexer.nextToken();
                
                symbolTable.removeLocalIdent();

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
                // TALVEZ ISSO BUGUE!!!!!!!
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
                        
			break;
		default:
			signalError.showError("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private CompositeStatement compositeStatement() {
		lexer.nextToken();
		StatementList stmtList = statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("} expected");
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
			readStatement();
			break;
		case WRITE:
			writeStatement();
			break;
		case WRITELN:
			writelnStatement();
			break;
		case IF:
			return ifStatement();			
		case BREAK:
			return breakStatement();			
		case WHILE:
			return whileStatement();			
		case SEMICOLON:
			nullStatement();
			break;
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
				(lexer.token == Symbol.IDENT) && isType(lexer.getStringValue()) ) {
                               
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
                                }
                                if (expr1.getType() instanceof TypeBoolean) {
                                    if (!(expr2.getType() instanceof TypeBoolean)) {
                                        signalError.showError("'"+ expr2.getType().getCname() + "' cannot be assigned to 'boolean'");
                                    }
                                }
                                System.out.println(expr2);
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.showError("';' expected", true);
				else
					lexer.nextToken();
			}
                        
                        return new AssignExpr(expr1, expr2);
		}	
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		return anExprList;
	}

	private Statement whileStatement() {
            whileStmt.push("true");
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		Expr e = expr();
                if (!(e.getType() instanceof TypeBoolean)){
                    signalError.showError("non-boolean expression in 'while' command");
                }
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		Statement stmt = statement();
            whileStmt.pop();
            
            return new StatementWhile(e, stmt);
	}

	private StatementIf ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		Expr e = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
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
                Variable var = (Variable) curMethod.pop();
                
                if (var.getType() instanceof TypeVoid){
                   signalError.showError("Illegal 'return' statement. Method returns 'void'");
                }
                
		lexer.nextToken();
		Expr e = expr();
                if (var.getType() instanceof KraClass) {
                   KraClass aClass = symbolTable.getInGlobal(var.getType().getName());
                   KraClass bClass = symbolTable.getInGlobal(e.getType().getName());
                   
                   if (aClass.getCname().compareTo(bClass.getCname()) != 0){
                       
                    if (aClass.getSuperclass() == null && bClass.getSuperclass() == null) {
                         signalError.showError("Type error: type of the expression returned is not subclass of the method return type");
                    }
                    else {
                          if (bClass.getSuperclass() != null && aClass.getSuperclass() == null) {
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
                curMethod.push(var);
                return new StatementReturn(e);
	}

	private void readStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String name = lexer.getStringValue();
                        Variable v = symbolTable.getInLocal(name);
                        if (v.getType() instanceof TypeBoolean){
                            signalError.showError("Command 'read' does not accept 'boolean' variables");
                        }
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

        //“write” “(” ExpressionList “)”
	private void writeStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
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
                
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void writelnStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
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

	private void nullStatement() {
		lexer.nextToken();
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
                                  if (aClass.getSuperclass() != null && bClass.getSuperclass() == null ){
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
		String messageName, id;
                String bClass;
                KraClass kraClass;
                
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
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
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

			String className = lexer.getStringValue();


                // encontre a classe className in symbol table KraClass
                        KraClass aClass = symbolTable.getInGlobal(className);

                        if ( aClass == null ) {
                            signalError.showError("Class '" + className + "' was not found");
                        }


			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("( expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError(") expected");
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
                    
                        boolean haveMethod = false;
                        bClass = (String) curClass.pop();
                        kraClass =  symbolTable.getInGlobal(bClass);
                        curClass.push(bClass);
                        
                        if (kraClass.getSuperclass()== null){
                            signalError.showError("'super' used in class '" + bClass + "' that does not have a superclass");
                        }
                       
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.showError("'.' expected");
			}
			else
				lexer.nextToken();
                                id = lexer.getStringValue();
                               
                                if (kraClass.getSuperclass() != null){
                                    kraClass =  symbolTable.getInGlobal(kraClass.getSuperclass().getCname());
                                    
                                    /* fazer while ate que nao tenha mais super classe */
                                    do{
                                        
                                        for(Variable v : kraClass.getMethodList()){
                                            
                                            if (v.getName().compareTo(id) == 0){
                                                if (v.getQualifier().compareTo("private") == 0) {
                                                    signalError.showError("Method '"+ id + "' was not found in the public interface of '" + kraClass.getCname() + "' or its superclasses");
                                                    break;
                                                }
                                                
                                                haveMethod = true;
                                            }
                                           
                                        }
                                        kraClass =  kraClass.getSuperclass();
                                       
                                    }while (kraClass != null);
                                 
                                    if (haveMethod == false){
                                        signalError.showError("Method '" + id + "' was not found in superclass '"+bClass+"' or its superclasses");
                                    }
                                }
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");
			messageName = lexer.getStringValue();
                        System.out.println(messageName);
                        
			/*
			 * para fazer as confer�ncias sem�nticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			exprList = realParameters();
                        
                        /*Guardar tipo da classe???*/
                       // return new PrimaryExpr(true);
			break;
		case IDENT:
			/*
          	 * PrimaryExpr ::=
          	 *                 Id  |
          	 *                 Id "." Id |
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
                        
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
                            // Id
                            // retorne um objeto da ASA que representa um identificador
                                Variable variable = symbolTable.getInLocal(firstId);
                                if(variable == null){
                                   signalError.showError("Identifier '" + firstId + "' was not found");
                                }
                                   
				return new PrimaryExpr(variable.getName(), variable.getType());
			}
			else { // Id "."
                                
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.showError("Identifier expected");
				}
				else {
					// Id "." Id
                                        
					lexer.nextToken();
					id = lexer.getStringValue();
                                        Variable variable = symbolTable.getInLocal(firstId);
                                        if (!(variable.getType() instanceof KraClass)){
                                            signalError.showError("Message send to a non-object receiver");
                                        }
					if ( lexer.token == Symbol.DOT ) {
						// Id "." Id "." Id "(" [ ExpressionList ] ")"
						/*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
						 */
                                                    lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.showError("Identifier expected");
						messageName = lexer.getStringValue();
						lexer.nextToken();
						exprList = this.realParameters();

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
                                            
						// Id "." Id "(" [ ExpressionList ] ")"
						exprList = this.realParameters();
						/*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
						 */
                                                Variable var = symbolTable.getInLocal(firstId);
                                                
                                                if (var != null){
                                                                                                     
                                                   KraClass kClass =  symbolTable.getInGlobal(var.getType().getCname());
                                                   boolean isPrivate = false;
                                                   
                                                   haveMethod = false;
                                                   for(Variable v : kClass.getMethodList()){
                                                       if(v.getName().compareTo(id) == 0){
                                                           if(v.getQualifier().compareTo("private") == 0){
                                                               signalError.showError("Method '"+ v.getName() +"' was not found in the public interface of '" + kClass.getName() +"' or its superclasses");
                                                           }
                                                           haveMethod = true;
                                  
                                                       }
                                                   }
                                                   
                                                   System.out.println("Em Factor verificar parametros do metodo, se ele existir. E tambem superclasse");
                                                   
                                                   
                                                   if (haveMethod == false){
                                                       if(kClass.getSuperclass() == null){
                                                        signalError.showError("Method '" + id  +"' was not found in class '" + var.getType().getCname() + "' or its superclasses" );
                                                       }
                                                       //procurar id nas superclasses
                                                       else{
                                                           KraClass superClasses = kClass.getSuperclass();
                                                           
                                                           //procurar em todas superclasses
                                                           do{
                                                               
                                                               for(Variable v : superClasses.getMethodList()){
                                                                   
                                                                if(v.getName().compareTo(id) == 0){
                                                                    haveMethod = true;
                                                                }
                                                            }
                                                             superClasses = superClasses.getSuperclass();
                                                           }while(superClasses != null);
                                                         
                                                           if(haveMethod == false){
                                                               signalError.showError("Method '" + id  +"' was not found in class '" + var.getType().getCname() + "' or its superclasses" );
                                                           }
                                                       }
                                                   }
                                                   
                                                }
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
                                                
                                                
					}
				}
			}
			break;
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
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
                                /*Retorna apenas um this*/
                                
                                return new PrimaryExpr(true);
			}
			else {
                            
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.showError("Identifier expected");
				id = lexer.getStringValue();
				lexer.nextToken();
				// j� analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
                                    // "this" "." Id "(" [ ExpressionList ] ")"
                                    /*
                                     * Confira se a classe corrente possui um m�todo cujo nome �
                                     * 'ident' e que pode tomar os par�metros de ExpressionList
                                     */
                                    Variable var = symbolTable.getInLocal(id);

                                    if (var == null){
                                       String cClass = (String) curClass.pop();
                                       KraClass kClass =  symbolTable.getInGlobal(cClass);
                                       haveMethod = false;
                                       for(Variable v : kClass.getMethodList()){
                                           if(v.getName().compareTo(id) == 0){
                                               haveMethod = true;
                                           }
                                       }
                                       
                                       curClass.push(cClass);
                                       if (haveMethod == false && kClass.getSuperclass() == null){
                                           signalError.showError("Method '" + id  +"' was not found in class '" + cClass + "' or its superclasses" );
                                       }
                                    }
					exprList = this.realParameters();
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.showError("Identifier expected");
					lexer.nextToken();
					exprList = this.realParameters();
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
                                       
					return new PrimaryExpr(true);
				}
			}
			break;
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
        private Stack curClass = new Stack();
        private Stack whileStmt = new Stack();
        private Stack curMethod = new Stack();
}

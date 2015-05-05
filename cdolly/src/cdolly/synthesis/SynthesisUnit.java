package cdolly.synthesis;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.c.ICNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.DeclarationGenerator;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTName;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.c.CBasicType;
import org.eclipse.cdt.internal.core.dom.parser.c.CNodeFactory;

import cdolly.model.inspector.AlloyInspector;
import cdolly.model.inspector.IModelVisitor;
import cdolly.model.inspector.IStringChecker;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;



/**
 * @author Jeanderson Candido<br>
 *         <a target="_blank"
 *         href="http://jeandersonbc.github.io">http://jeandersonbc
 *         .github.io</a>
 * 
 */
public class SynthesisUnit {

	private static Logger logger = Logger.getLogger(SynthesisUnit.class);

	/**
	 * Factory for C entities
	 */
	private static ICNodeFactory nodeFactory = CNodeFactory.getDefault();
	private static DeclarationGenerator generator = DeclarationGenerator.create(nodeFactory);

	private static IModelVisitor inspector;

	/**
	 * Extracts the C code specified by an Alloy model
	 * 
	 * @param alloyInstance
	 *            the alloy specification for the code to be extracted.
	 * @return A list of Translation Units ({@link IASTTranslationUnit}).
	 */
	public static List<IASTTranslationUnit> extractCode(A4Solution alloyInstance) {
		List<IASTTranslationUnit> sources = new ArrayList<IASTTranslationUnit>();

		if (alloyInstance == null) {
			logger.warn("Alloy instance is a null reference");
			return sources;
		}
		//inspector é um objeto da class AlloyInspector
		inspector = new AlloyInspector(alloyInstance);

		for (String translationUnit : inspector.getTranslationUnits()) {
			sources.add(synthetizeUnitFrom(translationUnit));
		}

		return sources;
	}

	private static IASTTranslationUnit synthetizeUnitFrom(String translationUnit) {
		IASTTranslationUnit source = nodeFactory.newTranslationUnit(null);
		List<String> declarations = inspector.getGlobalDeclarationsFrom(translationUnit);
		IStringChecker checker = inspector.getStringChecker();
		int contador = 0;
		
		
		for (String declaration : declarations) {
			if (checker.isFunctionDeclaration(declaration)) {
				source.addDeclaration(synthetizeFunctionFrom(declaration));
			} else if (checker.isVariableDeclaration(declaration)) {
				source.addDeclaration(synthetizeVariableFrom(declaration, true));
			}
			contador++;
		}
		return source;
	}
	
	private static IASTDeclaration synthetizeVariableFrom(String declaration, boolean isGlobal) {
		String name = inspector.getVariableNameFrom(declaration, isGlobal);
		
		IType type = extractVariableTypeFrom(declaration, isGlobal);
		
		IASTDeclarator declarator = generator.createDeclaratorFromType(type,
				name.toCharArray());
		IASTDeclSpecifier specifier = generator.createDeclSpecFromType(type);
		IASTSimpleDeclaration varDeclaration = nodeFactory.newSimpleDeclaration(specifier);

		IASTInitializer initializer = createLiteralFrom(type);
		declarator.setInitializer(initializer);
		varDeclaration.addDeclarator(declarator);

		return varDeclaration;
	}
	
	private static IASTDeclaration synthetizeFunctionFrom(String declaration) {
		
		IASTSimpleDeclSpecifier returnType = extractReturnTypeFrom(declaration);
		CASTFunctionDeclarator functionDecl = extractFunctionDeclaratorFrom(declaration);
		IASTCompoundStatement body = nodeFactory.newCompoundStatement();
	
		List<IASTDeclarationStatement> locals = extractLocalDeclarationsFrom(declaration);
		for (IASTDeclarationStatement decl : locals) {
			body.addStatement(decl);
		}

		//IASTIfStatement ifStatement = extractIfStatementFrom(declaration);
		//IASTIfStatement resultado =  extractIfStatementFrom(declaration);
		List<String> listExpressionStatement = inspector.getReturnIfStatementFrom(declaration);
		if (listExpressionStatement.size()>0) {
			body.addStatement(extractIfStatementFrom(declaration, returnType));
		}
			
		if (!isVoidReturn(returnType)) {
		IASTExpression returnExpr = extractReturnStmtFrom(declaration,returnType);
		body.addStatement(nodeFactory.newReturnStatement(returnExpr));
		}
		IASTFunctionDefinition function = nodeFactory.newFunctionDefinition(
				returnType, functionDecl, body);
		return function;
	}

	private static List<IASTDeclarationStatement> extractLocalDeclarationsFrom(	String declaration) {
		List<IASTDeclarationStatement> locals = new LinkedList<>();
		List<String> localDeclarations = inspector.getLocalDeclarationsFrom(declaration);
		for (String localDecl : localDeclarations) {
			String name = inspector.getVariableNameFrom(localDecl, false);
			IType type = extractVariableTypeFrom(localDecl, false);
			IASTDeclarator declarator = generator.createDeclaratorFromType(type, name.toCharArray());
			IASTDeclSpecifier specifier = generator.createDeclSpecFromType(type);
			IASTSimpleDeclaration varDeclaration = nodeFactory.newSimpleDeclaration(specifier);
			IASTInitializer initializer = createLiteralFrom(type);
			declarator.setInitializer(initializer);
			varDeclaration.addDeclarator(declarator);

			locals.add(nodeFactory.newDeclarationStatement(varDeclaration));
		}
		return locals;
	}

	private static IASTExpression extractFunctionCallExpression(String functionName) {
		
		IASTName nameFunction = nodeFactory.newName(functionName.toCharArray());
		IASTIdExpression arg0  = nodeFactory.newIdExpression( nameFunction);
		// buscar os parametros
		List<String> rawParams = inspector.getNameVariable(functionName);
		if (rawParams.size() >0 ) {
			String param = rawParams.get(0).replace("core/", "").replace("$", "Id_");//"LocallVariableId_0";
			IASTName paramName = nodeFactory.newName(param.toCharArray());
			IASTIdExpression expressionParam  = nodeFactory.newIdExpression( paramName);
			IASTInitializerClause [] arg1 ={expressionParam};
			//arg1[0] = expressionParam;
			IASTExpression functionCallExpression = nodeFactory.newFunctionCallExpression(arg0, arg1);
			return functionCallExpression;
		}
		else {
			IASTInitializerClause [] arg1 = null;
			IASTExpression functionCallExpression = nodeFactory.newFunctionCallExpression(arg0, arg1);
			return functionCallExpression;
		}
			
	}
	
	private static IASTExpression extractVariableExpression(String nameExpression) {
		String nameString = nameExpression;
		IASTName name = nodeFactory.newName(nameString.toCharArray());
		IASTIdExpression variableExpression  = nodeFactory.newIdExpression( name);
		return variableExpression;
		
	}
	
	private static IASTBinaryExpression extractAssignment(IASTExpression variableExpression1,String nameExpression) {
		
		Random randomGeneratorUnaryExpression = new Random();
		int position = randomGeneratorUnaryExpression.nextInt(7);
		String rawType = inspector.getypeVariableFrom(nameExpression);
		//String declaration  = extractVariableFrom(nameExpression);
		String[] intArray    = {"1"    , "0","1" ,"2","3" ,"100", "999999999", "886","-999999999"};
		String[] doubleArray = {"-10.4", "-1, 0" , "1.1"  , "3.0", "100.5", "9999999","-9878732.12"};
		String[] floatArray  = {"-1.0F", "0.0F"  , "1.0F" , "1.10F","10.54F","100.67F","99999999","99999.F"};
		String[] charArray   = {"'a'"  , "'b'"   ,"'#'"   ,"'Z'","'%'","'4'","'5'", "'&'" ,"'*'"};
		String nameString = null;
		if (rawType.contains("Float")) {
			nameString = floatArray[position];
		}
		else if (rawType.contains("Integer")) {
			nameString = intArray[position];
		}
		else if (rawType.contains("Doble")) {
			nameString = doubleArray[position];
		}
		else if (rawType.contains("Char")) {
			nameString = charArray[position];
		}
		
		IASTName nameVariable = nodeFactory.newName(nameString.toCharArray());
		IASTIdExpression variableExpression2  = nodeFactory.newIdExpression( nameVariable);
		
		IASTBinaryExpression ExpressionAssignment =  nodeFactory.newBinaryExpression(21, variableExpression1,variableExpression2);
		
		
		
		return ExpressionAssignment;
	}
	
	private static List<IASTExpression> extractExpressionIfStatement(String declaration) {
		
		List<IASTExpression> listExpression = new LinkedList<IASTExpression>() ;
		
		if (inspector.getReturnIfStatementFrom(declaration) != null ) {
			List<String> listExpressions = inspector.getReturnIfStatementFrom(declaration);
			for (String expression : listExpressions) {
				Random randomGeneratorUnaryExpression = new Random();
				int position = randomGeneratorUnaryExpression.nextInt(9);
				int[] intArray = {0,1,2,3,5,7,9,10,11};
				if (expression.contains("FunctionId") ) {
					
					listExpression.add(extractFunctionCallExpression(expression));
				}
				else {
					IASTUnaryExpression unaryExpression = nodeFactory.newUnaryExpression(intArray[position], extractVariableExpression(expression));
					listExpression.add(unaryExpression);
				}
				
			}
		}
		return listExpression;
	}
	@SuppressWarnings("deprecation")
	private static IASTIfStatement extractIfStatementFrom(String declaration ,IASTSimpleDeclSpecifier returnType) {
		
		List<IASTExpression> listExpression2 = extractExpressionIfStatement(declaration);
		
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(15);
		int[] intArray = {1,2,4,5,6,7,8,9,10,11,12,13,14,15,16};
		IASTBinaryExpression expressionIfStatement =  nodeFactory.newBinaryExpression(intArray[randomInt], listExpression2.get(0), listExpression2.get(1));
		List<String> listExpressions = inspector.getReturnIfStatementFrom(declaration);
		IASTCompoundStatement body = nodeFactory.newCompoundStatement();
		for (String nameExpresion : listExpressions) {
			if (!nameExpresion.contains("Function")) {
				String nameString = nameExpresion;
				IASTName nameVariable = nodeFactory.newName(nameString.toCharArray());
				IASTIdExpression variableExpression  = nodeFactory.newIdExpression( nameVariable);
				IASTExpressionStatement expressionStatement = nodeFactory.newExpressionStatement(extractAssignment(variableExpression,nameString));
				body.addStatement(expressionStatement);
			}
		}
		if (!isVoidReturn(returnType)) {
			IASTSimpleDeclSpecifier returnType2 = extractReturnTypeFrom(declaration);
			IASTExpression returnExpr = extractReturnStmtFrom(declaration,returnType2);
			body.addStatement(nodeFactory.newReturnStatement(returnExpr));
		}
		
		IASTIfStatement ifStatement = nodeFactory.newIfStatement(expressionIfStatement, body, null );
			
		
		return ifStatement;
	}
	private static IASTExpression extractReturnStmtFrom(String declaration, IASTSimpleDeclSpecifier returnType) {
		String rawReturnStmt = inspector.getReturnStmtFrom(declaration);
		String nameString = rawReturnStmt.replace("core/", "").replace("$", "_");
		IASTName name = nodeFactory.newName(nameString.toCharArray());
		IASTIdExpression condition  = nodeFactory.newIdExpression( name);
		
		return condition;
	}

	private static IASTExpression createReturnLiteralFor(IASTSimpleDeclSpecifier returnType) {
		IASTExpression returnExpr = null;
		if (returnType.getType() == IASTSimpleDeclSpecifier.t_int) {
			returnExpr = nodeFactory.newLiteralExpression(returnType.getType(),
					"1");
		} else if (returnType.getType() == IASTSimpleDeclSpecifier.t_char) {
			returnExpr = nodeFactory.newLiteralExpression(returnType.getType(),
					"'a'");
		} else if (returnType.getType() == IASTSimpleDeclSpecifier.t_double) {
			returnExpr = nodeFactory.newLiteralExpression(returnType.getType(),
					"3");

		} else if (returnType.getType() == IASTSimpleDeclSpecifier.t_float) {
			returnExpr = nodeFactory.newLiteralExpression(returnType.getType(),
					"2");
		}
		return returnExpr;
	}

	private static boolean isVoidReturn(IASTSimpleDeclSpecifier returnType) {
		return (returnType.getType() == IASTSimpleDeclSpecifier.t_void);
	}

	private static IASTSimpleDeclSpecifier extractReturnTypeFrom(String declaration) {
		int type = IASTSimpleDeclSpecifier.t_void;

		String rawType = inspector.getReturnTypeFrom(declaration);
		IStringChecker checker = inspector.getStringChecker();
		if (checker.isAnIntegerType(rawType)) {
			type = IASTSimpleDeclSpecifier.t_int;

		} else if (checker.isADoubleType(rawType)) {
			type = IASTSimpleDeclSpecifier.t_double;

		} else if (checker.isAFloatType(rawType)) {
			type = IASTSimpleDeclSpecifier.t_float;

		} else if (checker.isACharType(rawType)) {
			type = IASTSimpleDeclSpecifier.t_char;
		}
		return createTypeBasedOn(type);
	}
	
	private static String extractVariableFrom(String declaration) {
		String variable = null;
		if (declaration.contains("GlobalVarDecl")) {
			variable = inspector.getGlobalVarFrom(declaration);
		}
		else if (declaration.contains("LocalVarDecl")) {
			variable = inspector.getLocalVarFrom(declaration);
		}
		return variable;	
	}

	private static IType extractVariableTypeFrom(String declaration,boolean isGlobal) {

		// O método extractVariableFrom foi criado para que fosse possivel diferenciar uma variavel global de uma local 
		declaration = extractVariableFrom(declaration);
		
		String rawType = inspector.getDeclaringTypeFrom(declaration, isGlobal);
		IStringChecker checker = inspector.getStringChecker();
		if (checker.isAnIntegerType(rawType)) {
			return new CBasicType(Kind.eInt, 0);

		} else if (checker.isADoubleType(rawType)) {
			return new CBasicType(Kind.eDouble, 0);

		} else if (checker.isAFloatType(rawType)) {
			return new CBasicType(Kind.eFloat, 0);
		} else if (checker.isACharType(rawType)) {
			return new CBasicType(Kind.eChar, 0);
		}
		throw new UnknownRawTypeException(rawType);
	}

	private static IASTInitializer createLiteralFrom(IType type) {
		char[] value = null;
		int kind = 0;
		

		Random randomGeneratorUnaryExpression = new Random();
		int position = randomGeneratorUnaryExpression.nextInt(7);
		String[] intArray    = {"1"    , "0","1" ,"2","3" ,"100", "999999999", "886","-999999999"};
		String[] doubleArray = {"-10.4", "-1, 0" , "1.1"  , "3.0", "100.5", "9999999","-9878732.12"};
		String[] floatArray  = {"-1.0F", "0.0F"  , "1.0F" , "1.10F","10.54F","100.67F","99999999","99999.F"};
		String[] charArray   = {"'a'"  , "'b'"   ,"'#'"   ,"'Z'","'%'","'4'","'5'", "'&'" ,"'*'"};
		
		if (type != null) {
			if (type.isSameType(new CBasicType(Kind.eInt, 0))) {
				kind = IASTSimpleDeclSpecifier.t_int;
				
				value = intArray[position].toCharArray();

			} else if (type.isSameType(new CBasicType(Kind.eDouble, 0))) {
				kind = IASTSimpleDeclSpecifier.t_double;
				value = doubleArray[position].toCharArray();

			} else if (type.isSameType(new CBasicType(Kind.eFloat, 0))) {
				kind = IASTSimpleDeclSpecifier.t_float;
				value = floatArray[position].toCharArray();


			} else if (type.isSameType(new CBasicType(Kind.eChar, 0))) {
				kind = IASTSimpleDeclSpecifier.t_char;
				value = charArray[position].toCharArray();
			}
		}
		return nodeFactory.newEqualsInitializer(new CASTLiteralExpression(kind,
				value));
	}

	private static CASTFunctionDeclarator extractFunctionDeclaratorFrom(String declaration) {
		String rawFunctionName = inspector.getFunctionNameFrom(declaration);
		IASTName functionName = nodeFactory.newName(rawFunctionName.toCharArray());
		CASTFunctionDeclarator functionDecl = new CASTFunctionDeclarator(functionName);
		List<IASTParameterDeclaration> params = extractParamaters(declaration);
		for (IASTParameterDeclaration param : params) {
			functionDecl.addParameterDeclaration(param);
		}
		return functionDecl;
	}

	private static List<IASTParameterDeclaration> extractParamaters(String declaration) {
		List<IASTParameterDeclaration> params = new LinkedList<IASTParameterDeclaration>();
		
		List<String> rawParams = inspector.getNameVariable(declaration);
		IASTDeclSpecifier type = null;
		char[] realName = null;
		for (String rawParam : rawParams) {
			boolean condition = false; 
			if (rawParam.contains("Global")){
				condition = true;
			}
			realName = inspector.getVariableNameFrom(rawParam, condition).toCharArray();
			type = generator.createDeclSpecFromType(extractVariableTypeFrom(rawParam, condition));

			params.add(nodeFactory.newParameterDeclaration(type,new CASTDeclarator(new CASTName(realName))));

		}
		
		return params;
	}

	private static IASTSimpleDeclSpecifier createTypeBasedOn(int type) {
		IASTSimpleDeclSpecifier typeSpecifier = new CASTSimpleDeclSpecifier();
		typeSpecifier.setType(type);
		return typeSpecifier;
	}
}
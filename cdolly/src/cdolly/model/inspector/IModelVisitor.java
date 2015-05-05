package cdolly.model.inspector;

import java.util.List;

import cdolly.model.Entity;
import cdolly.model.Relation;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

/**
 * Represents an interface for retrieving information from an Alloy model.
 * 
 * @author Jeanderson Candido<br>
 *         <a target="_blank"
 *         href="http://jeandersonbc.github.io">http://jeandersonbc
 *         .github.io</a>
 * 
 */
public interface IModelVisitor {

	/**
	 * Returns a list containing the TranslationUnit names from the inspected
	 * model.<br>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty list is returned.
	 * 
	 * @return A list containing the TranslationUnit names from the inspected
	 *         model.
	 */
	List<String> getTranslationUnits();

	/**
	 * Returns a list containing all global declarations from the given
	 * TranslationUnit declaration.
	 * <p>
	 * A declaration is any entity in the set of {@link Relation#DECLARES} from
	 * the TranslationUnit.<br>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty list is returned.
	 * </p>
	 * 
	 * @param translationUnitName
	 *            The declaring name for a TranslationUnit
	 * @return A list containing all declarations from the given TranslationUnit
	 *         declaration.
	 */
	List<String> getGlobalDeclarationsFrom(String translationUnitName);

	/**
	 * Returns a list containing all local declarations from the given function
	 * declaration.
	 * <p>
	 * A declaration is any entity in the set of {@link Relation#DECLARES} from
	 * the function. <strong>Parameters are not considered </strong>in this
	 * implementation (use {@link IModelVisitor#getParametersFrom(String)}
	 * instead.<br>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty list is returned.
	 * </p>
	 * 
	 * @param functionDeclaration
	 *            The declaring name for a function
	 * @return A list containing all local declarations from the given function
	 *         declaration.
	 */
	List<String> getLocalDeclarationsFrom(String functionDeclaration);

	/**
	 * Returns the identifier associated with the given function declaration.
	 * <p>
	 * According to the model, {@link Entity#IDENTIFIER}s are unique, so the
	 * property guarantees unique function names.<br>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty string is returned.
	 * </p>
	 * 
	 * @param functionDeclaration
	 *            The function declaration.
	 * @return An string representing the function identifier associated with
	 *         the given declaration
	 */
	String getFunctionNameFrom(String functionDeclaration);

	/**
	 * Returns a list containing all parameters from the given function
	 * declaration.
	 * <p>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty list is returned.
	 * </p>
	 * 
	 * @param functionDeclaration
	 *            The declaring name for a function
	 * @return A list containing parameters from the given function declaration.
	 */
	List<String> getParametersFrom(String functionDeclaration);

	/**
	 * Returns the return type associated with the given function declaration.
	 * <p>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty string is returned.
	 * </p>
	 * 
	 * @param functionDeclaration
	 *            The function declaration.
	 * @return A string representing the return type associated with the given
	 *         declaration.
	 */
	String getReturnTypeFrom(String functionDeclaration);

	/**
	 * Returns the return statement associated with the given function
	 * declaration.
	 * <p>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty string is returned.
	 * </p>
	 * 
	 * @param functionDeclaration
	 *            The function declaration.
	 * @return A string representing the return statement associated with the
	 *         given declaration.
	 */
	String getReturnStmtFrom(String functionDeclaration);

	List<String> getReturnIfStatementFrom(String functionDeclaration);
	
	/**
	 * Returns the identifier associated with the given variable declaration.
	 * <p>
	 * According to the model, {@link Entity#IDENTIFIER}s are unique, so the
	 * property guarantees unique variable names.<br>
	 * A runtime exception may occur to indicate a unrecoverable problem in the
	 * alloy model. In other cases, an empty string is returned.
	 * </p>
	 * 
	 * @param variableDeclaration
	 *            The variable declaration.
	 * @param isGlobal
	 *            A flag indicating if this is a global variable.
	 * @return A string representing the function identifier associated with the
	 *         given declaration
	 */
	String getVariableNameFrom(String variableDeclaration, boolean isGlobal);

	String getDeclaringTypeFrom(String variableDeclaration, boolean isGlobal);

	IStringChecker getStringChecker();

	String getIfsFrom(String functionDeclaration);

	List<String>  getVariablesFrom(String declaration);

	String getDeclaringTypeFrom2(String variableDeclaration, boolean isGlobal);

	String getGlobalVarFrom(String GlobalVarDecl);

	String getLocalVarFrom(String LocalVarDecl);

	String getTeste();

	List<String> getNameVariable(String functionDeclaration);

	String getypeVariableFrom(String declaration);
}

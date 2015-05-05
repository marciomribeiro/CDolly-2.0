package cdolly.model.inspector;

public interface IStringChecker {

	boolean isVariableDeclaration(String rawDeclaration);

	boolean isFunctionDeclaration(String rawDeclaration);

	boolean isAnIntegerType(String rawType);

	boolean isAFloatType(String rawType);
	
	boolean isAPonteiroType(String rawType);

	boolean isADoubleType(String rawType);

	boolean isACharType(String rawType);

	boolean isALiteralReturn(String rawReturnStmt);
	
	boolean isAIfStatement(String rawIfStatement);

	boolean isIfDeclaration(String declaration);
}

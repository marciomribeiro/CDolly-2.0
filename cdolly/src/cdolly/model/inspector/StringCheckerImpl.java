package cdolly.model.inspector;

import cdolly.model.Entity;
import cdolly.model.Type;

public class StringCheckerImpl implements IStringChecker {

	private StringCheckerImpl() {
	}

	private static class InstanceHolder {
		static final IStringChecker INSTANCE = new StringCheckerImpl();
	}

	static IStringChecker getInstance() {
		return InstanceHolder.INSTANCE;
	}

	@Override
	public boolean isVariableDeclaration(String declaration) {
		return Entity.GLOBAL_VAR_DECL.isDeclaredBy(declaration);
	}

	@Override
	public boolean isFunctionDeclaration(String declaration) {
		return Entity.FUNCTION.isDeclaredBy(declaration);
	}
	
	@Override
	public boolean isAnIntegerType(String rawType) {
		return Type.INTEGER.isCompatibleBy(rawType);
	}

	@Override
	public boolean isAFloatType(String rawType) {
		return Type.FLOAT.isCompatibleBy(rawType);
	}
	
	@Override
	public boolean isADoubleType(String rawType) {
		return Type.DOUBLE.isCompatibleBy(rawType);
	}

	@Override
	public boolean isACharType(String rawType) {
		return Type.CHAR.isCompatibleBy(rawType);
	}

	@Override
	public boolean isALiteralReturn(String rawReturnStmt) {
		return Type.RETURN_LITERAL.isCompatibleBy(rawReturnStmt);
	}
	
	@Override
	public boolean isAIfStatement(String rawIfStatement) {
		return Entity.IF.isDeclaredBy(rawIfStatement);
	}

	@Override
	public boolean isAPonteiroType(String rawType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIfDeclaration(String declaration) {
		// TODO Auto-generated method stub
		return false;
	}


}

package cdolly.model;

/**
 * @author Jeanderson Candido<br>
 *         <a href="http://jeandersonbc.github.io"
 *         target="_blank">http://jeandersonbc.github.io</a>
 */
public enum Entity {

	TRANSLATION_UNIT("TranslationUnit"), FUNCTION("Function"), GLOBAL_VAR_DECL(
			"GlobalVarDecl"), LOCAL_VAR_DECL("LocalVarDecl"), GLOBAL_VAR("GlobalVar"),LOCAL_VAR("LocalVar"), DECLARATION(
			"Declaration"),VARIABLE("Variable"), IDENTIFIER("Identifier"), RETURN("Return"),IF("If"),
			EXPRESSION("Expression");

	private String label;

	Entity(String sigLabel) {
		this.label = sigLabel;
	}

	@Override
	public String toString() {
		return this.label;
	}

	/**
	 * 
	 * @return The label for this {@link Entity}.
	 */
	public String label() {
		return toString();
	}

	public boolean isDeclaredBy(String declaration) {
		return (declaration == null ? false : declaration.contains(this.label));
	}
}

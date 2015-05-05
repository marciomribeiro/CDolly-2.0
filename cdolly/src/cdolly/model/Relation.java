package cdolly.model;

public enum Relation {
	DECLARES("declares"), ID("id"), RETURN_TYPE("returnType"), PARAM("param"), 
		TYPE("type"), RETURN_STMT("returnStmt"),IF("If"), STMT("stmt"),
		G_V("gv"),V("v"), IDS("ids"), EXPRESSION("exp")
		;

	private String label;

	Relation(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public String label() {
		return toString();
	}
}

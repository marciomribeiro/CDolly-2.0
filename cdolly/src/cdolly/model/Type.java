package cdolly.model;

public enum Type {

	INTEGER("Integer"), FLOAT("Float"), DOUBLE("Double"), CHAR("Char"), RETURN_LITERAL(
			"ReturnLiteral");

	private String label;

	Type(String sigLabel) {
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

	public boolean isCompatibleBy(String rawType) {
		return (rawType == null ? false : rawType.contains(this.label));
	}
}
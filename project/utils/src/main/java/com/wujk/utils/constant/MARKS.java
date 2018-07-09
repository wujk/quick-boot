package com.wujk.utils.constant;

public enum MARKS {
	KEY1("!"), KEY2("@"), KEY3("#"), KEY4("$"), KEY5("%"), KEY6("^"), KEY7(
			"&"), KEY8("*"), KEY9("("), KEY0(")"), KEY10("{"), KEY11("}"), KEY12(
			"["), KEY13("]"), KEY14("`"), UNDERLINE("_"), MINUS("-"), PLUS("+"), EQUAL(
			"="), NEGATE("~"), COLON(":"), SEMICOLON(";"), QUOT("\""), APOS("'"), LT("<"), GT(">"), QUES("?"), SLASH("/"), VERT("|"), BACKSLASH("\\"), COMMA(","), POINT("\\.");

	private String value;

	private MARKS() {
	}

	private MARKS(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

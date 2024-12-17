/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class UnexpectedCharacter extends ParseException {
	private final int exp, got;

	public UnexpectedCharacter(int encounteredCharacter, int expectedCharacter) {
		got = encounteredCharacter;
		exp = expectedCharacter;
	}

	@Override
	public String getMessage() {
		return "Encountered unexpected character: found %s, expected %s".formatted((char)got, (char)exp);
	}
}

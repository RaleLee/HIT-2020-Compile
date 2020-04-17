package grammar;

public class Token {

    // species name
    private final String spName;
    // attribute value
    private final String attValue;
    // line index
    private final int lineIndex;

    public Token(String spName, String attValue, int lineIndex) {
        this.spName = spName;
        this.attValue = attValue;
        this.lineIndex = lineIndex;
    }

    public String getSpName() {
        return spName;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public String getAttValue() {
        return attValue;
    }

    @Override
    public String toString() {
        String att = attValue == null ? "-" : attValue;
        return "Line " + lineIndex + " :<" + spName + ", " + att + ">";
    }
}

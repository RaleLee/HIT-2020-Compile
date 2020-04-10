package grammar;

public class Token {

    // species name
    private String spName;
    // attribute value
    private String attValue;

    private int lineIndex;

    public Token(String spName, String attValue, int lineIndex){
        this.spName = spName;
        this.attValue = attValue;
        this.lineIndex = lineIndex;
    }

    public String getSpName() {
        return spName;
    }

    public String getAttValue() {
        return attValue;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    @Override
    public String toString(){
        String att = attValue == null ? "-" : attValue;
        return "Line "+ lineIndex + " :<"+spName + ", " + att+">";
    }
}

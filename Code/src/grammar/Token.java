package grammar;

public class Token {

    // species name
    private String spName;
    // attribute value
    private String attValue;

    public Token(String spName, String attValue){
        this.spName = spName;
        this.attValue = attValue;
    }

    public String getSpName() {
        return spName;
    }

    public String getAttValue() {
        return attValue;
    }
}

package grammar;

public class PToken {

    // Need print symbol
    public String token;
    // line number
    public int lineIndex;
    // attribute value
    public String attribute = null;

    public PToken(String token, int lineIndex){
        this.token = token;
        this.lineIndex = lineIndex;
    }

    public PToken(String token, int lineIndex, String attribute){
        this.token = token;
        this.lineIndex = lineIndex;
        this.attribute = attribute;
    }

    @Override
    public String toString(){
        if(attribute != null){
            return token+"("+lineIndex+")"+" :"+attribute;
        }
        return token+"("+lineIndex+")";
    }

}

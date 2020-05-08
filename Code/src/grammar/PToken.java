package grammar;

import java.util.ArrayList;
import java.util.List;

public class PToken {

  // Need print symbol
  public String token;
  // line number
  public int lineIndex;
  // attribute value
  public String lexeme = null;
  public String type = null;
  public String array = null;

  public void setArray(String array) {
    this.array = array;
  }

  public int width = 0;
  public List<Integer> trueList = new ArrayList<>();
  public List<Integer> falseList = new ArrayList<>();
  public List<Integer> nextList = new ArrayList<>();
  public List<String> typeList = new ArrayList<>();
  public int quad = 0;
  public int paramLen = 0;

  public void setLineIndex(int lineIndex) {
    this.lineIndex = lineIndex;
  }

  public void setLexeme(String lexeme) {
    this.lexeme = lexeme;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setTrueList(List<Integer> trueList) {
    this.trueList = trueList;
  }

  public void setFalseList(List<Integer> falseList) {
    this.falseList = falseList;
  }

  public void setNextList(List<Integer> nextList) {
    this.nextList = nextList;
  }

  public void setQuad(int quad) {
    this.quad = quad;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public String addr = null;

  public PToken(String token, int lineIndex) {
    this.token = token;
    this.lineIndex = lineIndex;
  }

  public PToken(String token, int lineIndex, String attribute) {
    this.token = token;
    this.lineIndex = lineIndex;
    this.lexeme = attribute;
  }

  @Override
  public String toString() {
    if (lexeme != null) {
      return token + "(" + lineIndex + ")" + " :" + lexeme;
    }
    return token + "(" + lineIndex + ")";
  }

}

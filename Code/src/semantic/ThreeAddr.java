package semantic;

public class ThreeAddr {

  static String[] types = {"+", "-", "*", "/", "=", "relop", "goto", "param", "call", "return",
      "=[]", "[]=", "&", "=*", "*="};
  private final String content;
  private final String type;
  private final String[] elements;

  public ThreeAddr(String content, String type, String[] elements) {
    this.content = content;
    this.type = type;
    this.elements = elements;
  }

  @Override
  public String toString() {
    return content + '\n' + '(' +
        type + ',' + elements[0] + ',' + elements[1] + ',' + elements[2]
        + ')';
  }

}

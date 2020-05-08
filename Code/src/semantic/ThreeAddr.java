package semantic;

public class ThreeAddr {

  static String[] types = {"+", "-", "*", "/", "=", "relop", "goto", "param", "call", "return",
      "=[]", "[]=", "&", "=*", "*="};
  private String content;
  private final String type;
  private final String[] elements;

  public ThreeAddr(String content, String type, String[] elements) {
    this.content = content;
    this.type = type;
    this.elements = elements;
  }

  public void backPatch(Integer quad) {
    this.content = this.content.replace("_", quad.toString());
    this.elements[2] = quad.toString();
  }

  @Override
  public String toString() {
    return content + '\n' + '(' +
        type + ',' + elements[0] + ',' + elements[1] + ',' + elements[2]
        + ')';
  }

}

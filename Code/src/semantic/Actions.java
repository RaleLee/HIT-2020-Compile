package semantic;

import java.util.List;

public class Actions {

  private static void action1(List<String> nowNodes, SemanticAnalyzer semanticAnalyzer) {

  }

  private static void action2(List<String> nowNodes, SemanticAnalyzer semanticAnalyzer) {

  }

  private static void action3(List<String> nowNodes, SemanticAnalyzer semanticAnalyzer) {

  }

  //按照父亲——从左兄弟到自己的顺序存放的，目前action需要的节点的编号
  public static void actionEntry(Integer actionNumber, List<String> nowNodes,
      SemanticAnalyzer semanticAnalyzer) {
    switch (actionNumber) {
      case 1:
        action1(nowNodes, semanticAnalyzer);
        break;
      case 2:
        break;
      case 3:
        break;
      default:
        System.out.println("ERROR ACTION!");
        System.exit(0);
    }
  }
}

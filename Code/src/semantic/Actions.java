package semantic;

import grammar.PToken;
import java.util.List;

public class Actions {

  private static void newTempAndGen(PToken t1, PToken t2,
      PToken t3, SemanticAnalyzer semanticAnalyzer, String type) {
    t1.setAddr("t" + semanticAnalyzer.nowTempLabel);
    semanticAnalyzer.setNowTempLabel(semanticAnalyzer.nowTempLabel + 1);
    String[] elements = new String[3];
    elements[0] = t2.addr;
    elements[1] = t3.addr;
    elements[2] = t1.addr;
    if (type.equals("-")) {
      semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + "=-" + t2.addr + type + t3.addr, type,
          elements));
    } else if (type.equals("/")) {
      semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + "=1/" + t2.addr + type + t3.addr, type,
          elements));
    } else {
      semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + '=' + t2.addr + type + t3.addr, type,
          elements));
    }
  }

  private static void action1(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken T = nowNodes.get(0);
    PToken C = nowNodes.get(2);
    T.setType(C.type);
    T.setWidth(C.width);
  }

  private static void action2(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken D = nowNodes.get(0);
    PToken T = nowNodes.get(1);
    PToken Identifier = nowNodes.get(2);
    try {
      semanticAnalyzer.enter(Identifier.lexeme, T.type);
    } catch (Exception e) {
      System.out.println("Error at Line " + D.lineIndex + ":Repeat Define!");
      System.exit(-1);
    }
    semanticAnalyzer.setOffset(semanticAnalyzer.offset + T.width);
  }

  private static void action7(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken C = nowNodes.get(0);
    C.setType(semanticAnalyzer.tType);
    C.setWidth(semanticAnalyzer.wWidth);
  }

  private static void action8(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(0);
    X.setType("int");
    X.setWidth(4);
  }

  private static void action22(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken NOT = nowNodes.get(1);
    PToken B = nowNodes.get(2);
    Q.setTrueList(B.falseList);
    Q.setFalseList(B.trueList);
  }

  private static void action28(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(0);
    PToken Y = nowNodes.get(1);
    PToken Ep = nowNodes.get(2);
    newTempAndGen(E, Y, Ep, semanticAnalyzer, "+");

  }

  private static void action30(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    PToken Y = nowNodes.get(2);
    PToken EpR = nowNodes.get(3);
    newTempAndGen(Ep, Y, EpR, semanticAnalyzer, "+");
  }

  private static void action31(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Y = nowNodes.get(0);
    PToken Z = nowNodes.get(1);
    PToken Yp = nowNodes.get(2);
    newTempAndGen(Y, Z, Yp, semanticAnalyzer, "*");
  }

  private static void action32(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    PToken Z = nowNodes.get(2);
    PToken YpR = nowNodes.get(3);
    newTempAndGen(Yp, Z, YpR, semanticAnalyzer, "*");
  }

  private static void action35(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Z = nowNodes.get(0);
    PToken NUM = nowNodes.get(1);
    Z.setAddr(NUM.lexeme);
  }

  private static void action38(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    PToken Y = nowNodes.get(2);
    PToken EpR = nowNodes.get(3);
    newTempAndGen(Ep, Y, EpR, semanticAnalyzer, "-");
  }

  private static void action39(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    PToken Z = nowNodes.get(2);
    PToken YpR = nowNodes.get(3);
    newTempAndGen(Yp, Z, YpR, semanticAnalyzer, "/");
  }

  private static void action40(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(1);
    semanticAnalyzer.settType(X.type);
    semanticAnalyzer.setwWidth(X.width);
  }

  private static void action41(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    Ep.setAddr("0");
  }

  private static void action42(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    Yp.setAddr("1");
  }

  //按照父亲——从左兄弟到自己的顺序存放的，目前action需要的节点的编号
  public static void actionEntry(Integer actionNumber, List<PToken> nowNodes,
      SemanticAnalyzer semanticAnalyzer) {
    switch (actionNumber) {
      case 1:
        action1(nowNodes, semanticAnalyzer);
        break;
      case 2:
        action2(nowNodes, semanticAnalyzer);
        break;
      case 7:
        action7(nowNodes, semanticAnalyzer);
        break;
      case 8:
        action8(nowNodes, semanticAnalyzer);
        break;
      case 28:
        action28(nowNodes, semanticAnalyzer);
        break;
      case 30:
        action30(nowNodes, semanticAnalyzer);
        break;
      case 31:
        action31(nowNodes, semanticAnalyzer);
        break;
      case 32:
        action32(nowNodes, semanticAnalyzer);
        break;
      case 35:
        action35(nowNodes, semanticAnalyzer);
        break;
      case 38:
        action38(nowNodes, semanticAnalyzer);
        break;
      case 39:
        action39(nowNodes, semanticAnalyzer);
        break;
      case 40:
        action40(nowNodes, semanticAnalyzer);
        break;
      case 41:
        action41(nowNodes, semanticAnalyzer);
        break;
      case 42:
        action42(nowNodes, semanticAnalyzer);
        break;
      default:
        System.out.println("ERROR ACTION!");
        System.exit(0);
    }
  }
}

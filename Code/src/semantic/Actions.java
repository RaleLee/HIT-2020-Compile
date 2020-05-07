package semantic;

import grammar.PToken;
import java.util.List;
import javafx.util.Pair;

public class Actions {

  private static void newTempAndGenOpeartorUseAddr(PToken t1, PToken t2,
      PToken t3, SemanticAnalyzer semanticAnalyzer, String type) {
    t1.setAddr(semanticAnalyzer.newTemp());
    if (t2.type.equals(t3.type)) {
      semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + "=" + t2.addr + type + t3.addr, type,
          new String[]{t2.addr, t3.addr, t1.addr}));
      t1.setType(t2.type);
    } else if (t2.type.equals("float") || t2.type.equals("double")) {
      if (t3.type.equals("int")) {
        String temp = semanticAnalyzer.newTemp();
        semanticAnalyzer.answers.add(new ThreeAddr(temp + "=" + "intToReal" + t2.addr, "=",
            new String[]{"intToReal", t2.addr, temp}));
        semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + "=" + temp + type + t3.addr, type,
            new String[]{temp, t3.addr, t1.addr}));
        t1.setType(t2.type);
      }
    } else if (t3.type.equals("float") || t3.type.equals("double")) {
      if (t2.type.equals("int")) {
        String temp = semanticAnalyzer.newTemp();
        semanticAnalyzer.answers.add(new ThreeAddr(temp + "=" + "intToReal" + t3.addr, "=",
            new String[]{"intToReal", t3.addr, temp}));
        semanticAnalyzer.answers.add(new ThreeAddr(t1.addr + "=" + t2.addr + type + temp, type,
            new String[]{t2.addr, temp, t1.addr}));
        t1.setType(t3.type);
      }
    } else {
      System.out
          .println("Error at Line " + t1.lineIndex + ":" + "Operation Type Mismatch!");
      semanticAnalyzer
          .setWrongEnd("Error at Line " + t1.lineIndex + ":" + "Operation Type Mismatch!");
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
      System.out.println("Error at Line " + D.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + D.lineIndex + ":" + e.getMessage());
      return;
    }
    semanticAnalyzer.setOffset(semanticAnalyzer.offset + T.width);
  }

  private static void action3(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken D = nowNodes.get(0);
    PToken proc = nowNodes.get(1);
    PToken X = nowNodes.get(2);
    PToken Identifier = nowNodes.get(3);
    try {
      semanticAnalyzer.enter(Identifier.lexeme, Identifier.type);
    } catch (Exception e) {
      // TODO: fill the exception condition ???
      System.out.println("Error at Line " + D.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + D.lineIndex + ":" + e.getMessage());
      return;
    }
  }

  private static void action4(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken D = nowNodes.get(0);
    PToken record = nowNodes.get(1);
    PToken Identifier = nowNodes.get(2);
    try {
      semanticAnalyzer.enter(Identifier.lexeme, Identifier.type);
    } catch (Exception e) {
      // TODO: fill the exception condition ???
      System.out.println("Error at Line " + D.lineIndex + ":" + e.getMessage());
    }
  }


  private static void action6(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken C = nowNodes.get(0);
    PToken Num = nowNodes.get(2);
    PToken C1 = nowNodes.get(4);
    C.setType("array(" + Num.lexeme + ',' + C1.type + ")");
    C.setWidth(Integer.parseInt(Num.lexeme) * C1.width);
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

  private static void action9(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(0);
    X.setType("float");
    X.setWidth(4);
  }

  private static void action10(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(0);
    X.setType("char");
    X.setWidth(1);
  }

  private static void action11(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(0);
    X.setType("double");
    X.setWidth(8);
  }

  // TODO: Action12~19
  private static void action14(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Identifier = nowNodes.get(1);
    PToken E = nowNodes.get(3);
    try {
      semanticAnalyzer.lookUp(Identifier.lexeme);
      semanticAnalyzer.answers.add(new ThreeAddr(Identifier.lexeme + "=" + E.addr, "=",
          new String[]{Identifier.lexeme, " ", E.addr}));
    } catch (Exception e) {
      System.out.println("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
    }
  }

  private static void action20(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken U = nowNodes.get(0);
    U.setQuad(semanticAnalyzer.nextQuad);
  }

  private static void action22(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken NOT = nowNodes.get(1);
    PToken B = nowNodes.get(2);
    Q.setTrueList(B.falseList);
    Q.setFalseList(B.trueList);
  }

  private static void action23(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken B = nowNodes.get(2);
    Q.setTrueList(B.trueList);
    Q.setFalseList(B.falseList);
  }

  private static void action28(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(0);
    PToken Y = nowNodes.get(1);
    PToken Ep = nowNodes.get(2);
    newTempAndGenOpeartorUseAddr(E, Y, Ep, semanticAnalyzer, "+");
  }

  private static void action30(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    PToken Y = nowNodes.get(2);
    PToken EpR = nowNodes.get(3);
    newTempAndGenOpeartorUseAddr(Ep, Y, EpR, semanticAnalyzer, "+");
  }

  private static void action31(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Y = nowNodes.get(0);
    PToken Z = nowNodes.get(1);
    PToken Yp = nowNodes.get(2);
    newTempAndGenOpeartorUseAddr(Y, Z, Yp, semanticAnalyzer, "*");
  }

  private static void action32(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    PToken Z = nowNodes.get(2);
    PToken YpR = nowNodes.get(3);
    newTempAndGenOpeartorUseAddr(Yp, Z, YpR, semanticAnalyzer, "*");
  }

  private static void action33(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Z = nowNodes.get(0);
    PToken E = nowNodes.get(2);
    Z.setAddr(E.addr);
    Z.setType(E.type);
  }

  private static void action34(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Z = nowNodes.get(0);
    PToken Identifier = nowNodes.get(1);
    try {
      Pair<String, Integer> tempPair = semanticAnalyzer.lookUp(Identifier.lexeme);
      Z.setAddr(Identifier.lexeme);
      Z.setType(tempPair.getKey());
    } catch (Exception e) {
      System.out.println("Error at Line " + Z.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Z.lineIndex + ":" + e.getMessage());
    }
  }

  private static void action35(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Z = nowNodes.get(0);
    PToken NUM = nowNodes.get(1);
    Z.setAddr(NUM.lexeme);
    Z.setType("int");
  }

  private static void action36(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Z = nowNodes.get(0);
    PToken Float = nowNodes.get(1);
    Z.setAddr(Float.lexeme);
    Z.setType("float");
  }

  private static void action38(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    PToken Y = nowNodes.get(2);
    PToken EpR = nowNodes.get(3);
    newTempAndGenOpeartorUseAddr(Ep, EpR, Y, semanticAnalyzer, "-");
  }

  private static void action39(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    PToken Z = nowNodes.get(2);
    PToken YpR = nowNodes.get(3);
    newTempAndGenOpeartorUseAddr(Yp, YpR, Z, semanticAnalyzer, "/");
  }

  private static void action40(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken X = nowNodes.get(1);
    semanticAnalyzer.settType(X.type);
    semanticAnalyzer.setwWidth(X.width);
  }

  private static void action41(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Ep = nowNodes.get(0);
    Ep.setAddr("0");
    Ep.setType("int");
  }

  private static void action42(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Yp = nowNodes.get(0);
    Yp.setAddr("1");
    Yp.setType("int");
  }

  private static void action43(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken L = nowNodes.get(0);
    PToken Identifer = nowNodes.get(1);
    PToken Lp = nowNodes.get(2);
    try {
      Pair<String, Integer> tempPair = semanticAnalyzer.lookUp(Identifer.lexeme);
      String tempLeft = Identifer.lexeme + "[" + Lp.addr + "]";
      if (!tempPair.getKey().contains("array")) {
        tempLeft = Identifer.lexeme;
      }
      L.setAddr(tempLeft);
    } catch (Exception e) {
      System.out.println("Error at Line " + Identifer.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Identifer.lineIndex + ":" + e.getMessage());
    }
  }

  private static void action44(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Lpl = nowNodes.get(0);
    PToken E = nowNodes.get(2);
    PToken Lpr = nowNodes.get(4);
    if (!Lpr.type.equals("int")) {
      System.out.println("Error at Line " + Lpl.lineIndex + ":"
          + "Using Array Access Operators For Non Array Variable");
      semanticAnalyzer.setWrongEnd("Error at Line " + Lpl.lineIndex + ":"
          + "Using Array Access Operators For Non Array Variable");
      return;
    }
    if (!E.type.equals("int")) {
      System.out.println("Error at Line " + E.lineIndex + ":" + "Non Integer Index!");
      semanticAnalyzer.setWrongEnd("Error at Line " + E.lineIndex + ":" + "Non Integer Index!");
    } else {
      newTempAndGenOpeartorUseAddr(Lpl, E, Lpr, semanticAnalyzer, "*");
    }
  }

  private static void action45(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken L = nowNodes.get(0);
    PToken Identifer = nowNodes.get(1);
    try {
      Pair<String, Integer> tempPair = semanticAnalyzer.lookUp(Identifer.lexeme);
      L.setType(tempPair.getKey());
      L.setWidth(tempPair.getValue());
      semanticAnalyzer.arrayWidth.clear();
      String tempType = tempPair.getKey();
      semanticAnalyzer.arrayElementWidth =
          util.getTypeSize(tempType);
      semanticAnalyzer.arrayWidth.clear();
      if (tempType.contains("array")) {
        semanticAnalyzer.arrayWidth = util.getNumbers(tempType);
      }
    } catch (Exception e) {
      System.out.println("Error at Line " + Identifer.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Identifer.lineIndex + ":" + e.getMessage());
      return;
    }
  }

  private static void action46(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Lp = nowNodes.get(0);
    Lp.setAddr(semanticAnalyzer.newTemp());
    Lp.width = semanticAnalyzer.arrayElementWidth;
    Lp.setType("int");
    if (semanticAnalyzer.arrayWidth.isEmpty()) {
      Lp.setType("ERROR");//用于检查对非数组变量的数组下标访问
    }
    semanticAnalyzer.answers
        .add(new ThreeAddr(Lp.addr + "=" + Lp.width, "=",
            new String[]{String.valueOf(Lp.width), " ", Lp.addr}));
  }

  private static void action47(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken L = nowNodes.get(1);
    PToken E = nowNodes.get(3);
    semanticAnalyzer.answers.add(new ThreeAddr(L.addr + "=" + E.addr, "=",
        new String[]{String.valueOf(E.addr), " ", L.addr}));
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
      case 6:
        action6(nowNodes, semanticAnalyzer);
        break;
      case 7:
        action7(nowNodes, semanticAnalyzer);
        break;
      case 8:
        action8(nowNodes, semanticAnalyzer);
        break;
      case 14:
        action14(nowNodes, semanticAnalyzer);
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
      case 34:
        action34(nowNodes, semanticAnalyzer);
        break;
      case 35:
        action35(nowNodes, semanticAnalyzer);
        break;
      case 36:
        action36(nowNodes, semanticAnalyzer);
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
      case 43:
        action43(nowNodes, semanticAnalyzer);
        break;
      case 44:
        action44(nowNodes, semanticAnalyzer);
        break;
      case 45:
        action45(nowNodes, semanticAnalyzer);
        break;
      case 46:
        action46(nowNodes, semanticAnalyzer);
        break;
      case 47:
        action47(nowNodes, semanticAnalyzer);
        break;
      default:
        System.out.println("ERROR ACTION!");
        System.exit(0);
    }
  }
}

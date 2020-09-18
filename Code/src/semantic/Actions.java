package semantic;

import grammar.PToken;
import java.util.List;
import javafx.util.Pair;

public class Actions {

  private static void newTempAndGenOpeartorUseAddr(PToken t1, PToken t2,
      PToken t3, SemanticAnalyzer semanticAnalyzer, String type) {
    t1.setAddr(semanticAnalyzer.newTemp());
    String tempT2Type = t2.type;
    String tempT3Type = t3.type;
    if (t2.type.contains("array")) {
      t2.setType(util.getTypeInArrayType(t2.type));
    }
    if (t3.type.contains("array")) {
      t3.setType(util.getTypeInArrayType(t3.type));
    }
    if (t2.type.equals(t3.type)) {
      semanticAnalyzer.addAnswers(new ThreeAddr(t1.addr + "=" + t2.addr + type + t3.addr, type,
          new String[]{t2.addr, t3.addr, t1.addr}));
      t1.setType(t2.type);
    } else if (t2.type.equals("float") || t2.type.equals("double")) {
      if (t3.type.equals("int")) {
        String temp = semanticAnalyzer.newTemp();
        semanticAnalyzer.addAnswers(new ThreeAddr(temp + "=" + "intToReal" + t3.addr, "=",
            new String[]{"intToReal", t3.addr, temp}));
        semanticAnalyzer.addAnswers(new ThreeAddr(t1.addr + "=" + t2.addr + type + temp, type,
            new String[]{t2.addr, temp, t1.addr}));
        t1.setType(t2.type);
      }
    } else if (t3.type.equals("float") || t3.type.equals("double")) {
      if (t2.type.equals("int")) {
        String temp = semanticAnalyzer.newTemp();
        semanticAnalyzer.addAnswers(new ThreeAddr(temp + "=" + "intToReal" + t2.addr, "=",
            new String[]{"intToReal", t2.addr, temp}));
        semanticAnalyzer.addAnswers(new ThreeAddr(t1.addr + "=" + temp + type + t3.addr, type,
            new String[]{temp, t3.addr, t1.addr}));
        t1.setType(t3.type);

      }
    } else {
      System.out
          .println("Error at Line " + t1.lineIndex + ":" + "Operation Type Mismatch!");
      semanticAnalyzer
          .setWrongEnd("Error at Line " + t1.lineIndex + ":" + "Operation Type Mismatch!");
    }
    t2.setType(tempT2Type);
    t3.setType(tempT3Type);
  }

  private static void genGoto(String next, SemanticAnalyzer semanticAnalyzer) {
    semanticAnalyzer
        .addAnswers(new ThreeAddr("goto " + next, "goto", new String[]{" ", " ", next}));
  }

  private static void genIfGoto(String next, PToken E1, PToken relop, PToken E2,
      SemanticAnalyzer semanticAnalyzer) {
    semanticAnalyzer.addAnswers(
        new ThreeAddr("if " + E1.addr + ' ' + relop.addr + ' ' + E2.addr + " goto " + next, "relop",
            new String[]{E1.addr, E2.addr, next}));

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
      Identifier.setType("proc");
      semanticAnalyzer.enter(Identifier.lexeme, Identifier.type);
      semanticAnalyzer.retType = X.type;
    } catch (Exception e) {
      System.out.println("Error at Line " + D.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + D.lineIndex + ":" + e.getMessage());
    }
  }

  private static void action4(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken D = nowNodes.get(0);
    PToken record = nowNodes.get(1);
    PToken Identifier = nowNodes.get(2);
    Identifier.setType("record");
    try {
      semanticAnalyzer.enter(Identifier.lexeme, Identifier.type);
    } catch (Exception e) {
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

  private static void action12(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken S1 = nowNodes.get(0);
    PToken B = nowNodes.get(2);
    PToken U1 = nowNodes.get(4);
    PToken S2 = nowNodes.get(5);
    PToken W1 = nowNodes.get(6);
    PToken U2 = nowNodes.get(8);
    PToken S3 = nowNodes.get(9);
    semanticAnalyzer.backPatch(U1.quad, B.trueList);
    semanticAnalyzer.backPatch(U2.quad, B.falseList);
    semanticAnalyzer.backPatch(semanticAnalyzer.nextQuad, W1.nextList);
    S1.setNextList(
        SemanticAnalyzer
            .mergeList(SemanticAnalyzer.mergeList(S2.nextList, W1.nextList), S3.nextList));
  }

  private static void action14(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Identifier = nowNodes.get(1);
    PToken E = nowNodes.get(3);
    try {
      semanticAnalyzer.lookUp(Identifier.lexeme);
      semanticAnalyzer.addAnswers(new ThreeAddr(Identifier.lexeme + "=" + E.addr, "=",
          new String[]{Identifier.lexeme, " ", E.addr}));
    } catch (Exception e) {
      System.out.println("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
    }
  }

  private static void action15(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken S = nowNodes.get(0);
    PToken Identifier = nowNodes.get(2);
    PToken Elist = nowNodes.get(4);
    try {
      Pair<String, Integer> tempPair = semanticAnalyzer.lookUp(Identifier.lexeme);
      if (!tempPair.getKey().equals("proc")) {
        System.out.println("Error at Line " + Identifier.lineIndex + ":" + "Call Not A Function!");
        semanticAnalyzer
            .setWrongEnd("Error at Line " + Identifier.lineIndex + ":" + "Call Not A Function!");
      } else {
        List<String> types = semanticAnalyzer.functionList.get(Identifier.lexeme).getKey();
        Integer paramLen = semanticAnalyzer.functionList.get(Identifier.lexeme).getValue();
        if (paramLen != semanticAnalyzer.paramQueue.size()) {
          System.out
              .println("Error at Line " + Identifier.lineIndex + ":" + "Call Param Len Error!");
          semanticAnalyzer
              .setWrongEnd("Error at Line " + Identifier.lineIndex + ":" + "Call Param Len Error!");
          return;
        }
        for (int ind = 0; ind < semanticAnalyzer.paramQueue.size(); ind++) {
          Pair<String, String> param = semanticAnalyzer.paramQueue.get(ind);
          semanticAnalyzer.addAnswers(new ThreeAddr("param " + param.getKey(), "param",
              new String[]{" ", " ", param.getKey()}));
          if (!param.getValue().equals(types.get(ind))) {
            System.out
                .println("Error at Line " + Identifier.lineIndex + ":" + "Call Param Type Error!");
            semanticAnalyzer
                .setWrongEnd(
                    "Error at Line " + Identifier.lineIndex + ":" + "Call Param Type Error!");
            return;
          }
          semanticAnalyzer.addAnswers(
              new ThreeAddr("call " + Identifier.lexeme + "," + semanticAnalyzer.paramQueue.size(),
                  "call",
                  new String[]{Identifier.lexeme,
                      String.valueOf(semanticAnalyzer.paramQueue.size()), " "}));
        }
      }
    } catch (Exception e) {
      System.out.println("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
      semanticAnalyzer.setWrongEnd("Error at Line " + Identifier.lineIndex + ":" + e.getMessage());
    }

  }

  private static void action16(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken S = nowNodes.get(0);
    PToken U1 = nowNodes.get(2);
    PToken B = nowNodes.get(3);
    PToken U2 = nowNodes.get(4);
    PToken S1 = nowNodes.get(5);
    S.setNextList(B.falseList);
    semanticAnalyzer.backPatch(U1.quad, S1.nextList);
    semanticAnalyzer.backPatch(U2.quad, B.trueList);
    genGoto(String.valueOf(U1.quad), semanticAnalyzer);
    semanticAnalyzer.backPatch(semanticAnalyzer.nextQuad, B.falseList);
  }

  private static void action17(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken W = nowNodes.get(0);
    W.nextList = SemanticAnalyzer.makeList(semanticAnalyzer.nextQuad);
    genGoto("_", semanticAnalyzer);
  }

  private static void action19(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Bpl = nowNodes.get(0);
    PToken U = nowNodes.get(2);
    PToken O = nowNodes.get(3);
    PToken Bpr = nowNodes.get(4);
    Bpl.setTrueList(O.trueList);
    Bpl.setFalseList(O.falseList);
    Bpl.quad = U.quad;
    Bpl.bpType = false;
  }

  private static void action20(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken U = nowNodes.get(0);
    U.setQuad(semanticAnalyzer.nextQuad);
  }

  private static void action21(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Opl = nowNodes.get(0);
    PToken U = nowNodes.get(2);
    PToken Q = nowNodes.get(3);
    Opl.setTrueList(Q.trueList);
    Opl.setFalseList(Q.falseList);
    Opl.quad = U.quad;
    Opl.bpType = true;
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

  private static void action24(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken E1 = nowNodes.get(1);
    PToken relop = nowNodes.get(2);
    PToken E2 = nowNodes.get(3);
    Q.setTrueList(SemanticAnalyzer.makeList(semanticAnalyzer.nextQuad));
    Q.setFalseList(SemanticAnalyzer.makeList(semanticAnalyzer.nextQuad + 1));
    genIfGoto("_", E1, relop, E2, semanticAnalyzer);
    genGoto("_", semanticAnalyzer);
  }

  private static void action25(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken True = nowNodes.get(1);
    Q.setTrueList(SemanticAnalyzer.makeList(semanticAnalyzer.nextQuad));
    genGoto("_", semanticAnalyzer);
  }

  private static void action26(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken Q = nowNodes.get(0);
    PToken False = nowNodes.get(1);
    Q.setFalseList(SemanticAnalyzer.makeList(semanticAnalyzer.nextQuad));
    genGoto("_", semanticAnalyzer);
  }

  private static void action27(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    semanticAnalyzer.paramQueue.clear();
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

  private static void action37(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(2);
    if (!E.type.equals(semanticAnalyzer.retType)) {
      System.out.println("Error at Line " + E.lineIndex + ":" + "Mismatch Return Type!");
      semanticAnalyzer.setWrongEnd("Error at Line " + E.lineIndex + ":" + "Mismatch Return Type!");
    }
    semanticAnalyzer
        .addAnswers(new ThreeAddr("return " + E.addr, "return", new String[]{" ", " ", E.addr}));
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
    semanticAnalyzer.addAnswers(new ThreeAddr(Lp.addr + "=" + Lp.width, "=",
        new String[]{String.valueOf(Lp.width), " ", Lp.addr}));
  }

  private static void action47(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken L = nowNodes.get(1);
    PToken E = nowNodes.get(3);
    semanticAnalyzer.addAnswers(new ThreeAddr(L.addr + "=" + E.addr, "=",
        new String[]{String.valueOf(E.addr), " ", L.addr}));
  }

  private static void action48(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(0);
    PToken L = nowNodes.get(1);
    E.setAddr(semanticAnalyzer.newTemp());
    E.setType(L.type);
    semanticAnalyzer.addAnswers(new ThreeAddr(E.addr + "=" + L.addr, "=",
        new String[]{String.valueOf(L.addr), " ", E.addr}));
  }

  private static void action49(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken relop = nowNodes.get(0);
    PToken endSymbol = nowNodes.get(1);
    relop.setAddr(endSymbol.token);
  }

  private static void action50(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken M = nowNodes.get(0);
    PToken X = nowNodes.get(1);
    PToken Identifier = nowNodes.get(2);
    PToken Mp = nowNodes.get(3);
    M.paramLen = Mp.paramLen + 1;
    M.paramNameList = SemanticAnalyzer
        .mergeList(Mp.paramNameList, SemanticAnalyzer.makeList(Identifier.lexeme));
    M.paramTypeList = SemanticAnalyzer
        .mergeList(Mp.paramTypeList, SemanticAnalyzer.makeList(X.type));
  }

  private static void action51(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken M = nowNodes.get(0);
    M.paramLen = 0;
    M.paramNameList.clear();
    M.paramTypeList.clear();
  }

  private static void action52(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken D = nowNodes.get(0);
    PToken Identifier = nowNodes.get(3);
    PToken M = nowNodes.get(5);
    for (int ind = 0; ind < M.paramLen; ind++) {
      try {
        semanticAnalyzer.enter(M.paramNameList.get(ind), M.paramTypeList.get(ind));
      } catch (Exception e) {
        System.out.println("Error at Line " + D.lineIndex + ":" + e.getMessage());
        semanticAnalyzer.setWrongEnd("Error at Line " + D.lineIndex + ":" + e.getMessage());
      }
    }
    semanticAnalyzer.functionList.put(Identifier.lexeme, new Pair<>(M.paramTypeList, M.paramLen));
  }

  private static void action53(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(1);
    semanticAnalyzer.addParamQueue(new Pair<>(E.addr, E.type));
  }

  private static void action54(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken E = nowNodes.get(2);
    semanticAnalyzer.addParamQueue(new Pair<>(E.addr, E.type));
  }

  private static void action55(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken B = nowNodes.get(0);
    PToken O = nowNodes.get(1);
    PToken Bp = nowNodes.get(1);
    B.setTrueList(SemanticAnalyzer.mergeList(O.trueList, Bp.trueList));
    if (Bp.falseList.size() != 0) {
      B.setFalseList(Bp.falseList);
    } else {
      B.setFalseList(O.falseList);
    }
    if (Bp.bpType != null) {
      if (Bp.bpType) {
        semanticAnalyzer.backPatch(Bp.quad, O.trueList);
      } else {
        semanticAnalyzer.backPatch(Bp.quad, O.falseList);
      }
    }
  }

  private static void action56(List<PToken> nowNodes, SemanticAnalyzer semanticAnalyzer) {
    PToken O = nowNodes.get(0);
    PToken Q = nowNodes.get(1);
    PToken Op = nowNodes.get(2);
    O.setFalseList(SemanticAnalyzer.mergeList(Q.falseList, Op.falseList));
    if (Op.trueList.size() != 0) {
      O.setTrueList(Op.trueList);
    } else {
      O.setTrueList(Q.trueList);
    }
    if (Op.bpType != null) {
      if (Op.bpType) {
        semanticAnalyzer.backPatch(Op.quad, Q.trueList);
      } else {
        semanticAnalyzer.backPatch(Op.quad, Q.falseList);
      }
    }
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
      case 3:
        action3(nowNodes, semanticAnalyzer);
        break;
      case 4:
        action4(nowNodes, semanticAnalyzer);
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
      case 9:
        action9(nowNodes, semanticAnalyzer);
        break;
      case 10:
        action10(nowNodes, semanticAnalyzer);
        break;
      case 11:
        action11(nowNodes, semanticAnalyzer);
        break;
      case 12:
        action12(nowNodes, semanticAnalyzer);
        break;
      case 14:
        action14(nowNodes, semanticAnalyzer);
        break;
      case 15:
        action15(nowNodes, semanticAnalyzer);
        break;
      case 16:
        action16(nowNodes, semanticAnalyzer);
        break;
      case 17:
        action17(nowNodes, semanticAnalyzer);
        break;
      case 19:
        action19(nowNodes, semanticAnalyzer);
        break;
      case 20:
        action20(nowNodes, semanticAnalyzer);
        break;
      case 21:
        action21(nowNodes, semanticAnalyzer);
        break;
      case 22:
        action22(nowNodes, semanticAnalyzer);
        break;
      case 23:
        action23(nowNodes, semanticAnalyzer);
        break;
      case 24:
        action24(nowNodes, semanticAnalyzer);
        break;
      case 25:
        action25(nowNodes, semanticAnalyzer);
        break;
      case 26:
        action26(nowNodes, semanticAnalyzer);
        break;
      case 27:
        action27(nowNodes, semanticAnalyzer);
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
      case 33:
        action33(nowNodes, semanticAnalyzer);
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
      case 37:
        action37(nowNodes, semanticAnalyzer);
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
      case 48:
        action48(nowNodes, semanticAnalyzer);
        break;
      case 49:
        action49(nowNodes, semanticAnalyzer);
        break;
      case 50:
        action50(nowNodes, semanticAnalyzer);
        break;
      case 51:
        action51(nowNodes, semanticAnalyzer);
        break;
      case 52:
        action52(nowNodes, semanticAnalyzer);
        break;
      case 53:
        action53(nowNodes, semanticAnalyzer);
        break;
      case 54:
        action54(nowNodes, semanticAnalyzer);
        break;
      case 55:
        action55(nowNodes, semanticAnalyzer);
        break;
      case 56:
        action56(nowNodes, semanticAnalyzer);
        break;
      default:
        System.out.println("ERROR ACTION!");
        System.exit(0);
    }
  }
}

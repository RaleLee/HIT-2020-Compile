package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import javafx.util.Pair;
import lexical.LexicalAnalyzer;

public class GrammarAnalyzer {

  public static final String grammarPath = "config\\Grammar\\LL1.txt";
  public static final String correctTestPath = "inputFile\\grammarTest\\grammarCorrectTest.txt";
  public static final String epsilon = "ε";
  public static final String end = "$";
  // Use LL1
  private final List<Production> productions = new ArrayList<>();
  // Predict table
  private final Map<String, Map<String, Production>> Table = new HashMap<>();
  // End symbol
  private final Set<String> endSymbols = new HashSet<>();


  // Non Terminal symbols
  private final Set<String> nonTerminals = new HashSet<>();
  // epsilon set
  private final Set<String> epsilonSymbols = new HashSet<>();
  // Standard out put in instruct book
  private final List<String> standardOut = new ArrayList<>();
  // Error Message
  private final List<String> errorMessage = new ArrayList<>();

  public static List<Token> getTokensFromPath(String inputPath) {
    LexicalAnalyzer la = new LexicalAnalyzer(new File(LexicalAnalyzer.dfaFilePath), false);
    StringBuilder programInput = new StringBuilder();
    try {
      Scanner sc = new Scanner(new File(inputPath));

      while (sc.hasNextLine()) {
        programInput.append(sc.nextLine()).append("\n");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return la.Analyzer(programInput.toString());
  }

  public static void main(String[] args) {

//    List<Token> test = new ArrayList<>();
//    test.add(new Token("+", null, 1));
//    test.add(new Token("id", null, 1));
//    test.add(new Token("*", null, 1));
//    test.add(new Token("+", null, 1));
//    test.add(new Token("id", null, 1));
//    test.add(new Token("*", null,1));
//    test.add(new Token("id", null,1));
//    test.add(new Token("$", null, 1));

    GrammarAnalyzer ga = new GrammarAnalyzer(new File(grammarPath));
    List<String> ta = ga.showTable();
    List<String> fi = ga.showFirst();
    List<String> fo = ga.showFollow();
    List<String> st = ga.getStandardOut();
    for (String s : fi) {
      System.out.println(s);
    }
    for (String s : fo) {
      System.out.println(s);
    }
    for (String s : ta) {
      System.out.println(s);
    }
    ga.Analyzer(getTokensFromPath(correctTestPath));
    for (String s : st) {
      System.out.println(s);
    }
  }

  public Set<String> getEndSymbols() {
    return endSymbols;
  }


  // First set
  private final Map<String, Set<String>> firstSet = new HashMap<>();
  // Follow set
  private final Map<String, Set<String>> followSet = new HashMap<>();
  // Select set
  private final Map<Production, Set<String>> select = new HashMap<>();
  private Stack<String> analyzer = new Stack<>();
  private Stack<Integer> treeDepth = new Stack<>();
  // Start symbol
  private String start;

  public Set<String> getNonTerminals() {
    return nonTerminals;
  }

  public Map<String, Set<String>> getFirstSet() {
    return firstSet;
  }


  /**
   * The constructor of Grammar Analyzer
   *
   * @param file the configure file of grammar analyzer.
   */
  public GrammarAnalyzer(File file) {
    readFile(file);
    // Construct epsilon set
    creEpsilonSet();
    // Construct first set
    creFirstSet();
    // Construct follow set
    creFollowSet();
    // Construct select set
    creSelectSet();
    // Construct Table
    creTable();
  }

  public Map<String, Set<String>> getFollowSet() {
    return followSet;
  }

  public Map<String, Map<String, Production>> getTable() {
    return Table;
  }

  public List<String> showTable() {
    List<String> ret = new ArrayList<>();
    for (String s : Table.keySet()) {
      Map<String, Production> map = Table.get(s);

      for (String ter : map.keySet()) {
        ret.add(s + " " + ter + " " + map.get(ter).toString());
      }
    }

    return ret;
  }

  public List<String> showFirst() {
    List<String> ret = new ArrayList<>();
    for (String s : firstSet.keySet()) {
      StringBuilder sb = new StringBuilder(s + " ");
      Set<String> set = firstSet.get(s);
      if (epsilonSymbols.contains(s)) {
        sb.append(epsilon).append(" ");
      }
      for (String fir : set) {
        sb.append(fir).append(" ");
      }
      ret.add(sb.toString());
    }
    return ret;
  }

  public List<String> showFollow() {
    List<String> ret = new ArrayList<>();
    for (String s : followSet.keySet()) {
      StringBuilder sb = new StringBuilder(s + " ");
      Set<String> set = followSet.get(s);
      for (String fir : set) {
        sb.append(fir).append(" ");
      }
      ret.add(sb.toString());
    }
    return ret;
  }

  public List<String> getStandardOut() {
    return standardOut;
  }

  public List<String> getErrorMessage() {
    return errorMessage;
  }

  /**
   * 进行语法分析
   *
   * @param lexicalOut 词法分析器的结果，List<Token>形式.
   * @return 返回一个Ptoken和在树中的深度，是一一对应的关系，用于GUI画树
   */
  public Pair<List<PToken>, List<Integer>> Analyzer(List<Token> lexicalOut) {
    reset();
    int index = 0;
    // 这一组用于存储不含有epsilon的
    List<PToken> outS = new ArrayList<>();
    List<Integer> outH = new ArrayList<>();
    // 这一组用于存储含有epsilon的
    List<PToken> outIE = new ArrayList<>();
    List<Integer> outIEH = new ArrayList<>();

    while (!analyzer.empty()) {
      // print now stack and input sequence
      for (String s : analyzer) {
        System.out.print(s + " ");
      }
      System.out.print("| ");
      for (int i = index; i < lexicalOut.size(); i++) {
        System.out.print(lexicalOut.get(i).getSpName() + " ");
      }
      System.out.println();

      String curGra = analyzer.pop();
      int depth = treeDepth.pop();
      Token curTok = lexicalOut.get(index);
      String curLex = curTok.getSpName();
      if (curGra.equals(curLex)) {
        index++;
        outH.add(depth);
        outS.add(new PToken(curGra, curTok.getLineIndex(), curTok.getAttValue()));
        outIE.add(new PToken(curGra, curTok.getLineIndex(), curTok.getAttValue()));
        outIEH.add(depth);
      } else if (endSymbols.contains(curGra) || curGra.equals(end)) {
        // 如果栈顶的终结符和输入符号不匹配，则弹出栈顶的终结符
        System.out.println("Error at Line " + curTok.getLineIndex() + ": "
            + "栈顶的终结符" + curGra + "和输入符号" + curLex + "不匹配");
        errorMessage.add("Error at Line " + curTok.getLineIndex() + ": "
            + "栈顶的终结符" + curGra + "和输入符号" + curLex + "不匹配");
        System.out.println("采用错误恢复，弹出栈顶终结符: " + curGra);
        errorMessage.add("采用错误恢复，弹出栈顶终结符: " + curGra);
      } else if (Table.get(curGra).containsKey(curLex)) {
        if (Table.get(curGra).get(curLex).getSync()) {
          // 如果M[A,a]是sync，则弹出栈顶的非终结符A，试图继续分析后面的语法成分
          System.out.println("Error at Line " + curTok.getLineIndex() + ": "
              + "跳转表[" + curGra + "," + curLex +
              "]为sync，采用错误恢复，弹出栈顶非终结符 " + curGra);
          errorMessage.add("Error at Line " + curTok.getLineIndex() + ": "
              + "跳转表[" + curGra + "," + curLex +
              "]为sync，采用错误恢复，弹出栈顶非终结符 " + curGra);
          continue;
        }
        List<String> right = Table.get(curGra).get(curLex).getRight();

        int size = right.size();
        if (right.get(0).equals(epsilon)) {
          // 为了不打印产生epsilon的符号，这里就不将其加入输出队列中了
          // 不对，因为要画树出来，还是得加，但是输出的时候判断一下不输出就行
          // 这里用两个List存吧，一个是含有epsilon的，一个是不含有的
          outIEH.add(depth);
          outIE.add(new PToken(curGra, curTok.getLineIndex()));
          continue;
        }
        for (int i = size - 1; i >= 0; i--) {
          analyzer.push(right.get(i));
          treeDepth.push(depth + 1);
        }
        outH.add(depth);
        outS.add(new PToken(curGra, curTok.getLineIndex()));
        outIEH.add(depth);
        outIE.add(new PToken(curGra, curTok.getLineIndex()));
      } else {
        // 如果M[A,a]是空，表示检测到错误，根据恐慌模式，忽略输入符号a
        System.out.println("Error at Line " + curTok.getLineIndex() + ": "
            + "跳转表[" + curGra + "," + curLex + "]为空，检测到错误，忽略输入符号" + curLex);
        errorMessage.add("Error at Line " + curTok.getLineIndex() + ": "
            + "跳转表[" + curGra + "," + curLex + "]为空，检测到错误，忽略输入符号" + curLex);
        index++;
        analyzer.push(curGra);
        treeDepth.push(depth);
      }
    }

    // Print the tree
    System.out.println(outS.get(0) /*+ " (" + outH.get(0) + ")"*/);
    standardOut.add(outS.get(0).toString());
    for (int i = 0; i < outS.size() - 1; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < outH.get(i); j++) {
        if (j == outH.get(i) - 1) {
          System.out.print("  ");
          sb.append("  ");
//          System.out.print("┗----");
          System.out.println(outS.get(i) /*+ " (" + outH.get(i) + ")"*/);
          sb.append(outS.get(i).toString());
          standardOut.add(sb.toString());
        } else {
          boolean isBrother = false;
          for (int k = i; k < outS.size(); k++) {
            if (outH.get(k) < j + 1) {
              break;
            } else if (outH.get(k) == j + 1) {
              //isBrother = true; TODO:暂时注释掉，下面修改后重置
              break;
            }
          }
          System.out.print("  ");
          sb.append("  ");
          // 这里暂时修改，是为了按照指导书的样式输出
//          if (isBrother) {
//            System.out.print("  ");
////            System.out.print("|    ");
//          } else {
//            System.out.print("  ");
////            System.out.print("     ");
//          }
        }
      }
    }
    return new Pair<>(outIE, outIEH);
  }


  private void reset() {
    analyzer = new Stack<>();
    treeDepth = new Stack<>();
    analyzer.push(end);
    analyzer.push(start);
    treeDepth.push(-1);
    treeDepth.push(0);
    standardOut.clear();
    errorMessage.clear();
  }

  /**
   * Find those symbols that will create epsilon
   */
  private void creEpsilonSet() {
    for (Production pro : productions) {
      List<String> ri = pro.getRight();
      if (ri.size() == 1 && ri.get(0).equals(epsilon)) {
        epsilonSymbols.add(pro.getLeft());
      }
    }
    boolean isUpdate = true;
    while (isUpdate) {
      isUpdate = false;
      for (Production pro : productions) {
        List<String> ri = pro.getRight();
        boolean isFind = true;
        for (String s : ri) {
          // Any symbol not
          if (!epsilonSymbols.contains(s)) {
            isFind = false;
            break;
          }
        }
        if (isFind) {
          if (!epsilonSymbols.contains(pro.getLeft())) {
            isUpdate = true;
            epsilonSymbols.add(pro.getLeft());
          }
        }
      }
    }
  }

  private void creFirstSet() {
    // init
    for (String s : nonTerminals) {
      firstSet.put(s, new HashSet<>());
    }
    // Add direct symbol
    for (Production pro : productions) {
      List<String> right = pro.getRight();
      if (endSymbols.contains(right.get(0))) {
//        System.out.println(pro.getLeft());
        firstSet.get(pro.getLeft()).add(right.get(0));
      }
    }
    boolean isUpdate = true;
    while (isUpdate) {
      isUpdate = false;
      for (Production pro : productions) {
        String left = pro.getLeft();
        List<String> right = pro.getRight();
        for (String s : right) {
          // if is terminal symbol
          if (endSymbols.contains(s)) {
            if (!firstSet.get(left).contains(s)) {
              firstSet.get(left).add(s);
              isUpdate = true;
            }
            break;
          }
          // if is non terminal symbol
          else if (nonTerminals.contains(s)) {
//            System.out.println(left+ " "+firstSet.get(left) + " "+firstSet.get(s).toString());
            //System.out.println();
            if (!firstSet.get(left).containsAll(firstSet.get(s))) {
              firstSet.get(left).addAll(firstSet.get(s));
              isUpdate = true;
            }
            if (!epsilonSymbols.contains(s)) {
              break;
            }
          }
        }
      }
    }
  }

  private void creFollowSet() {
    // A temp Map to save the follow set
    Map<String, Set<String>> tmp = new HashMap<>();
    // init
    for (String s : nonTerminals) {
      followSet.put(s, new HashSet<>());
      tmp.put(s, new HashSet<>());
    }
    // put $ into Start Symbol follow set
    followSet.get(start).add(end);

    for (Production pro : productions) {
      String left = pro.getLeft();
      List<String> right = pro.getRight();
      int size = right.size();
      for (int i = 0; i < size; i++) {
        if (i < size - 1) {
          // 如果存在一个产生式A→αBβ，那么FIRST(β)中除ε之外的所有符号都在FOLLOW(B)中
          if (nonTerminals.contains(right.get(i))) {
            for (int j = i + 1; j < size; j++) {
              if (nonTerminals.contains(right.get(j))) {
                followSet.get(right.get(i)).addAll(firstSet.get(right.get(j)));
                if (epsilonSymbols.contains(right.get(j))) {
                  if (j == size - 1) {
                    tmp.get(right.get(i)).add(left);
                  }
                  continue;
                }
              } else if (endSymbols.contains(right.get(j))) {
                followSet.get(right.get(i)).add(right.get(j));
              }
              break;
            }
          }
        } else if (nonTerminals.contains(right.get(i))) { // last symbol is non-terminal
          tmp.get(right.get(i)).add(left);
        }
      }
    }

    boolean isUpdate = true;
    while (isUpdate) {
      isUpdate = false;
      for (String s : tmp.keySet()) {
        Set<String> tmpSet = tmp.get(s);
        for (String ss : tmpSet) {
          if (!followSet.get(s).containsAll(followSet.get(ss))) {
            followSet.get(s).addAll(followSet.get(ss));
            isUpdate = true;
          }
        }
      }
    }
  }

  private void creSelectSet() {
    for (Production pro : productions) {
      boolean isEpsilon = true;
      select.put(pro, new HashSet<>());
      String left = pro.getLeft();
      List<String> right = pro.getRight();
      for (String s : right) {
        // 如果ε不属于FIRST(α),那么SELECT(A→α)= FIRST(α)
        if (nonTerminals.contains(s)) {
          select.get(pro).addAll(firstSet.get(s));
          if (!epsilonSymbols.contains(s)) {
            isEpsilon = false;
            break;
          }
        } else if (endSymbols.contains(s)) {
          select.get(pro).add(s);
          isEpsilon = false;
          break;
        } else { //如果 ε∈FIRST(α), 那么SELECT(A→α)=( FIRST(α)-{ε} )∪FOLLOW(A)
          select.get(pro).addAll(followSet.get(left));
          isEpsilon = false;
          break;
        }
      }
      if (isEpsilon) {
        select.get(pro).addAll(followSet.get(left));
      }
    }
  }

  private void creTable() {
    for (String s : nonTerminals) {
      Table.put(s, new HashMap<>());
    }

    for (Production pro : select.keySet()) {
      String left = pro.getLeft();
      Set<String> sel = select.get(pro);
      for (String s : sel) {
        Table.get(left).put(s, pro);
      }
    }
    // Add sync
    Production sy = new Production();
    for (String note : followSet.keySet()) {
      Set<String> syncSet = followSet.get(note);
      for (String syn : syncSet) {
        // Find bug here, before add synch should check if null
        Table.get(note).putIfAbsent(syn, sy);
      }
    }
  }

  /**
   * read file for grammar
   *
   * @param file the file will be read
   */
  private void readFile(File file) {
    Scanner sc;
    try {
      sc = new Scanner(file);
      String line = sc.nextLine();
      // Read start symbol
      if (line.contains("====")) {
        line = sc.nextLine();
        start = line;
      }
      line = sc.nextLine();
      // Read end symbol
      if (line.contains("====")) {
        line = sc.nextLine();
        String[] ends = line.split(" ");
        endSymbols.addAll(Arrays.asList(ends));
      }
      line = sc.nextLine();
      // Read Non-terminal
      if (line.contains("====")) {
        line = sc.nextLine();
        String[] nonTerminalSet = line.split(" ");
        nonTerminals.addAll(Arrays.asList(nonTerminalSet));
      }
      line = sc.nextLine();
      // Read production
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          String[] pro = line.split("->");
          System.out.println(line);
          System.out.println(pro[0]);
          // left part
          String left = pro[0].trim();
          // right part
          String[] rights = pro[1].trim().split(" ");
          List<String> right = new ArrayList<>();
          for (String s : rights) {
            if (s.equals("|")) {
              productions.add(new Production(left, right));
              right = new ArrayList<>();
            } else {
              right.add(s);
            }
          }
          productions.add(new Production(left, right));
        }
      }

//      for(String s : nonTerminals){
//        System.out.print(s + " ");
//      }
//      System.out.println();
//      for (Production production : productions) {
//        System.out.println(production.toString());
//      }
      sc.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}

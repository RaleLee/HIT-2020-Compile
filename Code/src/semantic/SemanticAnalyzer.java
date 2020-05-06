package semantic;

import grammar.GrammarAnalyzer;
import grammar.PToken;
import grammar.Production;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javafx.util.Pair;

public class SemanticAnalyzer {

  public static final String grammarPath = "config\\Semantic\\LL1.txt";
  public static final String correctTestPath = "inputFile\\semanticTest\\semanticCorrectTest.txt";
  public final List<String> addrQueue = new ArrayList<>();
  public final List<ThreeAddr> answers = new ArrayList<>();
  public final Map<Integer, List<Integer>> sons = new HashMap<>();
  public int offset;
  public int nowTempLabel;
  public int nextQuad;
  public int tType;
  public int wWidth;
  //语义分析所用的全局变量，会对内容进行更改
  public List<Production> productions;
  public List<PToken> pTokens;
  public List<Integer> depths;

  //信息变量，不会更改
  public SemanticAnalyzer() {
    this.nowTempLabel = 1;
    this.nextQuad = 1;
    this.addrQueue.clear();
    this.answers.clear();
  }

  public static void main(String[] args) {
    GrammarAnalyzer grammarAnalyzer = new GrammarAnalyzer(new File(grammarPath));
    Pair<List<PToken>, List<Integer>> tempPair = grammarAnalyzer
        .Analyzer(GrammarAnalyzer.getTokensFromPath(correctTestPath));
//    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
//    semanticAnalyzer.analyzer(tempPair, grammarAnalyzer);

  }

  private void dfsGrammarTree(Integer curNode, Integer fatherNode) {
    List<Integer> nowNodesToAction = new ArrayList<>();
    if (fatherNode != -1) {
      nowNodesToAction.add(fatherNode);
    }
    if (!sons.containsKey(curNode)) {
      return;
    }
    List<Integer> curSons = sons.get(curNode);
    List<String> curSonsTokens = new ArrayList<>();
    for (Integer son : curSons) {
      curSonsTokens.add(pTokens.get(son).token);
    }
    Production curProduction = Production
        .findProduction(productions, pTokens.get(curNode).token, curSonsTokens);
    Iterator<Integer> iterator = curSons.iterator();
    System.out.println(curProduction.trueRight);
    for (String rightToken : curProduction.getRight()) {
      if (rightToken.contains("#a")) {
        Integer actionNumber = Integer.parseInt(
            rightToken.substring(rightToken.indexOf('a') + 1, rightToken.lastIndexOf('#')));
      } else {
        Integer nextNode = iterator.next();
        dfsGrammarTree(nextNode, curNode);
      }
    }
  }

  public void analyzer(Pair<List<PToken>, List<Integer>> tempPair,
      GrammarAnalyzer grammarAnalyzer) {
    System.out.println("Start Semantic");
    //TODO：根据情况传入GA部分属性
    pTokens = new ArrayList<>(tempPair.getKey());
    depths = new ArrayList<>(tempPair.getValue());
    productions = grammarAnalyzer.getProductions();
    Stack<Integer> fathers = new Stack<>();
    fathers.push(0);
    //忽略 $ 即pTokens的最后一个
    pTokens.remove(pTokens.size() - 1);
    for (int ind = 1; ind < pTokens.size(); ind++) {
      Integer curDepth = depths.get(ind);
      while (depths.get(fathers.peek()) >= curDepth) {
        fathers.pop();
      }
      Integer curFather = fathers.peek();
      if (!sons.containsKey(curFather)) {
        sons.put(curFather, new ArrayList<>());
      }
      sons.get(curFather).add(ind);
      fathers.push(ind);
    }
    dfsGrammarTree(0, -1);
  }
}


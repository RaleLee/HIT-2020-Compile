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

  public static final String grammarPath = "config\\Semantic\\LL1-Semantic.txt";
  public static final String correctTestPath = "inputFile\\semanticTest\\semanticCorrectTest.txt";

  public final List<String> addrQueue = new ArrayList<>();
  public final List<ThreeAddr> answers = new ArrayList<>();
  public final Map<Integer, List<Integer>> sons = new HashMap<>();
  public final Map<String, Pair<String, Integer>> symbolList = new HashMap<>();
  public int offset;
  public int nowTempLabel;
  public String tType;
  public int nextQuad;
  public int wWidth;
  //语义分析所用的全局变量，会对内容进行更改
  public List<Production> productions;
  public List<PToken> pTokens;
  public List<Integer> depths;

  //信息变量，不会更改
  public SemanticAnalyzer() {
    this.nowTempLabel = 1;
    this.nextQuad = 1;
    this.offset = 0;
    this.wWidth = -1;
    this.tType = null;
    this.addrQueue.clear();
    this.answers.clear();
  }

  public static void main(String[] args) {
    GrammarAnalyzer grammarAnalyzer = new GrammarAnalyzer(new File(SemanticAnalyzer.grammarPath));
    Pair<List<PToken>, List<Integer>> tempPair = grammarAnalyzer
        .Analyzer(GrammarAnalyzer.getTokensFromPath(correctTestPath));
    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyzer(tempPair, grammarAnalyzer);
    System.out.println(String.join("\n", semanticAnalyzer.getResults()));
  }

  public void enter(String name, String type) throws Exception {
    if (this.symbolList.containsKey("name")) {
      throw new Exception("Repeat define!");
    }
    this.symbolList.put(name, new Pair<>(type, offset));
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public void setNowTempLabel(int nowTempLabel) {
    this.nowTempLabel = nowTempLabel;
  }

  public void setNextQuad(int nextQuad) {
    this.nextQuad = nextQuad;
  }

  public void settType(String tType) {
    this.tType = tType;
  }

  public void setwWidth(int wWidth) {
    this.wWidth = wWidth;
  }

  public List<String> getResults() {
    List<String> outResults = new ArrayList<>();
    for (ThreeAddr threeAddr : this.answers) {
      outResults.add(threeAddr.toString());
    }
    return outResults;
  }

  private void dfsGrammarTree(Integer curNode, Integer fatherNode) {
    if (!sons.containsKey(curNode)) {
      return;
    }
    List<PToken> nowNodesToAction = new ArrayList<>();
    nowNodesToAction.add(pTokens.get(curNode));
    List<Integer> curSons = sons.get(curNode);
    List<String> curSonsTokens = new ArrayList<>();
    for (Integer son : curSons) {
      curSonsTokens.add(pTokens.get(son).token);
    }
    Production curProduction = Production
        .findProduction(productions, pTokens.get(curNode).token, curSonsTokens);
    Iterator<Integer> iterator = curSons.iterator();
    for (String rightToken : curProduction.trueRight) {
      if (rightToken.contains("#a")) {
        Integer actionNumber = Integer.parseInt(
            rightToken.substring(rightToken.indexOf('a') + 1, rightToken.lastIndexOf('#')));
        System.out.println("#a" + actionNumber + "#");
        System.out.println(curProduction.toTrueString());
        Actions.actionEntry(actionNumber, nowNodesToAction, this);
        for (PToken nowNodes : nowNodesToAction) {
          System.out.println(nowNodes + "addr:" + nowNodes.addr);
          System.out.println(nowNodes + "type:" + nowNodes.type);
          System.out.println(nowNodes + "width:" + nowNodes.width);
        }
      } else {
        Integer nextNode = iterator.next();
        nowNodesToAction.add(pTokens.get(nextNode));
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
    int tokensLen = pTokens.size();
    for (int ind = 1; ind < tokensLen; ind++) {
      if (!sons.containsKey(ind) && !grammarAnalyzer.checkTokenIsTerminal(pTokens.get(ind))) {
        sons.put(ind, new ArrayList<>());
        pTokens.add(new PToken(GrammarAnalyzer.epsilon, pTokens.get(ind).lineIndex));
        sons.put(ind, new ArrayList<>());
        sons.get(ind).add(pTokens.size() - 1);
      }
    }
    dfsGrammarTree(0, -1);
  }
}


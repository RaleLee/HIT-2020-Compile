package semantic;

import grammar.GrammarAnalyzer;
import grammar.PToken;
import grammar.Production;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javafx.util.Pair;

public class SemanticAnalyzer {

  public static final String grammarPath = "config\\Semantic\\LL1-Semantic.txt";
  public static final String correctTestPath = "inputFile\\semanticTest\\SemanticCorrectTest.txt";

  public final List<Pair<String, String>> paramQueue = new ArrayList<>();//Pair:addr type
  public final List<ThreeAddr> answers = new ArrayList<>();
  public final Map<String, Pair<List<String>, Integer>> functionList = new HashMap<>();//pair:types paramsize
  public List<Integer> arrayWidth = new ArrayList<>();

  public final Map<Integer, List<Integer>> sons = new HashMap<>();
  public final Map<String, Pair<String, Integer>> symbolList = new HashMap<>();
  public String wrongEnd;
  public int offset;
  public int nowTempLabel;
  public String tType;
  public String retType;
  public int nextQuad;
  public int wWidth;
  public int arrayElementWidth;
  //语义分析所用的全局变量，会对内容进行更改
  public List<Production> productions;
  public List<PToken> pTokens;
  public List<Integer> depths;

  public SemanticAnalyzer() {
    this.reSet();
  }

  public static void main(String[] args) {
    GrammarAnalyzer grammarAnalyzer = new GrammarAnalyzer(new File(SemanticAnalyzer.grammarPath));
    Pair<List<PToken>, List<Integer>> tempPair = grammarAnalyzer
        .Analyzer(GrammarAnalyzer.getTokensFromPath(correctTestPath));
    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer
        .analyzer(tempPair, grammarAnalyzer.getProductions(), grammarAnalyzer.getEndSymbols());
    System.out.println(String.join("\n", semanticAnalyzer.getResults()));
  }

  public static <T> List<T> makeList(T tempQuad) {
    return Collections.singletonList(tempQuad);
  }

  public static <T> List<T> mergeList(List<T> aList, List<T> bList) {
    List<T> t1 = new ArrayList<>(aList);
    List<T> t2 = new ArrayList<>(bList);
    t1.addAll(t2);
    return new ArrayList<>(new HashSet<>(t1));
  }

  private void reSet() {
    this.nowTempLabel = 0;
    this.nextQuad = 0;
    this.offset = 0;
    this.wWidth = -1;
    this.tType = null;
    this.wrongEnd = null;
    this.retType = null;
    this.paramQueue.clear();
    this.answers.clear();
    this.arrayWidth.clear();
    this.sons.clear();
    this.symbolList.clear();
    this.functionList.clear();
    this.sons.clear();
    this.symbolList.clear();
    this.functionList.clear();
    this.paramQueue.clear();
    this.arrayWidth.clear();
  }

  public void addParamQueue(Pair<String, String> tempPair) {
    this.paramQueue.add(tempPair);
  }

  public void addAnswers(ThreeAddr threeAddr) {
    this.answers.add(threeAddr);
    this.setNextQuad(this.nextQuad + 1);
  }

  public void backPatch(Integer quad, List<Integer> answerList) {
    for (int i : answerList) {
      this.answers.get(i).backPatch(quad);
    }
  }

  public void setWrongEnd(String wrongEnd) {
    this.wrongEnd = wrongEnd;
  }

  public String newTemp() {
    this.setNowTempLabel(this.nowTempLabel + 1);
    return "t" + this.nowTempLabel;
  }

  public void enter(String name, String type) throws Exception {
    if (this.symbolList.containsKey(name)) {
      throw new Exception("Repeat define!");
    }
    this.symbolList.put(name, new Pair<>(type, offset));
  }

  public Pair<String, Integer> lookUp(String key) throws Exception {
    if (!this.symbolList.containsKey(key)) {
      throw new Exception("UnDefine！");
    }
    return this.symbolList.get(key);
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
    for (int ind = 0; ind < this.answers.size(); ind++) {
      outResults.add(ind + ": " + this.answers.get(ind).toString());
    }
    if (this.wrongEnd != null) {
      outResults.add(this.wrongEnd);
    }
    return outResults;
  }

  private void dfsGrammarTree(Integer curNode) {
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
        if (this.wrongEnd != null) {
          return;
        }
      } else {
        Integer nextNode = iterator.next();
        nowNodesToAction.add(pTokens.get(nextNode));
        dfsGrammarTree(nextNode);
        if (this.wrongEnd != null) {
          return;
        }
      }
    }
  }

  public void analyzer(Pair<List<PToken>, List<Integer>> tempPair, List<Production> tempProductions,
      Set<String> endSymbols) {
    this.reSet();
    System.out.println("Start Semantic");
    pTokens = new ArrayList<>(tempPair.getKey());
    depths = new ArrayList<>(tempPair.getValue());
    productions = tempProductions;
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
      if (!sons.containsKey(ind) && !endSymbols.contains(pTokens.get(ind).token)) {
        sons.put(ind, new ArrayList<>());
        pTokens.add(new PToken(GrammarAnalyzer.epsilon, pTokens.get(ind).lineIndex));
        sons.put(ind, new ArrayList<>());
        sons.get(ind).add(pTokens.size() - 1);
      }
    }
    dfsGrammarTree(0);
  }
}


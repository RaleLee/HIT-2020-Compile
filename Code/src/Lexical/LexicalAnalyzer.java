package Lexical;

import javafx.scene.control.Tab;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

  // Jump Table
  Map<Integer, Map<Pattern, Integer>> Table = new HashMap<>();
  // Map of state name, Integer for state number, String for the recognized end state name.
  Map<Integer, String> stateName = new HashMap<>();
  // Set of the state that have attribute value
  Set<Integer> attState = new HashSet<>();
  // Set of const
  Set<String> constValue = new HashSet<>();
  // Map of abbreviation
  Map<String, String> abbMap = new HashMap<>();
  // Current State
  private int curState = 0;
  // Processed word
  private String processedWord = "";
  // total State number
  private int totStateNum;

  // NFA Jump Table
  Map<Integer, Map<String, Set<Integer>>> nfaTable = new HashMap<>();
  Map<Set<Integer>, Map<String, Set<Integer>>> dfaTable = new HashMap<>();
  Map<Integer, Map<String, Integer>> dTable = new HashMap<>();
  Set<Integer> nfafinalState = new HashSet<Integer>();
  Set<Integer> dfafinalState = new HashSet<Integer>();
  Map<Integer, List<String>> dfaStateName = new HashMap<>();
  // Map of state name, Integer for state number, String for the recognized end state name.
  Map<Integer, String> nfaStateName = new HashMap<>();
  // Set of the state that have attribute value
  Set<Integer> nfaAttState = new HashSet<>();
  // All input character
  Set<String> inputChar = new HashSet<>();

  public static final String inputFilePath = "inputFile/test.txt";
  public static final String wrongInputFilePath = "inputFile/wrong1.txt";
  public static final String dfaFilePath = "config/dfa.txt";
  public static final String nfaFilePath = "config/nfa3.txt";//TODO: 改为nfa的路径

  public LexicalAnalyzer(File faFile, Boolean isNfa) {
    if(isNfa){
      readNFAFile(faFile);
      convertNFAtoDFA();
    }else {
      readDFAFile(faFile);
    }
  }

  public static void main(String[] args) throws FileNotFoundException {

    File nfaFile = new File(nfaFilePath);
    LexicalAnalyzer nla = new LexicalAnalyzer(nfaFile, true);
    File inputFile4 = new File("inputFile/test.txt");
    Scanner sc = new Scanner(inputFile4);
    StringBuilder input2 = new StringBuilder();
    while(sc.hasNextLine()){
      input2.append(sc.nextLine() + "\n");

    }
    List<String> ret = nla.Analyzer(input2.toString(), true);
    for(String s : ret){
      System.out.print(s);
    }

    File dfaFile = new File(dfaFilePath);
    LexicalAnalyzer la = new LexicalAnalyzer(dfaFile, false);
    File inputFile = new File(inputFilePath);
    sc = new Scanner(inputFile);
    StringBuilder input = new StringBuilder();
    while (sc.hasNextLine()) {
      input.append(sc.nextLine()).append("\n");
    }
    ret = la.Analyzer(input.toString(), false);
    for (String s : ret) {
      System.out.print(s);
    }
    File inputFile2 = new File(wrongInputFilePath);
    sc = new Scanner(inputFile2);
    input = new StringBuilder();
    while (sc.hasNextLine()) {
      input.append(sc.nextLine()).append("\n");
    }
    ret = la.Analyzer(input.toString(), false);
    System.out.println("WORK END");
    for (String s : ret) {
      System.out.print(s);
    }
  }

  /**
   * Using subset construction to convert NFA to DFA.
   */
  private void convertNFAtoDFA(){
    for(Integer i : nfaTable.keySet()){
      Map<String, Set<Integer>> ta = nfaTable.get(i);
      for(String s : ta.keySet()){
        System.out.print(i);
        System.out.print(" ");
        System.out.print(s+ " ");
        for(Integer p : ta.get(s)) {
          System.out.print(p);
          System.out.print(" ");
        }
        System.out.println();
      }
    }
    Set<Integer> eclosure0 = e_closureS(0);

    for(Integer i : eclosure0){
      System.out.print(i);
      System.out.print(" ");
    }
    System.out.println();
    // Compute e_closure(T)

    // Convert
    Map<Integer, Boolean> DstatesVis = new HashMap<Integer, Boolean>();
    Map<Integer, Set<Integer>> Dstates = new HashMap<>();
    DstatesVis.put(0, false);
    Dstates.put(0, eclosure0);
    // Add final state
    Set<Integer> tmp = new HashSet<Integer>();
    tmp.addAll(eclosure0);
    tmp.retainAll(nfafinalState);
    if(!tmp.isEmpty()){
      dfafinalState.add(0);
    }

    Integer s = endConvert(DstatesVis);
//    System.out.println(s);
//    System.out.println(DstatesVis.get(s));
    boolean b = DstatesVis.get(s);

    while(!b && s >= 0){
      DstatesVis.put(s, true);
      dTable.put(s, new HashMap<>());
      // dfaTable.put(e_closures.get(0), )
      Set<Integer> T = Dstates.get(s);
      for(String ch : inputChar){
        System.out.println(ch);
        Set<Integer> moveTch = moveTch(ch, T);

        Set<Integer> U = e_closureT(moveTch);
        if(U.isEmpty()){
          continue;
        }
        if(!Dstates.containsValue(U)){
          if(Dstates.size() != DstatesVis.size()){
            throw new RuntimeException("Fuck");
          }
          int num = Dstates.size();
          Dstates.put(num, U);
          DstatesVis.put(num, false);
          tmp.clear();
          tmp.addAll(U);
          tmp.retainAll(nfafinalState);
          if(!tmp.isEmpty()){
            dfafinalState.add(num);
            dfaStateName.put(num, new ArrayList<String>());
          }
          for(Integer i : tmp){
            if(nfaAttState.contains(i)){
              attState.add(num);
            }
            dfaStateName.get(num).add(nfaStateName.get(i));
          }
//          for(Integer i : U){
//            System.out.print(i);
//            System.out.print(" ");
//          }
//          System.out.println();
        }
        int index = -1;
        Set set = Dstates.entrySet();
        Iterator<Map.Entry<Integer, Set<Integer>>> iterator = set.iterator();
        while(iterator.hasNext()){
          Map.Entry<Integer, Set<Integer>> entry = iterator.next();
          if(entry.getValue().equals(U)){
            index = entry.getKey();
            break;
          }
        }

        dTable.get(s).put(ch, index);
      }

      s = endConvert(DstatesVis);
//      System.out.println(s);
//      System.out.println(DstatesVis.get(s));
      b = DstatesVis.getOrDefault(s, true);
    }
    for(int i : dTable.keySet()){
      Table.put(i, new HashMap<Pattern, Integer>());
      for(String ss : dTable.get(i).keySet()){
        System.out.print(i);
        System.out.print(" ");
        System.out.print(ss + " ");
        System.out.print(dTable.get(i).get(ss));
        System.out.print("\n");

        Table.get(i).put(Pattern.compile(ss), dTable.get(i).get(ss));
      }
      //System.out.println();
    }

    for(int i : dfafinalState){
      System.out.print(i);
      System.out.print(" ");
    }
    System.out.println();
  }

  private Set<Integer> moveTch(String ch, Set<Integer> T){
    Set<Integer> ret = new HashSet<>();
    for(Integer i : T){
      for(String s : nfaTable.get(i).keySet()){
        if(s.contains(ch)){
          ret.addAll(nfaTable.get(i).get(s));
        }
      }
    }
    return ret;
  }

  private Set<Integer> e_closureS(Integer s){
    String epsilon = "ε";
    // Compute e_closure(s0)
    Stack<Integer> st = new Stack<Integer>();
    Set<Integer> e_closure = new HashSet<Integer>();
    st.push(s);
    e_closure.add(s);
    while(!st.empty()){
      Integer cur = st.pop();
      Map<String, Set<Integer>> ma = nfaTable.get(cur);
      for(String str : ma.keySet()){
        if(str.contains(epsilon)){
          Set<Integer> set = ma.get(str);
          for(Integer p : set) {
            if (!e_closure.contains(p)) {
              e_closure.add(p);
              st.push(p);
            }
          }
        }
      }
    }
    return e_closure;
  }

  private Set<Integer> e_closureT(Set<Integer> T){
    Set<Integer> ret = new HashSet<>();
    Stack<Integer> st = new Stack<>();
    for(Integer i : T){
      st.push(i);
      //ret.add(i);
    }
    while(!st.empty()){
      Integer t = st.pop();
      System.out.println(t);
      Set<Integer> el = e_closureS(t);
      for(Integer u : el){
        if(!ret.contains(u)){
          ret.add(u);
          st.push(u);
        }
      }
    }
    return ret;
  }
  private Integer endConvert(Map<Integer, Boolean> Dstates){
    for (Integer s: Dstates.keySet()){
        if(!Dstates.get(s)){
          return s;
        }
    }
    Integer re = -1;
    return re;
  }
  /**
   * To read the file and build NFA.
   *
   * @param file file path of the NFA file
   */
  public void readNFAFile(File file){
    Scanner sc;
    try{
      sc = new Scanner(file);
      String line = sc.nextLine();
      // Read total State number
      if (line.contains("====")) {
        line = sc.nextLine();
        System.out.println(line);
        // Total state number
        totStateNum = Integer.parseInt(line);
        for (int i = 0; i <= totStateNum; i++) {
          this.nfaTable.put(i, new HashMap<>());
        }
      }
      line = sc.nextLine();
      // Read input char
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          for(String ch : words){
            inputChar.add(ch);
          }
        }
      }
      // Read final State
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          int state = Integer.parseInt(words[0]);
          nfaStateName.put(state, words[1]);
          if(words.length > 2){
            nfaAttState.add(state);
          }
          nfafinalState.add(state);

        }
      }
      // Read NFA table
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          int inState = Integer.parseInt(words[0]);
          int outState = Integer.parseInt(words[2]);
          String pat = words[1];
//          for (String p : abbMap.keySet()) {
//            pat = pat.replaceAll(p, abbMap.get(p));
//          }
          System.out.println(pat);
          // Add row
          Set<Integer> set = nfaTable.get(inState).get(pat);
          if(set == null){
            set = new HashSet<Integer>();
          }
          set.add(outState);
          this.nfaTable.get(inState).put(pat, set);
        }
      }
      // Read const
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          constValue.add(line);
        }
      }
      sc.close();

    }catch (FileNotFoundException e){
      // TODO: Exception Handle
    }
  }
  /**
   * To read the file and build DFA.
   *
   * @param file file path of DFA file
   */
  public void readDFAFile(File file) {
    Scanner sc;
    try {
      sc = new Scanner(file);
      String line = sc.nextLine();
      // Read total State number
      if (line.contains("====")) {
        line = sc.nextLine();
        System.out.println(line);
        // Total state number
        totStateNum = Integer.parseInt(line);
        for (int i = 0; i < totStateNum; i++) {
          this.Table.put(i, new HashMap<>());
        }
      }
      line = sc.nextLine();
      // Read the end state index and the name of recognized word
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          int state = Integer.parseInt(words[0]);
          stateName.put(state, words[1]);
          if (words.length > 2) {
            attState.add(state);
          }
        }
      }
      // Read abbreviation name
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          abbMap.put(words[0], words[1]);
        }
      }
      // Read DFA table
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          String[] words = line.split(" ");
          int inState = Integer.parseInt(words[0]);
          int outState = Integer.parseInt(words[2]);
          String pat = words[1];
          for (String p : abbMap.keySet()) {
            pat = pat.replaceAll(p, abbMap.get(p));
          }
          System.out.println(pat);
          // Add row
          this.Table.get(inState).put(Pattern.compile(pat), outState);
        }
      }
      // Read const
      if (line.contains("====")) {
        while (sc.hasNextLine()) {
          line = sc.nextLine();
          System.out.println(line);
          if (line.contains("====")) {
            break;
          }
          constValue.add(line);
        }
      }
      sc.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * To Analyze the input file
   *
   * @param input All Input
   * @return The result of analyzer
   */
  public List<String> Analyzer(String input, boolean isnfa) {
    List<String> ret = new ArrayList<>();
    List<String> error = new ArrayList<>();
    int len = input.length();

    int index = 1;
    for (int i = 0; i < len; i++) {
      char c = input.charAt(i);
      if (c == '\n') {
        index += 1;
      }
      // Handle the wrong state
      if (!ReadChar(c)) {
        if (curState == 0) {
          String tmp = "Error at line " + index + " with " + c + "\n";
          error.add(tmp);
          System.out.println(tmp);
          resetTable();
          continue;
        }
        handleWrongState(ret, index, isnfa);
        if (c == '\n') {
          index -= 1;
        }
        i -= 1;
      } else if (curState == 0) {
        resetTable();
      } else if (i == len - 1) {
        handleWrongState(ret, index, isnfa);
      }
    }
    resetTable();
    ret.addAll(error);
    return ret;
  }

  private List<String> handleWrongState(List<String> ret, int index, boolean isnfa){
    String tmp;
    Map use = null;
    Set stat = null;
    if(isnfa){
      use = dfaStateName;
      stat = attState;
    }else{
      use = stateName;
      stat = attState;
    }
    if (use.containsKey(curState)) {
      if (constValue.contains(processedWord)) {
        tmp = processedWord + "\t<" + processedWord.toUpperCase() + ", - >\n";
        ret.add(tmp);
        System.out.println(tmp);
      } else if (!stat.contains(curState)) {
        if(isnfa){
          tmp = processedWord + "\t<";
          for(String ss : dfaStateName.get(curState)){
            tmp += ss;
          }
          tmp += ", - >\n";
        }else {
          tmp = processedWord + "\t<" + use.get(curState) + ", - >\n";
        }
        ret.add(tmp);
        System.out.println(tmp);
      } else {
        if(isnfa){
          tmp = processedWord + "\t<";
          for(String ss : dfaStateName.get(curState)){
            tmp += ss;
          }
          tmp += ", "+ processedWord +">\n";
        }else {
          tmp = processedWord + "\t<" + use.get(curState) + ", " + processedWord + ">\n";
        }
        ret.add(tmp);
        System.out.println(tmp);
      }
    } else {
      tmp = "Error at line " + index + " with " + processedWord + "\n";
      System.out.println(tmp);
    }
    //char p = '\'';
    resetTable();
    return ret;
  }
  /**
   * To read a char, based on current state. True means the char has been accepted, else the char is
   * a wrong char.
   *
   * @param c The char that need be matched.
   * @return true if there is a pattern matched the char, else false.
   */
  public boolean ReadChar(char c) {
    Map<Pattern, Integer> jumper = Table.get(curState);
    for (Pattern p : jumper.keySet()) {
      Matcher m = p.matcher(c + "");
      if (m.matches()) {
        processedWord += c;
        curState = jumper.get(p);
        System.out.println("Read:" + c + " Current State is: " + curState);
        return true;
      }
    }
    return false;
  }

  /**
   * To reset the Jump Table. Using this before reading a new DFA file!
   */
  public void resetTable() {
    curState = 0;
    processedWord = "";
  }
}

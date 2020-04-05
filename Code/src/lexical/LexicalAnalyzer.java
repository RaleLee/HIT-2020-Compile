package lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

  public static final String nfaFilePath = "config/NFA.txt";
  public static final String inputFilePath = "inputFile/correctTest.txt";
  //public static final String wrongInputFilePath = "inputFile/wrongTest.txt";
  public static final String dfaFilePath = "config/DFA.txt";
  // Jump Table
  final Map<Integer, Map<Pattern, Integer>> Table = new HashMap<>();
  // Map of state name, Integer for state number, String for the recognized end state name.
  final Map<Integer, String> stateName = new HashMap<>();
  // Current State
  private int curState = 0;

  public static void main(String[] args) throws FileNotFoundException {

    File nfaFile = new File(nfaFilePath);
    LexicalAnalyzer nla = new LexicalAnalyzer(nfaFile, true);
    File inputFile4 = new File("inputFile/correctTest.txt");
    Scanner sc = new Scanner(inputFile4);
    StringBuilder input2 = new StringBuilder();
    while (sc.hasNextLine()) {
      input2.append(sc.nextLine()).append("\n");

    }
    List<String> ret = nla.Analyzer(input2.toString(), true);
    for (String s : ret) {
      System.out.print(s);
    }

//    File dfaFile = new File(dfaFilePath);
//    LexicalAnalyzer la = new LexicalAnalyzer(dfaFile, false);
//    la.showDFA();
//    File inputFile = new File(inputFilePath);
//    Scanner sc = new Scanner(inputFile);
//    StringBuilder input = new StringBuilder();
//    while (sc.hasNextLine()) {
//      input.append(sc.nextLine()).append("\n");
//    }
//    List<String> ret = la.Analyzer(input.toString(), false);
//    for (String s : ret) {
//      System.out.print(s);
//    }
////    File inputFile2 = new File(wrongInputFilePath);
////    sc = new Scanner(inputFile2);
////    input = new StringBuilder();
////    while (sc.hasNextLine()) {
////      input.append(sc.nextLine()).append("\n");
////    }
////    ret = la.Analyzer(input.toString(), false);
////    System.out.println("WORK END");
////    for (String s : ret) {
////      System.out.print(s);
////    }
  }

  // Processed word
  private String processedWord = "";
  // total State number
  private int totStateNum;
  // Set of the state that have attribute value
  final Set<Integer> attState = new HashSet<>();
  // Set of const
  final Set<String> constValue = new HashSet<>();
  // Map of abbreviation
  final Map<String, String> abbMap = new HashMap<>();
  // NFA Jump Table
  final Map<Integer, Map<String, Set<Integer>>> nfaTable = new HashMap<>();
  final Map<Integer, Map<String, Integer>> dTable = new HashMap<>();
  final Set<Integer> nfaFinalState = new HashSet<>();
  final Map<Integer, List<String>> dfaStateName = new HashMap<>();
  // Map of state name, Integer for state number, String for the recognized end state name.
  final Map<Integer, String> nfaStateName = new HashMap<>();
  // Set of the state that have attribute value
  final Set<Integer> nfaAttState = new HashSet<>();
  // All input character
  final Set<String> inputChar = new HashSet<>();
  final Set<Integer> dfaFinalState = new HashSet<>();

  public LexicalAnalyzer(File faFile, Boolean isNfa) {
    if (isNfa) {
      readNFAFile(faFile);
      convertNFAtoDFA();
    } else {
      readDFAFile(faFile);
    }
  }


  public List<String> showDFA() {
    List<String> ret = new ArrayList<>();
    for (int i : dTable.keySet()) {
      for (String ss : dTable.get(i).keySet()) {
        String tmp = "";
        tmp += i + " " +
            ss + " " + dTable.get(i).get(ss) + "\n";
        ret.add(tmp);
      }
    }
    return ret;
  }

  public List<String> showNFA() {
    List<String> ret = new ArrayList<>();
    for (int i : nfaTable.keySet()) {
      for (String ss : nfaTable.get(i).keySet()) {
        String tmp = "";
        tmp += i + " " +
            ss + " " + nfaTable.get(i).get(ss) + "\n";
        ret.add(tmp);
      }
    }
    return ret;
  }

  /**
   * Using subset construction to convert NFA to DFA.
   */
  private void convertNFAtoDFA() {
    for (Integer i : nfaTable.keySet()) {
      Map<String, Set<Integer>> ta = nfaTable.get(i);
      for (String s : ta.keySet()) {
        System.out.print(i);
        System.out.print(" ");
        System.out.print(s + " ");
        for (Integer p : ta.get(s)) {
          System.out.print(p);
          System.out.print(" ");
        }
        System.out.println();
      }
    }
    Set<Integer> eclosure0 = e_closureS(0);

    for (Integer i : eclosure0) {
      System.out.print(i);
      System.out.print(" ");
    }
    System.out.println();
    // Compute e_closure(T)

    // Convert
    Map<Integer, Boolean> DStatesVis = new HashMap<>();
    Map<Integer, Set<Integer>> DStates = new HashMap<>();
    DStatesVis.put(0, false);
    DStates.put(0, eclosure0);
    // Add final state
    Set<Integer> tmp = new HashSet<>(eclosure0);
    tmp.retainAll(nfaFinalState);
    if (!tmp.isEmpty()) {
      dfaFinalState.add(0);
    }

    Integer s = endConvert(DStatesVis);
//    System.out.println(s);
//    System.out.println(DStatesVis.get(s));
    boolean b = DStatesVis.get(s);

    while (!b && s >= 0) {
      DStatesVis.put(s, true);
      dTable.put(s, new HashMap<>());
      // dfaTable.put(e_closures.get(0), )
      Set<Integer> T = DStates.get(s);
      for (String ch : inputChar) {
        System.out.println(ch);
        Set<Integer> moveTch = moveTch(ch, T);

        Set<Integer> U = e_closureT(moveTch);
        if (U.isEmpty()) {
          continue;
        }
        if (!DStates.containsValue(U)) {
          if (DStates.size() != DStatesVis.size()) {
            throw new RuntimeException("Fuck");
          }
          int num = DStates.size();
          DStates.put(num, U);
          DStatesVis.put(num, false);
          tmp.clear();
          tmp.addAll(U);
          tmp.retainAll(nfaFinalState);
          if (!tmp.isEmpty()) {
            dfaFinalState.add(num);
            dfaStateName.put(num, new ArrayList<>());
          }
          for (Integer i : tmp) {
            if (nfaAttState.contains(i)) {
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
        Set<Map.Entry<Integer, Set<Integer>>> set = DStates.entrySet();
        for (Map.Entry<Integer, Set<Integer>> entry : set) {
          if (entry.getValue().equals(U)) {
            index = entry.getKey();
            break;
          }
        }

        dTable.get(s).put(ch, index);
      }

      s = endConvert(DStatesVis);
//      System.out.println(s);
//      System.out.println(DStatesVis.get(s));
      b = DStatesVis.getOrDefault(s, true);
    }
    for (int i : dTable.keySet()) {
      Table.put(i, new HashMap<>());
      for (String ss : dTable.get(i).keySet()) {
        System.out.print(i);
        System.out.print(" ");
        System.out.print(ss + " ");
        System.out.print(dTable.get(i).get(ss));
        System.out.print("\n");

        Table.get(i).put(Pattern.compile(ss), dTable.get(i).get(ss));
      }
      //System.out.println();
    }

    for (int i : dfaFinalState) {
      System.out.print(i);
      System.out.print(" ");
    }
    System.out.println();
  }

  private Set<Integer> moveTch(String ch, Set<Integer> T) {
    Set<Integer> ret = new HashSet<>();
    for (Integer i : T) {
      for (String s : nfaTable.get(i).keySet()) {
        if (s.equals(ch)) {
          ret.addAll(nfaTable.get(i).get(s));
        }
      }
    }
    return ret;
  }

  private Set<Integer> e_closureS(Integer s) {
    String epsilon = "ε";
    // Compute e_closure(s0)
    Stack<Integer> st = new Stack<>();
    Set<Integer> e_closure = new HashSet<>();
    st.push(s);
    e_closure.add(s);
    while (!st.empty()) {
      Integer cur = st.pop();
      Map<String, Set<Integer>> ma = nfaTable.get(cur);
      for (String str : ma.keySet()) {
        if (str.contains(epsilon)) {
          Set<Integer> set = ma.get(str);
          for (Integer p : set) {
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

  private Set<Integer> e_closureT(Set<Integer> T) {
    Set<Integer> ret = new HashSet<>();
    Stack<Integer> st = new Stack<>();
    for (Integer i : T) {
      st.push(i);
      //ret.add(i);
    }
    while (!st.empty()) {
      Integer t = st.pop();
      System.out.println(t);
      Set<Integer> el = e_closureS(t);
      for (Integer u : el) {
        if (!ret.contains(u)) {
          ret.add(u);
          st.push(u);
        }
      }
    }
    return ret;
  }

  private Integer endConvert(Map<Integer, Boolean> DStates) {
    for (Integer s : DStates.keySet()) {
      if (!DStates.get(s)) {
        return s;
      }
    }
    return -1;
  }

  /**
   * To read the file and build NFA.
   *
   * @param file file path of the NFA file
   */
  public void readNFAFile(File file) {
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
          Collections.addAll(inputChar, words);
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
          if (words.length > 2) {
            nfaAttState.add(state);
          }
          nfaFinalState.add(state);

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
          if (set == null) {
            set = new HashSet<>();
          }
          set.add(outState);
          this.nfaTable.get(inState).put(pat, set);
        }
      }
      // Read const
      readConst(sc, line);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void readConst(Scanner sc, String line) {
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
          this.dTable.put(i, new HashMap<>());
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
          this.dTable.get(inState).put(pat, outState);
        }
      }
      // Read const
      readConst(sc, line);
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
  public List<String> Analyzer(String input, boolean isNfa) {
    input = input + "\n";
    List<String> ret = new ArrayList<>();
    List<String> error = new ArrayList<>();
    int len = input.length();
    Map<?, ?> use;
    if (isNfa) {
      use = dfaStateName;
    } else {
      use = stateName;
    }
    StringBuilder tmpp;
    int index = 1;
    int lastfinalin = -1;
    int lastfinalst = -1;
    for (int i = 0; i < len; i++) {
      char c = input.charAt(i);
      // Handle the wrong state
      if (!ReadChar(c)) {
        if (curState == 0) {
          String tmp = "Error with " + c + "\n";
          error.add(tmp);
          System.out.println(tmp);
          resetTable();
        } else {
          if (lastfinalin == -1) {
            String tmp = "Error with " + processedWord + c + "\n";
            error.add(tmp);
            System.out.println(tmp);
            resetTable();
            i -= 1;
          } else {
            String token = processedWord.substring(0,
                processedWord.length() + lastfinalin - i + 1);
            if (constValue.contains(token)) {
              tmpp = new StringBuilder(token + "\t<" + token.toUpperCase() + ", - >\n");
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            } else if (!attState.contains(lastfinalst)) {
              if (isNfa) {
                tmpp = new StringBuilder(token + "\t<");
                for (String ss : dfaStateName.get(lastfinalst)) {
                  tmpp.append(ss);
                }
                tmpp.append(", - >\n");
              } else {
                tmpp = new StringBuilder(token + "\t<" + use.get(lastfinalst) + ", - >\n");
              }
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            } else {
              if (isNfa) {
                tmpp = new StringBuilder(token + "\t<");
                for (String ss : dfaStateName.get(lastfinalst)) {
                  tmpp.append(ss);
                }
                tmpp.append(", ").append(token).append(">\n");
              } else {
                tmpp = new StringBuilder(
                    token + "\t<" + use.get(lastfinalst) + ", " + token + ">\n");
              }
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            }
            resetTable();
            i = lastfinalin;
            lastfinalin = -1;
            lastfinalst = -1;
          }
        }
      } else if (curState == 0) {
        resetTable();
      } else if (i == input.length() - 1) {
        String token = processedWord;
        //Object type = null;
        boolean type = false;
        if (isNfa) {
          List t = dfaStateName.get(curState);
          if(t != null){
            type = true;
          }
        } else {
          String t = stateName.get(curState);
          if(t != null){
            type = true;
          }
        }

        if (type) {
          if (constValue.contains(token)) {
            tmpp = new StringBuilder(token + "\t<" + token.toUpperCase() + ", - >\n");
            ret.add(tmpp.toString());
            System.out.println(tmpp);
          } else if (!attState.contains(lastfinalst)) {
            if (isNfa) {
              tmpp = new StringBuilder(token + "\t<");
              for (String ss : dfaStateName.get(lastfinalst)) {
                tmpp.append(ss);
              }
              tmpp.append(", - >\n");
            } else {
              tmpp = new StringBuilder(token + "\t<" + use.get(lastfinalst) + ", - >\n");
            }
            ret.add(tmpp.toString());
            System.out.println(tmpp);
          } else {
            if (isNfa) {
              tmpp = new StringBuilder(token + "\t<");
              for (String ss : dfaStateName.get(lastfinalst)) {
                tmpp.append(ss);
              }
              tmpp.append(", ").append(token).append(">\n");
            } else {
              tmpp = new StringBuilder(
                  token + "\t<" + use.get(lastfinalst) + ", " + token + ">\n");
            }
            ret.add(tmpp.toString());
            System.out.println(tmpp);
          }
        } else {
          if (lastfinalin == -1) {
            String tmp = "Error with " + processedWord + c + "\n";
            error.add(tmp);
            System.out.println(tmp);
            resetTable();
            i -= 1;
          } else {
            token = processedWord.substring(0,
                processedWord.length() + lastfinalin - i + 1);
            if (constValue.contains(token)) {
              tmpp = new StringBuilder(token + "\t<" + token.toUpperCase() + ", - >\n");
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            } else if (!attState.contains(lastfinalst)) {
              if (isNfa) {
                tmpp = new StringBuilder(token + "\t<");
                for (String ss : dfaStateName.get(lastfinalst)) {
                  tmpp.append(ss);
                }
                tmpp.append(", - >\n");
              } else {
                tmpp = new StringBuilder(token + "\t<" + use.get(lastfinalst) + ", - >\n");
              }
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            } else {
              if (isNfa) {
                tmpp = new StringBuilder(token + "\t<");
                for (String ss : dfaStateName.get(lastfinalst)) {
                  tmpp.append(ss);
                }
                tmpp.append(", ").append(token).append(">\n");
              } else {
                tmpp = new StringBuilder(
                    token + "\t<" + use.get(lastfinalst) + ", " + token + ">\n");
              }
              ret.add(tmpp.toString());
              System.out.println(tmpp);
            }
            resetTable();
            i = lastfinalin;
            lastfinalin = -1;
            lastfinalst = -1;
          }
        }
        resetTable();
      } else {
        if (c == '\n') {
          index += 1;
        }
        if(isNfa){
          if (dfaStateName.containsKey(curState)) {
            lastfinalin = i;
            lastfinalst = curState;
          }
        }else{
          if (stateName.containsKey(curState)) {
            lastfinalin = i;
            lastfinalst = curState;
          }
        }
      }
    }
    resetTable();
    ret.addAll(error);
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


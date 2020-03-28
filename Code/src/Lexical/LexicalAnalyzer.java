package Lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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

  public static final String inputFilePath = "inputFile/test.txt";
  public static final String wrongInputFilePath = "inputFile/wrong1.txt";
  public static final String dfaFilePath = "config/dfa.txt";
  public static final String nfaFilePath = "config/dfa.txt";//TODO: 改为nfa的路径

  public LexicalAnalyzer(File faFile, Boolean isNfa) {
    readDFAFile(faFile);
  }

  public static void main(String[] args) throws FileNotFoundException {

    File dfaFile = new File(dfaFilePath);
    LexicalAnalyzer la = new LexicalAnalyzer(dfaFile, false);
    File inputFile = new File(inputFilePath);
    Scanner sc = new Scanner(inputFile);
    StringBuilder input = new StringBuilder();
    while (sc.hasNextLine()) {
      input.append(sc.nextLine()).append("\n");
    }
    List<String> ret = la.Analyzer(input.toString());
    for (String s : ret) {
      System.out.print(s);
    }
    File inputFile2 = new File(wrongInputFilePath);
    sc = new Scanner(inputFile2);
    input = new StringBuilder();
    while (sc.hasNextLine()) {
      input.append(sc.nextLine()).append("\n");
    }
    ret = la.Analyzer(input.toString());
    System.out.println("WORK END");
    for (String s : ret) {
      System.out.print(s);
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
        int totStateNum = Integer.parseInt(line);
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
  public List<String> Analyzer(String input) {
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
        if (stateName.containsKey(curState)) {
          if (constValue.contains(processedWord)) {
            String tmp = processedWord + "\t<" + processedWord.toUpperCase() + ", - >\n";
            ret.add(tmp);
            System.out.println(tmp);
          } else if (!attState.contains(curState)) {
            String tmp = processedWord + "\t<" + stateName.get(curState) + ", - >\n";
            ret.add(tmp);
            System.out.println(tmp);
          } else {
            String tmp =
                processedWord + "\t<" + stateName.get(curState) + ", " + processedWord + ">\n";
            ret.add(tmp);
            System.out.println(tmp);
          }
        } else {
          String tmp = "Error at line " + index + " with " + processedWord + "\n";
          System.out.println(tmp);
        }
        resetTable();
        if (c == '\n') {
          index -= 1;
        }
        i -= 1;
      } else if (curState == 0) {
        resetTable();
      } else if (i == len - 1) {
        if (stateName.containsKey(curState)) {
          if (constValue.contains(processedWord)) {
            String tmp = processedWord + "\t<" + processedWord.toUpperCase() + ", - >\n";
            ret.add(tmp);
            System.out.println(tmp);
          } else if (!attState.contains(curState)) {
            String tmp = processedWord + "\t<" + stateName.get(curState) + ", - >\n";
            ret.add(tmp);
            System.out.println(tmp);
          } else {
            String tmp =
                processedWord + "\t<" + stateName.get(curState) + ", " + processedWord + ">\n";
            ret.add(tmp);
            System.out.println(tmp);
          }
        } else {
          String tmp = "Error at line " + index + " with " + processedWord + "\n";
          System.out.println(tmp);
        }
        resetTable();
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
   * To add a Jump Table row/tuple into the jump table.
   *
   * @param startState The start state
   * @param nextState The next
   * @param input input String
   */
  public void addTableRow(int startState, int nextState, String input) {
    Table.get(startState).put(Pattern.compile(input), nextState);
  }

  /**
   * To reset the Jump Table. Using this before reading a new DFA file!
   */
  public void resetTable() {
    curState = 0;
    processedWord = "";
  }
}

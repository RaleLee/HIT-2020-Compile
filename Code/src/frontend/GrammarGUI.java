package frontend;

import grammar.GrammarAnalyzer;
import grammar.PToken;
import grammar.Production;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javafx.util.Pair;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lexical.LexicalAnalyzer;

public class GrammarGUI extends BaseGUI {


  public GrammarGUI() {
  }

  public static void main(String[] args) {
    GrammarGUI grammarGUI = new GrammarGUI();
    grammarGUI.show("语法分析");
  }

  private static JFrame generateFirstAndFollow(GrammarAnalyzer analyzer) {
    List<String> nonTerSymbols = new ArrayList<>(analyzer.getNonTerminals());
    nonTerSymbols.sort(String::compareTo);

    Object[][] tempTable = new Object[nonTerSymbols.size()][3];
    List<String> columns = new ArrayList<>();
    columns.add("Symbol");
    columns.add("FIRST(Symbol)");
    columns.add("FOLLOW(Symbol)");
    Map<String, Set<String>> first = analyzer.getFirstSet();
    Map<String, Set<String>> follow = analyzer.getFollowSet();
    for (int ind = 0; ind < nonTerSymbols.size(); ind++) {
      String nonTerSymbol = nonTerSymbols.get(ind);
      tempTable[ind][0] = nonTerSymbol;
      if (first.containsKey(nonTerSymbol)) {
        tempTable[ind][1] = String.join(",", first.get(nonTerSymbol));
      } else {
        tempTable[ind][1] = "";
      }
      if (follow.containsKey(nonTerSymbol)) {
        tempTable[ind][2] = String.join(",", follow.get(nonTerSymbol));
      } else {
        tempTable[ind][2] = "";
      }
    }
    return generateFrameWithTable(tempTable, columns);
  }


  private static JFrame generateLL1Table(GrammarAnalyzer analyzer) {

    List<String> endSymbols = new ArrayList<>(analyzer.getEndSymbols());
    List<String> nonTerSymbols = new ArrayList<>(analyzer.getNonTerminals());
    endSymbols.sort(String::compareTo);
    nonTerSymbols.sort(String::compareTo);
    Object[][] tempTable = new Object[nonTerSymbols.size()][endSymbols.size() + 1];
    List<String> columns = new ArrayList<>();
    columns.add("Symbol");
    columns.addAll(endSymbols);
    Map<String, Map<String, Production>> LL1table = analyzer.getTable();
    for (int row = 0; row < nonTerSymbols.size(); row++) {
      String nonTerSymbol = nonTerSymbols.get(row);
      tempTable[row][0] = nonTerSymbol;
      for (int line = 1; line <= endSymbols.size(); line++) {
        tempTable[row][line] = LL1table.get(nonTerSymbol).get(columns.get(line));
      }
    }
    return generateFrameWithTable(tempTable, columns);
  }

  private static ImageIcon drawParseTree(Pair<List<PToken>, List<Integer>> treePair,
      GrammarAnalyzer analyzer) {
    GraphViz graphViz = new GraphViz("result", "lib\\graphviz\\bin\\dot.exe");
    graphViz.creatOrder();
    File imageFile = new File(graphViz.getSavePath());
    graphViz.start_graph();
    List<PToken> pTokens = new ArrayList<>(treePair.getKey());
    List<Integer> depths = new ArrayList<>(treePair.getValue());
    List<String> nodeLabels = new ArrayList<>();
    List<Boolean> checkSon = new ArrayList<>();
    for (int ind = 0; ind < pTokens.size() - 1; ind++) {
      PToken pToken = pTokens.get(ind);
      String label =
          pToken.token + (pToken.attribute != null ? ("\n(" + pToken.attribute) + ")" : "");
      nodeLabels.add(label);
      graphViz.addLn("node" + ind + "[label=\"" + label + "\"];");
      checkSon.add(false);
    }
    Stack<Integer> fathers = new Stack<>();
    fathers.push(0);
    //忽略$ 即pTokens的最后一个
    for (int ind = 1; ind < nodeLabels.size(); ind++) {
      Integer curDepth = depths.get(ind);
      String curNode = nodeLabels.get(ind);
      while (depths.get(fathers.peek()) >= curDepth) {
        fathers.pop();
      }
      checkSon.set(fathers.peek(), true);
      graphViz.addLn("node" + fathers.peek() + "->" + "node" + ind + ";");
      fathers.add(ind);
    }
    int epsilonCount = 0;
    for (int ind = 0; ind < nodeLabels.size(); ind++) {
      if (!checkSon.get(ind) && !analyzer.checkTokenIsTerminal(pTokens.get(ind))) {
        graphViz.addLn("epsilon" + epsilonCount + "[label=\"" + GrammarAnalyzer.epsilon + "\"];");
        graphViz.addLn("node" + ind + "->" + "epsilon" + (epsilonCount++) + ";");
      }
    }
    graphViz.end_graph();
    graphViz.run();
    ImageIcon retImageIcon = new ImageIcon(graphViz.getSavePath());
    retImageIcon.setImage(retImageIcon.getImage()
        .getScaledInstance(retImageIcon.getIconWidth(), retImageIcon.getIconHeight(),
            Image.SCALE_DEFAULT));
    return retImageIcon;
  }

  protected void placeComponents(JPanel panel) {
    /*
      输入部分
     */
    JLabel inputLabel = generateLabel("输入", xBlankLen, 0);
    panel.add(inputLabel);

    JTextArea inputTextArea = new JTextArea();
    inputTextArea.setFont(textAreaFont);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    inputScrollPane
        .setBounds(inputLabel.getX() + inputLabel.getWidth(), inputLabel.getY(), textAreaWidth,
            textAreaHeight);
    panel.add(inputScrollPane);

    /*
     输出部分
     */
    JLabel outputLabel = generateLabel("输出", xBlankLen, inputLabel.getY() + inputLabel.getHeight());
    panel.add(outputLabel);

    JTextArea outputTextArea = new JTextArea();
    outputTextArea.setFont(textAreaFont);
    JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
    outputScrollPane.setBounds(inputScrollPane.getX(),
        inputScrollPane.getY() + inputScrollPane.getHeight() + yBlankLen,
        textAreaWidth, textAreaHeight);
    panel.add(outputScrollPane);

    /*
    初始化语法分析器和FIRST FOLLOW LL1
     */
    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(new File(LexicalAnalyzer.dfaFilePath),
        false);
    GrammarAnalyzer grammarAnalyzer = new GrammarAnalyzer(new File(GrammarAnalyzer.grammarPath));
    JFrame firstAndFollow = generateFirstAndFollow(grammarAnalyzer);
    JFrame LL1Table = generateLL1Table(grammarAnalyzer);
    /*
     初始化显示树的label
     */
    JLabel treeLabel = new JLabel();
    treeLabel.setBounds(inputScrollPane.getX() + inputScrollPane.getWidth() + xBlankLen,
        inputScrollPane.getY(), textAreaWidth,
        textAreaHeight);
    treeLabel.setFont(labelFont);
    treeLabel.setVisible(false);
    panel.add(treeLabel);
    /*
    创建按钮，绑定事件
     */
    JButton grammarButton = new JButton("读取输入文件");
    grammarButton.setFont(buttonFont);
    grammarButton.setBounds(xBlankLen,
        outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    grammarButton.addActionListener(actionEvent -> {
      JFileChooser jFileChooser = new JFileChooser();
      jFileChooser.setCurrentDirectory(new File("inputFile"));
      jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jFileChooser.showDialog(new JLabel(), "选择文件");
      inputTextArea.setText(fileContent2String(jFileChooser.getSelectedFile()).trim());
    });
    panel.add(grammarButton);
    JButton grammarAnalyzeButton = new JButton("语法分析");
    grammarAnalyzeButton.setFont(buttonFont);
    grammarAnalyzeButton
        .setBounds(xBlankLen + grammarButton.getX() + grammarButton.getWidth(),
            outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    grammarAnalyzeButton.addActionListener(actionEvent -> {
      firstAndFollow.setVisible(true);
      LL1Table.setVisible(true);
      ImageIcon parseTree = drawParseTree(
          grammarAnalyzer.Analyzer(lexicalAnalyzer.Analyzer(inputTextArea.getText())),
          grammarAnalyzer);
      treeLabel.setIcon(parseTree);
      treeLabel.setBounds(treeLabel.getX(), treeLabel.getY(), parseTree.getIconWidth(),
          parseTree.getIconHeight());
      treeLabel.setVisible(true);
      outputTextArea.setText(String.join("\n", grammarAnalyzer.getStandardOut()));
    });
    panel.add(grammarAnalyzeButton);
  }
}

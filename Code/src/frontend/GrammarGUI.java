package frontend;

import grammar.GrammarAnalyzer;
import grammar.Production;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    columns.add("NonTerminalSymbol");
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
    JButton nfaLexicalButton = new JButton("语法分析");
    nfaLexicalButton.setFont(buttonFont);
    nfaLexicalButton
        .setBounds(xBlankLen + grammarButton.getX() + grammarButton.getWidth(),
            outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    nfaLexicalButton.addActionListener(actionEvent -> {
      firstAndFollow.setVisible(true);
      LL1Table.setVisible(true);
      grammarAnalyzer.Analyzer(lexicalAnalyzer.Analyzer(inputTextArea.getText()));
      outputTextArea.setText(String.join("\n", grammarAnalyzer.getStandardOut()));
    });
    panel.add(nfaLexicalButton);
  }
}

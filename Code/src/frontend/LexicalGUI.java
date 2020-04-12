package frontend;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import lexical.LexicalAnalyzer;

public class LexicalGUI extends BaseGUI {

  public LexicalGUI() {
  }

  public static void main(String[] args) {
    LexicalGUI lexicalGUI = new LexicalGUI();
    lexicalGUI.show("词法分析");
  }


  private static JFrame getTransTable(LexicalAnalyzer analyzer, boolean isNfa) {
    JFrame tempFrame = new JFrame();
    tempFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    tempFrame.setBounds(0, 0, 1920, 1080);

    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(null);
    List<String> faTable = isNfa ? analyzer.showNFA() : analyzer.showDFA();
    Set<String> transSet = new HashSet<>();
    Set<Integer> initStateSet = new HashSet<>();
    for (String faTrans : faTable) {
      List<String> triple = Arrays.asList(faTrans.trim().split(" "));
      initStateSet.add(Integer.parseInt(triple.get(0)));
      transSet.add(triple.get(1));
    }
    Object[][] transTable = new Object[Collections.max(initStateSet) + 1][transSet.size() + 1];
    List<String> transList = new LinkedList<>(transSet);
    transList.sort(String::compareTo);
    transList.add(0, "");
    Map<String, Integer> transMap = new HashMap<>();
    for (int ind = 0; ind < transList.size(); ind++) {
      transMap.put(transList.get(ind), ind);
    }
    for (String faTrans : faTable) {
      List<String> triple = Arrays.asList(faTrans.trim().split(" "));
      int initState = Integer.parseInt(triple.get(0));
      String trans = triple.get(1);
      String target = String.join("", triple.subList(2, triple.size()));
      transTable[initState][0] = initState;
      transTable[initState][transMap.get(trans)] = target;
    }
    JTable table = new JTable(transTable, transList.toArray());
    JScrollPane tableScrollPane = new JScrollPane(table);

    table.setVisible(true);
    tempFrame.add(tableScrollPane);
    tempFrame.setVisible(false);
    return tempFrame;
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
    DFA显示
     */
    JLabel dfaLabel = generateLabel("DFA转换描述:",
        inputScrollPane.getX() + inputScrollPane.getWidth() + xBlankLen,
        inputLabel.getY());
    panel.add(dfaLabel);

    JTextArea dfaTextArea = new JTextArea();
    dfaTextArea.setFont(textAreaFont);
    JScrollPane dfaScrollPane = new JScrollPane(dfaTextArea);
    dfaScrollPane.setBounds(dfaLabel.getX() + dfaLabel.getWidth() + xBlankLen,
        dfaLabel.getY(),
        textAreaWidth, textAreaHeight);
    panel.add(dfaScrollPane);

    /*
    NFA显示
     */
    JLabel nfaLabel = generateLabel("NFA转换描述:",
        outputScrollPane.getX() + outputScrollPane.getWidth() + xBlankLen,
        outputLabel.getY());
    panel.add(nfaLabel);

    JTextArea nfaTextArea = new JTextArea();
    nfaTextArea.setFont(textAreaFont);
    JScrollPane nfaScrollPane = new JScrollPane(nfaTextArea);
    nfaScrollPane.setBounds(nfaLabel.getX() + nfaLabel.getWidth() + xBlankLen,
        nfaLabel.getY(),
        textAreaWidth, textAreaHeight);
    panel.add(nfaScrollPane);
    nfaLabel.setVisible(false);
    nfaScrollPane.setVisible(false);

    /*
    初始化词法分析器和转换表
     */
    LexicalAnalyzer lexicalAnalyzerDFA = new LexicalAnalyzer(new File(LexicalAnalyzer.dfaFilePath),
        false);
    LexicalAnalyzer lexicalAnalyzerNFA = new LexicalAnalyzer(new File(LexicalAnalyzer.nfaFilePath),
        true);
    JFrame dfaAnalyzerDfaTransTable = getTransTable(lexicalAnalyzerDFA, false);
    dfaAnalyzerDfaTransTable.setTitle("词法分析（DFA）DFA转换表");
    JFrame nfaAnalyzerDfaTransTable = getTransTable(lexicalAnalyzerNFA, false);
    nfaAnalyzerDfaTransTable.setTitle("词法分析（NFA）DFA转换表");
    JFrame nfaAnalyzerNfaTransTable = getTransTable(lexicalAnalyzerNFA, true);
    nfaAnalyzerNfaTransTable.setTitle("词法分析（NFA）NFA转换表");
    /*
    创建按钮，绑定事件
     */
    JButton dfaLexicalButton = new JButton("词法分析（DFA）");
    dfaLexicalButton.setFont(buttonFont);
    dfaLexicalButton.setBounds(xBlankLen,
        outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    dfaLexicalButton.addActionListener(actionEvent -> {
      nfaLabel.setVisible(false);
      nfaScrollPane.setVisible(false);
      dfaAnalyzerDfaTransTable.setVisible(true);
      List<String> dfaStatus = lexicalAnalyzerDFA.Analyzer(inputTextArea.getText(), false);
      outputTextArea
          .setText(String.join("", dfaStatus));
      dfaTextArea.setText(String.join("", lexicalAnalyzerDFA.showDFA()));
    });
    panel.add(dfaLexicalButton);

    JButton nfaLexicalButton = new JButton("词法分析（NFA）");
    nfaLexicalButton.setFont(buttonFont);
    nfaLexicalButton
        .setBounds(xBlankLen + dfaLexicalButton.getX() + dfaLexicalButton.getWidth(),
            outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    nfaLexicalButton.addActionListener(actionEvent -> {
      nfaLabel.setVisible(true);
      nfaScrollPane.setVisible(true);
      nfaAnalyzerDfaTransTable.setVisible(true);
      nfaAnalyzerNfaTransTable.setVisible(true);
      List<String> nfaStatus = lexicalAnalyzerNFA.Analyzer(inputTextArea.getText(), true);
      outputTextArea
          .setText(String.join("", nfaStatus));
      dfaTextArea.setText(String.join("", lexicalAnalyzerNFA.showDFA()));
      nfaTextArea.setText(String.join("", lexicalAnalyzerNFA.showNFA()));
    });
    panel.add(nfaLexicalButton);
  }
}

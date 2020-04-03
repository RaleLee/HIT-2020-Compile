package frontend;

import java.awt.Font;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lexical.LexicalAnalyzer;

public class GUI {

  public static final int xBlankLen = 80;//水平间隔长度
  public static final int yBlankLen = 20;//竖直间隔长度
  public static final int textAreaWidth = xBlankLen * 6;
  public static final int textAreaHeight = yBlankLen * 15;
  public static final int labelWidth = xBlankLen * 2;
  public static final Font labelFont = new Font("微软雅黑", Font.PLAIN, 30);
  public static final Font buttonFont = new Font("微软雅黑", Font.BOLD, 25);
  public static final int buttonWidth = xBlankLen * 3;
  public static final int buttonHeight = yBlankLen * 3;

  public static void main(String[] args) {
    // 创建 JFrame 实例
    JFrame frame = new JFrame("编译器前端");
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setBounds(0, 0, 1920, 1080);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // 添加 JPanel
    JPanel panel = new JPanel();
    frame.add(panel);
    // 放置组件
    placeComponents(panel);
    // 设置界面可见
    frame.setVisible(true);
  }

  private static JLabel generateLabel(String labelName, int labelX, int labelY) {
    JLabel retLabel = new JLabel(labelName);
    retLabel.setFont(labelFont);
    retLabel.setBounds(labelX, labelY, labelWidth, textAreaHeight);
    return retLabel;
  }

  private static void placeComponents(JPanel panel) {

    /* 布局部分我们这边不多做介绍
     * 这边设置布局为 null
     */
    panel.setLayout(null);

    /*
      输入部分
     */
    JLabel inputLabel = generateLabel("输入", xBlankLen, 0);
    panel.add(inputLabel);

    JTextArea inputTextArea = new JTextArea();
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
    JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
    outputScrollPane.setBounds(inputScrollPane.getX(),
        inputScrollPane.getY() + inputScrollPane.getHeight() + yBlankLen,
        textAreaWidth, textAreaHeight);
    panel.add(outputScrollPane);

    /*
    DFA显示
     */
    JLabel dfaLabel = generateLabel("DFA转换表:",
        inputScrollPane.getX() + inputScrollPane.getWidth() + xBlankLen,
        inputLabel.getY());
    panel.add(dfaLabel);

    JTextArea dfaTextArea = new JTextArea();
    JScrollPane dfaScrollPane = new JScrollPane(dfaTextArea);
    dfaScrollPane.setBounds(dfaLabel.getX() + dfaLabel.getWidth() + xBlankLen,
        dfaLabel.getY(),
        textAreaWidth, textAreaHeight);
    panel.add(dfaScrollPane);

    /*
    NFA显示
     */
    JLabel nfaLabel = generateLabel("NFA转换表:",
        outputScrollPane.getX() + outputScrollPane.getWidth() + xBlankLen,
        outputLabel.getY());
    panel.add(nfaLabel);

    JTextArea nfaTextArea = new JTextArea();
    JScrollPane nfaScrollPane = new JScrollPane(nfaTextArea);
    nfaScrollPane.setBounds(nfaLabel.getX() + nfaLabel.getWidth() + xBlankLen,
        nfaLabel.getY(),
        textAreaWidth, textAreaHeight);
    panel.add(nfaScrollPane);
    nfaLabel.setVisible(false);
    nfaScrollPane.setVisible(false);

    /*
    初始化词法分析器
     */
    LexicalAnalyzer lexicalAnalyzerDFA = new LexicalAnalyzer(new File(LexicalAnalyzer.dfaFilePath),
        false);
    LexicalAnalyzer lexicalAnalyzerNFA = new LexicalAnalyzer(new File(LexicalAnalyzer.nfaFilePath),
        true);
    /*
    创建按钮，绑定事件
     */
    JButton dfaLexicalButton = new JButton("语法分析（DFA）");
    dfaLexicalButton.setFont(buttonFont);
    dfaLexicalButton.setBounds(xBlankLen,
        outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    dfaLexicalButton.addActionListener(actionEvent -> {
      nfaLabel.setVisible(false);
      nfaScrollPane.setVisible(false);
      System.out.println(lexicalAnalyzerDFA.Analyzer(inputTextArea.getText(), false));
      outputTextArea
          .setText(String.join("", lexicalAnalyzerDFA.Analyzer(inputTextArea.getText(), false)));
      dfaTextArea.setText(String.join("", lexicalAnalyzerDFA.showDFA()));
    });
    panel.add(dfaLexicalButton);

    JButton nfaLexicalButton = new JButton("语法分析（NFA）");
    nfaLexicalButton.setFont(buttonFont);
    nfaLexicalButton
        .setBounds(xBlankLen + dfaLexicalButton.getX() + dfaLexicalButton.getWidth(),
            outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    nfaLexicalButton.addActionListener(actionEvent -> {
      nfaLabel.setVisible(true);
      nfaScrollPane.setVisible(true);
      outputTextArea
          .setText(String.join("", lexicalAnalyzerNFA.Analyzer(inputTextArea.getText(), true)));
      dfaTextArea.setText(String.join("", lexicalAnalyzerNFA.showDFA()));
      nfaTextArea.setText(String.join("", lexicalAnalyzerNFA.showNFA()));
    });
    panel.add(nfaLexicalButton);

  }
}

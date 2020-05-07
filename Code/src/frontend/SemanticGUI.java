package frontend;

import grammar.GrammarAnalyzer;
import grammar.PToken;
import java.io.File;
import java.util.List;
import javafx.util.Pair;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lexical.LexicalAnalyzer;
import semantic.SemanticAnalyzer;

public class SemanticGUI extends BaseGUI {

  @Override
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
    创建按钮，绑定事件
     */
    JButton inputFileButton = new JButton("读取输入文件");
    inputFileButton.setFont(buttonFont);
    inputFileButton.setBounds(xBlankLen,
        outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    inputFileButton.addActionListener(actionEvent -> {
      JFileChooser jFileChooser = new JFileChooser();
      jFileChooser.setCurrentDirectory(new File("inputFile"));
      jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jFileChooser.showDialog(new JLabel(), "选择文件");
      inputTextArea.setText(fileContent2String(jFileChooser.getSelectedFile()).trim());
    });
    panel.add(inputFileButton);
    //初始化各个分析器
    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(new File(LexicalAnalyzer.dfaFilePath),
        false);
    GrammarAnalyzer grammarAnalyzer = new GrammarAnalyzer(new File(SemanticAnalyzer.grammarPath));
    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    Pair<List<PToken>, List<Integer>> tempPair = grammarAnalyzer
        .Analyzer(lexicalAnalyzer.Analyzer(inputTextArea.getText()));
    semanticAnalyzer.analyzer(tempPair, grammarAnalyzer);

    JButton grammarAnalyzeButton = new JButton("生成三地址码和四元式");
    grammarAnalyzeButton.setFont(buttonFont);
    grammarAnalyzeButton
        .setBounds(xBlankLen + inputFileButton.getX() + inputFileButton.getWidth(),
            outputLabel.getY() + outputLabel.getHeight() + yBlankLen, buttonWidth, buttonHeight);
    grammarAnalyzeButton.addActionListener(actionEvent -> {
      outputTextArea.setText(String.join("\n", semanticAnalyzer.getResults()));
    });
    panel.add(grammarAnalyzeButton);
  }
}

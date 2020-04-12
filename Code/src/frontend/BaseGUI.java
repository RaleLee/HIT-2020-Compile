package frontend;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public abstract class BaseGUI {

  public static final int xBlankLen = 80;//水平间隔长度
  public static final int yBlankLen = 20;//竖直间隔长度
  public static final int textAreaWidth = xBlankLen * 6;
  public static final int textAreaHeight = yBlankLen * 15;
  public static final int labelWidth = xBlankLen * 2;
  public static final Font labelFont = new Font("微软雅黑", Font.PLAIN, 25);
  public static final Font buttonFont = new Font("微软雅黑", Font.BOLD, 25);
  public static final Font textAreaFont = new Font("黑体", Font.PLAIN, 20);
  public static final Font tableFont = new Font("黑体", Font.BOLD, 20);
  public static final int buttonWidth = xBlankLen * 3;
  public static final int buttonHeight = yBlankLen * 3;

  public static String fileContent2String(File file) {
    StringBuilder result = new StringBuilder();
    try {
      BufferedReader bufferedReader = new BufferedReader(
          new FileReader(file));//构造一个BufferedReader类来读取文件
      String s;
      while ((s = bufferedReader.readLine()) != null) {//使用readLine方法，一次读一行
        result.append(System.lineSeparator()).append(s);
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  protected static void fitTableColumns(JTable table) {
    JTableHeader header = table.getTableHeader();
    int rowCount = table.getRowCount();
    Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
    while (columns.hasMoreElements()) {
      TableColumn column = columns.nextElement();
      int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
      int width = (int) table.getTableHeader().getDefaultRenderer()
          .getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col)
          .getPreferredSize().getWidth();
      for (int row = 0; row < rowCount; row++) {
        int preferedWidth = (int) table.getCellRenderer(row, col)
            .getTableCellRendererComponent(table,
                table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
        width = Math.max(width, preferedWidth);
      }
      header.setResizingColumn(column); // 此行很重要
      column.setWidth(width + table.getIntercellSpacing().width);
    }
  }

  protected static JFrame generateFrameWithTable(Object[][] tempTable, List<String> columns) {
    JFrame tempFrame = new JFrame();
    tempFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    tempFrame.setBounds(0, 0, 1920, 1080);

    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(null);
    JTable table = new JTable(tempTable, columns.toArray());
    table.setFont(tableFont);
    table.setRowHeight(40);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fitTableColumns(table);

    JScrollPane tableScrollPane = new JScrollPane(table);

    table.setVisible(true);
    tempFrame.add(tableScrollPane);
    tempFrame.setVisible(false);
    return tempFrame;
  }

  protected static JLabel generateLabel(String labelName, int labelX, int labelY) {
    JLabel retLabel = new JLabel(labelName);
    retLabel.setFont(labelFont);
    retLabel.setBounds(labelX, labelY, labelWidth, textAreaHeight);
    return retLabel;
  }

  public void show(String title) {

    // 创建 JFrame 实例
    JFrame frame = new JFrame(title);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setBounds(0, 0, 1920, 1080);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // 添加 JPanel
    JPanel panel = new JPanel();
    panel.setLayout(null);

    frame.add(panel);
    // 放置组件
    placeComponents(panel);
    // 设置界面可见
    frame.setVisible(true);
  }

  protected abstract void placeComponents(JPanel panel);
}

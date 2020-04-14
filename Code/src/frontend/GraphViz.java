package frontend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class GraphViz {

  private final String dotCodeFile = "dotCode.txt";
  private final StringBuilder graph = new StringBuilder();
  private final String runPath;
  private final String dotPath;
  Runtime runtime = Runtime.getRuntime();
  private String runOrder = "";
  private String savePath = "";

  public GraphViz(String runPath, String dotPath) {
    this.runPath = runPath;
    this.dotPath = dotPath;
  }

  public String getSavePath() {
    return savePath;
  }

  public void run() {
    File file = new File(runPath);
    assert file.exists() || file.mkdirs();
    writeGraphToFile(graph.toString(), runPath);
    try {
      Process process = runtime.exec(runOrder);
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void creatOrder() {
    runOrder += dotPath + " ";
    runOrder += runPath;
    runOrder += "\\" + dotCodeFile + " ";
    runOrder += "-T jpg ";
    runOrder += "-o ";
    runOrder += runPath;
    String resultGif = "dotResult";
    runOrder += "\\" + resultGif + ".jpg";
    savePath = runPath + "\\" + resultGif + ".jpg";
    System.out.println(runOrder);
  }

  public void writeGraphToFile(String dotCode, String filename) {
    try {
      File file = new File(filename + "\\" + dotCodeFile);
      assert file.exists() || file.createNewFile();
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(dotCode.getBytes());
      fos.close();
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void add(String line) {
    graph.append("\t").append(line);
  }

  public void addLn(String line) {
    graph.append("\t").append(line).append("\n");
  }

  public void start_graph() {
    graph.append("digraph G {\n");
  }

  public void end_graph() {
    graph.append("}");
  }
}

package grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Production {

  public List<String> trueRight = new ArrayList<>();
  public List<String> actions = new ArrayList<>();
  // Left part of production
  private String left = null;
  // Right part of the production
  private List<String> right = null;
  private String sync = null;

  /**
   * The constructor of Production
   *
   * @param left String the left part of the production
   * @param right List<String> the right part of the production
   */
  public Production(String left, List<String> right) {
    this.left = left;
    Iterator<String> iterator = right.iterator();
    while (iterator.hasNext()) {
      String temp = iterator.next();
      this.trueRight.add(temp);
      if (temp.contains("#a")) {
        this.actions.add(temp);
        iterator.remove();
      }
    }
    this.right = right;
  }

  public Production() {
    this.sync = "sync";
  }

  public static Production findProduction(List<Production> productions, String left,
      List<String> right) {
    for (Production production : productions) {
      //list equals maybe ok
      if (production.left.equals(left) && production.right.equals(right)) {
        return production;
      }
    }
    System.out.println("DEBUG PRODUCTION:");
    System.out.println(left);
    System.out.println(right);
    System.out.println("ERROR:NO PRODUCTION!ERROR TREE MAYBE");
    System.exit(0);
    return null;
  }

  public boolean getSync() {
    return this.sync != null;
  }

  @Override
  public int hashCode() {
    int code = left.hashCode() * 10;
    for (String s : right) {
      code += s.hashCode() * 5;
    }
    return code;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (obj instanceof Production) {
      Production that = (Production) obj;
      return that.getLeft().equals(this.left) && that.getRight().equals(this.right);
    }
    return false;
  }

  @Override
  public String toString() {
    if (sync != null) {
      return sync;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(this.left);
    sb.append(" -> ");
    for (String s : right) {
      sb.append(s).append(" ");
    }
    return sb.toString();
  }

  /**
   * The getter of left
   *
   * @return String, left
   */
  public String getLeft() {
    return left;
  }

  /**
   * The getter of right
   *
   * @return List<String> the right
   */
  public List<String> getRight() {
    return right;
  }
}

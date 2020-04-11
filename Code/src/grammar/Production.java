package grammar;

import java.util.List;

public class Production {

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
        this.right = right;
    }

    public Production() {
        this.sync = "sync";
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
        if(obj == null)
            return false;
        if(this == obj)
            return true;
        if(obj instanceof Production){
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
     * @return String, left
     */
    public String getLeft() {
        return left;
    }

    /**
     * The getter of right
     * @return List<String> the right
     */
    public List<String> getRight() {
        return right;
    }
}

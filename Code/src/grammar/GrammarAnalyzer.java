package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GrammarAnalyzer {

    public static String epsilon = "ε";
    public static String end = "$";
    // Use LL1

    private Stack<String> analyzer = new Stack<>();

    private Stack<Integer> treeDepth = new Stack<>();

    private List<Production> productions = new ArrayList<>();

    // Predict table
    private Map<String, Map<String, Production>> Table = new HashMap<>();
    // Start symbol
    private String start;
    // End symbol
    private Set<String> endSymbols = new HashSet<>();
    // Non Terminal symbols
    private Set<String> nonTerminals = new HashSet<>();
    // epsilon set
    private Set<String> epsilonSymbols = new HashSet<>();
    // First set
    private Map<String, Set<String>> firstSet = new HashMap<>();
    // Follow set
    private Map<String, Set<String>> followSet = new HashMap<>();
    // Select set
    private Map<Production, Set<String>> select = new HashMap<>();


    public static void main(String[] args) throws Exception{
        List<Token> test = new ArrayList<>();
        test.add(new Token("(", null));
        test.add(new Token("id", null));
        test.add(new Token("+", null));
        test.add(new Token("id", null));
        test.add(new Token(")", null));
        test.add(new Token("*", null));
        test.add(new Token("id", null));
        test.add(new Token("$", null));

        GrammarAnalyzer ga = new GrammarAnalyzer(new File("config/grammar.txt"));
        ga.Analyzer(test);
    }

    /**
     *  The constructor of Grammar Analyzer
     *
     * @param file the configure file of grammar analyzer.
     */
    public GrammarAnalyzer(File file){
        readFile(file);
        // Construct epsilon set
        creEpsilonSet();
        // Construct first set
        creFirstSet();
        // Construct follow set
        creFollowSet();
        // Construct select set
        creSelectSet();
        // Construct Table
        creTable();
    }

    public void Analyzer(List<Token> lexicalOut){
        reset();
        int index = 0;
        List<String> outS = new ArrayList<>();
        List<Integer> outH = new ArrayList<>();

        while(!analyzer.empty()){
            // print now
            for(String s : analyzer){
                System.out.print(s + " ");
            }
            System.out.print("|");
            for(int i = index; i < lexicalOut.size(); i++){
                System.out.print(lexicalOut.get(i).getSpName() + " ");
            }
            System.out.println();

            String curGra = analyzer.pop();
            int depth = treeDepth.pop();
            String curLex = lexicalOut.get(index).getSpName();
            if(curGra.equals(curLex)){
                index++;
                outH.add(depth);
                outS.add(curGra);
            } else if(endSymbols.contains(curGra) || curGra.equals(end)){
                handleWrongState();
                System.out.println("Wrong!");
            } else if(Table.get(curGra).containsKey(curLex)){
                List<String> right = Table.get(curGra).get(curLex).getRight();
                int size = right.size();
                if(right.get(0).equals(epsilon)){
                    outH.add(depth);
                    outS.add(curGra);
                    continue;
                }
                for(int i = size-1; i >= 0; i--){
                    analyzer.push(right.get(i));
                    treeDepth.push(depth+1);
                }
                outH.add(depth);
                outS.add(curGra);
            } else {
                handleWrongState();
                System.out.println("Wrong!");
            }

        }

        // Print the tree
        System.out.println(outS.get(0) + " (" + outH.get(0) + ")");
        for(int i = 0; i < outS.size()-1; i++){
            for(int j = 0; j < outH.get(i); j++){
                if(j == outH.get(i) - 1){
                    System.out.print("┗----");
                    System.out.println(outS.get(i) + " (" + outH.get(i) + ")");
                }else {
                    boolean isbrother = false;
                    for(int k = i; k < outS.size(); k++){
                        if(outH.get(k) < j+1){
                            break;
                        } else if(outH.get(k) == j+1){
                            isbrother = true;
                            break;
                        }
                    }
                    if(isbrother){
                        System.out.print("|    ");
                    }else{
                        System.out.print("     ");
                    }
                }
            }
        }
    }


    private void reset(){
        analyzer = new Stack<>();
        treeDepth = new Stack<>();
        analyzer.push(end);
        analyzer.push(start);
        treeDepth.push(-1);
        treeDepth.push(0);
    }

    private void handleWrongState(){
        // TODO: handle wrong state
        throw new RuntimeException("Need construct");
    }

    /**
     * Find those symbols that will create epsilon
     */
    private void creEpsilonSet(){
        for(Production pro : productions){
            List<String> ri = pro.getRight();
            if(ri.size() == 1 && ri.get(0).equals(epsilon))
                epsilonSymbols.add(pro.getLeft());
        }
        boolean isUpdate = true;
        while(isUpdate){
            isUpdate = false;
            for(Production pro : productions){
                List<String> ri = pro.getRight();
                boolean isFind = true;
                for(String s : ri) {
                    // Any symbol not
                    if (!epsilonSymbols.contains(s)) {
                        isFind = false;
                        break;
                    }
                }
                if(isFind){
                    if(!epsilonSymbols.contains(pro.getLeft())){
                        isUpdate = true;
                        epsilonSymbols.add(pro.getLeft());
                    }
                }
            }
        }
    }

    private void creFirstSet(){
        // init
        for(String s : nonTerminals){
            firstSet.put(s, new HashSet<>());
        }
        // Add direct symbol
        for(Production pro : productions){
            List<String> right = pro.getRight();
            if(endSymbols.contains(right.get(0))){
                firstSet.get(pro.getLeft()).add(right.get(0));
            }
        }
        boolean isUpdate = true;
        while(isUpdate){
            isUpdate = false;
            for(Production pro : productions){
                String left = pro.getLeft();
                List<String> right = pro.getRight();
                for(String s : right){
                    // if is terminal symbol
                    if(endSymbols.contains(s)){
                        if(!firstSet.get(left).contains(s)){
                            firstSet.get(left).add(s);
                            isUpdate = true;
                        }
                        break;
                    }
                    // if is non terminal symbol
                    else if(nonTerminals.contains(s)){
                        if(!firstSet.get(left).containsAll(firstSet.get(s))){
                            firstSet.get(left).addAll(firstSet.get(s));
                            isUpdate = true;
                        }
                        if(!epsilonSymbols.contains(s))
                            break;
                    }
                }
            }
        }
    }

    private void creFollowSet(){
        // A temp Map to save the follow set
        Map<String, Set<String>> tmp = new HashMap<>();
        // init
        for(String s : nonTerminals){
            followSet.put(s, new HashSet<>());
            tmp.put(s, new HashSet<>());
        }
        // put $ into Start Symbol follow set
        followSet.get(start).add(end);

        for(Production pro : productions){
            String left = pro.getLeft();
            List<String> right = pro.getRight();
            int size = right.size();
            for(int i = 0; i < size; i++){
                if(i < size - 1){
                    // 如果存在一个产生式A→αBβ，那么FIRST(β)中除ε之外的所有符号都在FOLLOW(B)中
                    if(nonTerminals.contains(right.get(i))){
                        for(int j = i+1; j < size; j++){
                            if(nonTerminals.contains(right.get(j))){
                                followSet.get(right.get(i)).addAll(firstSet.get(right.get(j)));
                                if(epsilonSymbols.contains(right.get(j))){
                                    if(j == size - 1)
                                        tmp.get(right.get(i)).add(left);
                                    continue;
                                }
                            } else if(endSymbols.contains(right.get(j))){
                                followSet.get(right.get(i)).add(right.get(j));
                            }
                            break;
                        }
                    }
                } else if(nonTerminals.contains(right.get(i))){ // last symbol is non-terminal
                    tmp.get(right.get(i)).add(left);
                }
            }
        }

        boolean isUpdate = true;
        while(isUpdate){
            isUpdate = false;
            for(String s : tmp.keySet()){
                Set<String> tmpp = tmp.get(s);
                for(String ss : tmpp){
                    if(!followSet.get(s).containsAll(followSet.get(ss))){
                        followSet.get(s).addAll(followSet.get(ss));
                        isUpdate = true;
                    }
                }
            }
        }
    }

    private void creSelectSet(){
        for(Production pro : productions){
            boolean isEpsilon = true;
            select.put(pro, new HashSet<>());
            String left = pro.getLeft();
            List<String> right = pro.getRight();
            for(String s : right){
                // 如果ε不属于FIRST(α),那么SELECT(A→α)= FIRST(α)
                if(nonTerminals.contains(s)){
                    select.get(pro).addAll(firstSet.get(s));
                    if(epsilonSymbols.contains(s))
                        continue;
                    else {
                        isEpsilon = false;
                        break;
                    }
                } else if(endSymbols.contains(s)){
                    select.get(pro).add(s);
                    isEpsilon = false;
                    break;
                } else { //如果 ε∈FIRST(α), 那么SELECT(A→α)=( FIRST(α)-{ε} )∪FOLLOW(A)
                    select.get(pro).addAll(followSet.get(left));
                    isEpsilon = false;
                    break;
                }
            }
            if(isEpsilon){
                select.get(pro).addAll(followSet.get(left));
            }
        }
    }

    private void creTable(){
        for(String s : nonTerminals){
            Table.put(s, new HashMap<>());
        }

        for(Production pro : select.keySet()){
            String left = pro.getLeft();
            Set<String> sel = select.get(pro);
            for(String s : sel){
                Table.get(left).put(s, pro);
            }
        }
    }

    /**
     *
     * @param file
     */
    private void readFile(File file){
        Scanner sc;
        try{
            sc = new Scanner(file);
            String line = sc.nextLine();
            // Read start symbol
            if(line.contains("====")){
                line = sc.nextLine();
                start = line;
            }
            line = sc.nextLine();
            // Read end symbol
            if(line.contains("====")){
                line = sc.nextLine();
                String[] ends = line.split(" ");
                endSymbols.addAll(Arrays.asList(ends));
            }
            line = sc.nextLine();
            // Read Non-terminal
            if(line.contains("====")){
                line = sc.nextLine();
                String[] nons = line.split(" ");
                nonTerminals.addAll(Arrays.asList(nons));
            }
            line = sc.nextLine();
            // Read production
            if(line.contains("====")){
                while(sc.hasNextLine()){
                    line = sc.nextLine();
                    String[] pro = line.split("->");
                    // left part
                    String left = pro[0].strip();
                    // right part
                    String[] rights = pro[1].strip().split(" ");
                    List<String> right = new ArrayList<>();
                    for (String s : rights) {
                        if (s.equals("|")) {
                            productions.add(new Production(left, right));
                            right = new ArrayList<>();
                        } else {
                            right.add(s);
                        }
                    }
                    productions.add(new Production(left, right));
                }
            }
            sc.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

}

import java.util.*;
import java.io.PrintWriter;

public class SemanticAnalyzer {
    private final SymbolTable symbolTable;

    public SemanticAnalyzer(SymbolTable st) {
        this.symbolTable = st;
    }

    // Main entry point for analysis
    public void analyze(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            evaluate(node);
        }
    }

    private int evaluate(ASTNode node) {
        if (node instanceof NumberNode) {
            return ((NumberNode) node).value;
        }

        if (node instanceof VarNode) {
            String name = ((VarNode) node).name;
            if (!symbolTable.contains(name)) {
                throw new RuntimeException("Semantic Error: Variable '" + name + "' used before declaration.");
            }
            return symbolTable.get(name);
        }

        if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            int left = evaluate(binOp.left);
            int right = evaluate(binOp.right);

            switch (binOp.operator) {
                case "+": return left + right;
                case "-": return left - right;
                case "*": return left * right;
                case "/": 
                    if (right == 0) throw new RuntimeException("Semantic Error: Division by zero.");
                    return left / right;
            }
        }

        if (node instanceof AssignNode) {
            AssignNode assign = (AssignNode) node;
            int value = evaluate(assign.expr);
            symbolTable.set(assign.name, value);
            return value;
        }

        return 0;
    }

    public static String convertEnglishToBangla(int number) {
        String engNum = String.valueOf(number);
        StringBuilder sb = new StringBuilder();
        for (char c : engNum.toCharArray()) {
            if (c == '-') sb.append('-'); // Handle negative results
            else sb.append((char) (c - '0' + '০'));
        }
        return sb.toString();
    }
    public static int convertBanglaToEnglish(String bNum) {
        StringBuilder sb = new StringBuilder();
        for (char c : bNum.toCharArray()) sb.append((char) (c - '০' + '0'));
        return Integer.parseInt(sb.toString());
    }
}
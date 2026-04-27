public abstract class ASTNode {}

class NumberNode extends ASTNode {
    int value;
    NumberNode(int value) { this.value = value; }
}

class VarNode extends ASTNode {
    String name;
    VarNode(String name) { this.name = name; }
}

class BinOpNode extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;
    BinOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left; this.operator = operator; this.right = right;
    }
}

class AssignNode extends ASTNode {
    String name;
    ASTNode expr;
    AssignNode(String name, ASTNode expr) { this.name = name; this.expr = expr; }
}
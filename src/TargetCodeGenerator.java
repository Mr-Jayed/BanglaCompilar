import java.util.List;
import java.util.ArrayList;

public class TargetCodeGenerator {

    
    public String generatePythonCode(List<Instruction> instructions) {
        StringBuilder pyCode = new StringBuilder();
        pyCode.append("# Auto-generated Python Target Code from Bangla Compiler\n\n");

        int indent = 0;
        List<String> userVars = new ArrayList<>();

        for (int i = 0; i < instructions.size(); i++) {
            Instruction inst = instructions.get(i);
            String pad = "    ".repeat(indent);

            switch (inst.type) {
                case "LABEL":
                    break;
                case "GOTO":
                    break;
                case "IF_FALSE_GOTO":
                    pyCode.append(pad).append("if ").append(mapValue(inst.arg1)).append(":\n");
                    indent++;
                    break;
                case "PRINT":
                    pyCode.append(pad).append("print(").append(mapValue(inst.arg1)).append(")\n");
                    break;
                default:
                    String left = mapValue(inst.arg1);
                    if (inst.operator == null) {
                        pyCode.append(pad).append(inst.result).append(" = ").append(left).append("\n");
                    } else {
                        String right = mapValue(inst.arg2);
                        String op = inst.operator;
                        pyCode.append(pad).append(inst.result).append(" = ").append(left).append(" ").append(op).append(" ").append(right).append("\n");
                    }
                    if (inst.result != null && !inst.result.matches("t\\d+") && !userVars.contains(inst.result)) {
                        userVars.add(inst.result);
                    }
                    break;
            }
        }

        
        pyCode.append("\n# Print final state of variables\n");
        pyCode.append("print('--- Execution Output ---')\n");
        for (String var : userVars) {
            pyCode.append("print('").append(var).append(":', ").append(var).append(")\n");
        }

        return pyCode.toString();
    }

    
    public String generatePythonFromAST(List<ASTNode> nodes) {
        StringBuilder pyCode = new StringBuilder();
        pyCode.append("# Auto-generated Python Target Code from Bangla Compiler\n\n");
        List<String> userVars = new ArrayList<>();
        generateBlock(nodes, 0, pyCode, userVars);

        pyCode.append("\n# Print final state of variables\n");
        pyCode.append("print('--- Execution Output ---')\n");
        for (String var : userVars) {
            pyCode.append("print('").append(var).append(":', ").append(var).append(")\n");
        }

        return pyCode.toString();
    }

    private void generateBlock(List<ASTNode> nodes, int indent, StringBuilder pyCode, List<String> userVars) {
        for (ASTNode node : nodes) {
            String pad = "    ".repeat(indent);
            if (node instanceof AssignNode) {
                AssignNode assign = (AssignNode) node;
                String expr = generateExpression(assign.expr);
                pyCode.append(pad).append(assign.name).append(" = ").append(expr).append("\n");
                if (!userVars.contains(assign.name)) {
                    userVars.add(assign.name);
                }
            } else if (node instanceof IfNode) {
                IfNode ifNode = (IfNode) node;
                String cond = generateExpression(ifNode.condition);
                pyCode.append(pad).append("if ").append(cond).append(":\n");
                generateBlock(ifNode.thenBody, indent + 1, pyCode, userVars);
                if (ifNode.elseBody != null) {
                    pyCode.append(pad).append("else:\n");
                    generateBlock(ifNode.elseBody, indent + 1, pyCode, userVars);
                }
            } else if (node instanceof WhileNode) {
                WhileNode whileNode = (WhileNode) node;
                String cond = generateExpression(whileNode.condition);
                pyCode.append(pad).append("while ").append(cond).append(":\n");
                generateBlock(whileNode.body, indent + 1, pyCode, userVars);
            } else if (node instanceof PrintNode) {
                PrintNode printNode = (PrintNode) node;
                String expr = generateExpression(printNode.expr);
                pyCode.append(pad).append("print(").append(expr).append(")\n");
            }
        }
    }

    private String generateExpression(ASTNode node) {
        if (node instanceof NumberNode) {
            return String.valueOf(((NumberNode) node).value);
        }
        if (node instanceof BooleanNode) {
            return ((BooleanNode) node).value ? "True" : "False";
        }
        if (node instanceof VarNode) {
            return ((VarNode) node).name;
        }
        if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            String left = generateExpression(binOp.left);
            String right = generateExpression(binOp.right);
            return "(" + left + " " + binOp.operator + " " + right + ")";
        }
        return "None";
    }

    
    private String mapValue(String val) {
        if (val == null) return null;

        
        if (val.equals("সত্য")) return "True";
        if (val.equals("মিথ্যা")) return "False";

        if (isBanglaNumber(val)) {
            return String.valueOf(SemanticAnalyzer.convertBanglaToEnglish(val));
        }

        return val;
    }

    private boolean isBanglaNumber(String val) {
        if (val == null || val.isEmpty()) return false;
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            if (i == 0 && c == '-') continue; 
            if (c < '০' || c > '৯') return false;
        }
        return true;
    }
}

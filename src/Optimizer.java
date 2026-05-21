import java.util.*;

public class Optimizer {
    private final List<String> optimizationLog = new ArrayList<>();

    /**
     * Optimize the given list of three-address code instructions.
     * Applies: Constant Propagation, Constant Folding, Copy Propagation, Dead Code Elimination.
     * Control flow instructions (LABEL, GOTO, IF_FALSE_GOTO, PRINT) are preserved as-is.
     */
    public List<Instruction> optimize(List<Instruction> original) {
        // Work on a mutable copy
        List<Instruction> code = new ArrayList<>();
        for (Instruction inst : original) {
            if (inst.type.equals("LABEL") || inst.type.equals("GOTO") || 
                inst.type.equals("IF_FALSE_GOTO") || inst.type.equals("PRINT")) {
                // Copy control flow instructions as-is
                code.add(new Instruction(inst.type, inst.arg1, inst.arg2));
            } else {
                code.add(new Instruction(inst.result, inst.arg1, inst.operator, inst.arg2));
            }
        }

        // Check if there are any control flow instructions
        boolean hasControlFlow = false;
        for (Instruction inst : code) {
            if (inst.type.equals("LABEL") || inst.type.equals("GOTO") || 
                inst.type.equals("IF_FALSE_GOTO") || inst.type.equals("WHILE")) {
                hasControlFlow = true;
                break;
            }
        }

        // Pass 1: Constant Propagation & Constant Folding
        // Only do this for straight-line code (no control flow) to keep it safe
        if (!hasControlFlow) {
            Map<String, String> constants = new LinkedHashMap<>();
            for (int i = 0; i < code.size(); i++) {
                Instruction inst = code.get(i);

                // Skip control flow instructions
                if (isControlFlow(inst)) continue;

                // Propagate known constants into arg1 and arg2
                if (inst.arg1 != null && constants.containsKey(inst.arg1)) {
                    String oldArg = inst.arg1;
                    inst.arg1 = constants.get(inst.arg1);
                    optimizationLog.add("Constant Propagation: replaced '" + oldArg + "' with '" + inst.arg1 + "' in instruction " + (i + 1));
                }
                if (inst.arg2 != null && constants.containsKey(inst.arg2)) {
                    String oldArg = inst.arg2;
                    inst.arg2 = constants.get(inst.arg2);
                    optimizationLog.add("Constant Propagation: replaced '" + oldArg + "' with '" + inst.arg2 + "' in instruction " + (i + 1));
                }

                // Try constant folding: if both operands are numeric literals
                if (inst.operator != null && inst.arg1 != null && inst.arg2 != null) {
                    Integer left = tryParseBangla(inst.arg1);
                    Integer right = tryParseBangla(inst.arg2);
                    if (left != null && right != null) {
                        Integer result = fold(left, inst.operator, right);
                        if (result != null) {
                            String banglaResult = SemanticAnalyzer.convertEnglishToBangla(result);
                            optimizationLog.add("Constant Folding: computed " + inst.arg1 + " " + inst.operator + " " + inst.arg2 + " = " + banglaResult + " in instruction " + (i + 1));
                            inst.arg1 = banglaResult;
                            inst.operator = null;
                            inst.arg2 = null;
                        }
                    }
                    // Boolean folding for ==
                    if (inst.operator != null && inst.operator.equals("==")) {
                        if (inst.arg1.equals(inst.arg2)) {
                            optimizationLog.add("Constant Folding: " + inst.arg1 + " == " + inst.arg2 + " => সত্য in instruction " + (i + 1));
                            inst.arg1 = "সত্য";
                            inst.operator = null;
                            inst.arg2 = null;
                        } else if (left != null && right != null) {
                            String boolResult = left.equals(right) ? "সত্য" : "মিথ্যা";
                            optimizationLog.add("Constant Folding: " + SemanticAnalyzer.convertEnglishToBangla(left) + " == " + SemanticAnalyzer.convertEnglishToBangla(right) + " => " + boolResult + " in instruction " + (i + 1));
                            inst.arg1 = boolResult;
                            inst.operator = null;
                            inst.arg2 = null;
                        }
                    }
                }

                // Track simple assignments as constants (result = arg1, no operator)
                if (inst.operator == null && inst.arg2 == null && inst.arg1 != null) {
                    constants.put(inst.result, inst.arg1);
                }
            }
        }

        // Pass 2: Dead Code Elimination — remove temp assignments whose result is never read
        // Skip this for code with control flow to be safe
        Set<String> usedVars = new HashSet<>();
        for (Instruction inst : code) {
            if (inst.arg1 != null) usedVars.add(inst.arg1);
            if (inst.arg2 != null) usedVars.add(inst.arg2);
        }
        List<Instruction> optimized = new ArrayList<>();
        for (Instruction inst : code) {
            if (!isControlFlow(inst) && isTemp(inst.result) && !usedVars.contains(inst.result)) {
                optimizationLog.add("Dead Code Elimination: removed unused temporary '" + inst.result + "'");
                continue;
            }
            optimized.add(inst);
        }

        return optimized;
    }

    private boolean isControlFlow(Instruction inst) {
        return inst.type.equals("LABEL") || inst.type.equals("GOTO") || 
               inst.type.equals("IF_FALSE_GOTO") || inst.type.equals("PRINT");
    }

    /**
     * Try to parse a Bangla number string into an Integer.
     * Returns null if the string is not a valid Bangla number.
     */
    private Integer tryParseBangla(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            StringBuilder sb = new StringBuilder();
            for (char c : s.toCharArray()) {
                if (c == '-') {
                    sb.append('-');
                } else if (c >= '০' && c <= '৯') {
                    sb.append((char) (c - '০' + '0'));
                } else {
                    return null; // Not a Bangla digit
                }
            }
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Fold a binary operation on two integer constants.
     */
    private Integer fold(int left, String operator, int right) {
        switch (operator) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": return right != 0 ? left / right : null;
            default: return null;
        }
    }

    /**
     * Check if a variable name is a compiler-generated temporary.
     */
    private boolean isTemp(String name) {
        return name != null && name.matches("t\\d+");
    }

    /**
     * Get the log of optimizations that were applied.
     */
    public List<String> getOptimizationLog() {
        return optimizationLog;
    }
}

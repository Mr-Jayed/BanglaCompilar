import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String inputFilePath = "input.bn";
        String outputFilePath = "output.txt";

        // Open the PrintWriter at the start so we can write errors to it
        try (PrintWriter out = new PrintWriter(outputFilePath)) {
            try {
                // 1. Read source code
                String source = Files.readString(Paths.get(inputFilePath));
                
                // 2. Lexical Analysis
                Lexer lexer = new Lexer(source);
                List<Token> tokens = lexer.scanTokens();
                
                // 3. Syntax Analysis
                Parser parser = new Parser(tokens);
                List<ASTNode> astNodes = parser.parse();

                // 4. Full Semantic Analysis
                SymbolTable st = new SymbolTable();
                SemanticAnalyzer analyzer = new SemanticAnalyzer(st);
                
                // This will throw an exception if a variable is undefined or math fails
                analyzer.analyze(astNodes); 

                // 5. Generate Success Report
                out.println("=== STEP 1: LEXICAL ANALYSIS (TOKENS) ===");
                for (Token t : tokens) {
                    out.println(t.toString());
                }

                out.println("\n=== STEP 2: SYNTAX ANALYSIS (AST) ===");
                for (ASTNode node : astNodes) {
                    writeTreeToFile(node, 0, out);
                }

                out.println("\n=== STEP 3: SEMANTIC ANALYSIS & SYMBOL TABLE ===");
                out.println("[Status]: Type checking passed.");
                out.println("[Status]: Scope check passed.");
                out.println("-------------------------");
                st.displayToFile(out);
                
                System.out.println("Process completed successfully. Check output.txt");

            } catch (RuntimeException semanticOrSyntaxError) {
                // Catch errors like undefined variables or syntax mistakes
                out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                out.println("COMPILER ERROR: " + semanticOrSyntaxError.getMessage());
                out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("Compiler error detected. Details written to output.txt");
                
            } catch (IOException e) {
                out.println("FILE ERROR: Could not find or read '" + inputFilePath + "'");
                System.err.println("File error written to output.txt");
            }

        } catch (Exception fatalError) {
            // This catches errors in creating the output.txt file itself
            System.err.println("Fatal Error: Could not write to output file. " + fatalError.getMessage());
        }
    }

    public static void writeTreeToFile(ASTNode node, int indent, PrintWriter out) {
        String p = "  ".repeat(indent);
        if (node instanceof AssignNode) {
            AssignNode a = (AssignNode) node;
            out.println(p + "Assignment: " + a.name);
            writeTreeToFile(a.expr, indent + 1, out);
        } else if (node instanceof BinOpNode) {
            BinOpNode b = (BinOpNode) node;
            out.println(p + "Binary Operator: " + b.operator);
            writeTreeToFile(b.left, indent + 1, out);
            writeTreeToFile(b.right, indent + 1, out);
        } else if (node instanceof VarNode) {
            out.println(p + "Variable: " + ((VarNode) node).name);
        } else if (node instanceof NumberNode) {
            int val = ((NumberNode) node).value;
            String bVal = SemanticAnalyzer.convertEnglishToBangla(val);
            out.println(p + "Integer Value: " + bVal);
        }
    }
}
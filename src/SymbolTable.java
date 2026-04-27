import java.util.*;
import java.io.PrintWriter;

public class SymbolTable {
    // Changing HashMap to LinkedHashMap to maintain order
    private final Map<String, Integer> table = new LinkedHashMap<>();

    public void set(String name, int val) { 
        table.put(name, val); 
    }

    public int get(String name) { 
        return table.get(name); 
    }

    public boolean contains(String name) { 
        return table.containsKey(name); 
    }

    public void displayToFile(PrintWriter out) {
        out.println("Variable | Value (Bangla)");
        out.println("-------------------------");
        if (table.isEmpty()) {
            out.println("(No variables defined)");
        } else {
            table.forEach((k, v) -> {
                String bVal = SemanticAnalyzer.convertEnglishToBangla(v);
                out.println(k + " : " + bVal);
            });
        }
    }
}
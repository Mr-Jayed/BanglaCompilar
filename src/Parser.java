import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public List<ASTNode> parse() {
        List<ASTNode> nodes = new ArrayList<>();
        while (!isAtEnd()) {
            if (match(TokenType.SEMI)) continue;
            nodes.add(statement());
        }
        return nodes;
    }

    private ASTNode statement() {
        if (match(TokenType.DHORO)) return declaration();
        throw new RuntimeException("Expected 'ধরো' at line " + peek().line);
    }

    private ASTNode declaration() {
        Token name = consume(TokenType.ID, "Expected variable name.");
        consume(TokenType.ASSIGN, "Expected '='.");
        ASTNode expr = expression();
        consume(TokenType.SEMI, "Expected ';' after statement.");
        return new AssignNode(name.lexeme, expr);
    }

    private ASTNode expression() {
        ASTNode node = term();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            String op = previous().lexeme;
            node = new BinOpNode(node, op, term());
        }
        return node;
    }

    private ASTNode term() {
        ASTNode node = primary();
        while (match(TokenType.MUL, TokenType.DIV)) {
            String op = previous().lexeme;
            node = new BinOpNode(node, op, primary());
        }
        return node;
    }

    private ASTNode primary() {
        if (match(TokenType.NUMBER)) return new NumberNode(SemanticAnalyzer.convertBanglaToEnglish(previous().lexeme));
        if (match(TokenType.ID)) return new VarNode(previous().lexeme);
        if (match(TokenType.LPAREN)) {
            ASTNode expr = expression();
            consume(TokenType.RPAREN, "Expected ')'");
            return expr;
        }
        throw new RuntimeException("Expected expression at line " + peek().line);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) { if (check(type)) { advance(); return true; } }
        return false;
    }

    private Token consume(TokenType type, String msg) { if (check(type)) return advance(); throw new RuntimeException(msg); }
    private boolean check(TokenType type) { return !isAtEnd() && peek().type == type; }
    private Token advance() { if (!isAtEnd()) current++; return previous(); }
    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }
}
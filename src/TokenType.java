public enum TokenType {
    DHORO,      // সংখ্যা (variable declaration)
    ID,         // Identifiers (Bangla letters)
    NUMBER,     // ০-৯
    ASSIGN,     // =
    EQ,         // ==
    NEQ,        // !=
    GT,         // >
    LT,         // <
    GTE,        // >=
    LTE,        // <=
    PLUS, MINUS, MUL, DIV,
    LPAREN, RPAREN,
    LBRACE, RBRACE, // { }
    SEMI,       // ;
    TRUE, FALSE, // সত্য, মিথ্যা
    IF,         // যদি
    ELSE,       // নাহলে
    WHILE,      // যতক্ষণ
    PRINT,      // দেখাও
    EOF         // End of file
}
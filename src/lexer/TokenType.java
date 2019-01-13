package lexer;

public enum TokenType {

    TYPE_INT,
    TYPE_HEX,

    TRUE,
    FALSE,

    ID,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULUS,

    ASSIGN,

    MORE,
    MORE_EQUAL,
    LESS,
    LESS_EQUAL,
    EQUAL,
    NOT_EQUAL,

    OR,
    AND,
    NOT,

    BRACKET_OPEN,
    BRACKET_CLOSE,
    CURLY_BRACKET_OPEN,
    CURLY_BRACKET_CLOSE,
    SEMICOLON,
    COMMA,
    COLON,

    INT,
    BOOL,
    RETURN,
    SWITCH,
    CASE,
    DEFAULT,
    VOID,

    ERROR,
    EOF
}

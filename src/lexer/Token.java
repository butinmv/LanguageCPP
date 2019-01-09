package lexer;

public class Token {
    private String text;
    private TokenType type;

    public Token(TokenType type) {
        this.type = type;
        this.text = "";
    }

    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        String str = String.valueOf(type);
        if (!text.equals(""))
            str += " " + text;
        return str;
    }
}

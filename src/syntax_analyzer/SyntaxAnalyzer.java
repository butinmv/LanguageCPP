package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;


public class SyntaxAnalyzer {

    private Lexer lexer;

    public SyntaxAnalyzer(Lexer lexer) {
        this.lexer = lexer;
    }

    protected Token nextToken() {
        return lexer.next();
    }

    protected Token nextToken(TokenType type, String text) {
        Token token = lexer.next();
        if (token.getType() != type)
            printError(text);
        return token;
    }

    protected Token nextTokenRead() {
        lexer.save();
        Token token = lexer.next();
        lexer.ret();
        return token;
    }

    protected void printError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
        System.exit(1);
    }
}


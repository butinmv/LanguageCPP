package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import objects.TypeData;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

public class SyntaxAnalyzer {

    private Lexer lexer;

    public SyntaxAnalyzer(Lexer lexer) {
        this.lexer = lexer;
    }

    public void program() {
        Token token = nextTokenRead();
        while (token.getType() != TokenType.EOF) {
            if (token.getType() == TokenType.VOID) {
                nextToken(TokenType.ID, "Ожидался идентификатор");
                if (isFunction(token))
                    function();
                else
                    printError("Неизвестный символ");
            } else if (token.getType() == TokenType.BOOL || token.getType() == TokenType.INT) {
                lexer.next();
                nextTokenRead();
                nextToken(TokenType.ID, "Ожидался идентификатор");
                token = lexer.next();
                if (isFunction(token))
                    function();
                else if (isData(token))
                    data();
                else
                    printError("Неизвестный символ");
            }
            else
                printError("Неизвестный символ");
            token = nextTokenRead();
        }
    }

    private void data() {

    }

    private boolean isData(Token token) {
        return token.getType() == TokenType.COMMA || token.getType() == TokenType.SEMICOLON;
    }

    private void function() {
        Token token;
        do {
            token = nextTokenRead();
            formalParameters();
        } while (token.getType() != TokenType.BRACKET_CLOSE);
    }

    private void formalParameters() {
        Token token;
        do {
            token = lexer.next();
            if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
                token = nextTokenRead();
                nextToken(TokenType.ID, "Ожидался идентификатор");
                token = nextTokenRead();
            }
        } while (token.getType() == TokenType.COMMA);
    }

    private boolean isFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private Token nextToken(TokenType type, String text) {
        Token token = lexer.next();
        if (token.getType() != type)
            printError(text);
        return token;
    }

    private Token nextTokenRead() {
        lexer.save();
        Token token = lexer.next();
        lexer.ret();
        return token;
    }

    private void printError(String errorText) {
        System.out.println(errorText);
    }
}


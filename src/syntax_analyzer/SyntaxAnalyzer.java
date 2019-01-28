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
            }
            else if (token.getType() == TokenType.BOOL || token.getType() == TokenType.INT) {
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

    //TODO: Сделай data()
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

        token = nextTokenRead();
        if (isCompoundOperator(token))
            compoundOperator();
        else
            printError("Ожидался символ {");
    }

    private boolean isCompoundOperator(Token token) {
        return token.getType() == TokenType.CURLY_BRACKET_OPEN;
    }

    private void compoundOperator() {
        Token token = lexer.next();
        while (token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            if (isData(token))
                data();
            else if (isOperator(token))
                operator();
            else
                printError("Неизвестный символ");
        }
    }

    private void operator() {
        Token token = nextTokenRead();

        if (token.getType() == TokenType.SEMICOLON)
            nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
        else if (isCompoundOperator(token))
            compoundOperator();
        else if (isOperSwitch(token))
            operSwitch();
        else if (isOperReturn(token))
            operReturn();
        else if (token.getType() == TokenType.ID) {
            token = nextTokenRead();
            if (isAssigment(token))
                assign();
            else if (isCallFunction(token))
                callFunction();
            else
                printError("Ошибка");
        }

    }

    //TODO: Сделай operReturn
    private void operReturn() {
    }

    //TODO: Сделай operSwitch
    private void operSwitch() {
    }

    private boolean isOperator(Token token) {
        return  isOperSwitch(token) || token.getType() == TokenType.ID ||
                isOperReturn(token) || token.getType() == TokenType.SEMICOLON ||
                isCompoundOperator(token);
    }

    private boolean isOperReturn(Token token) {
        return token.getType() == TokenType.RETURN;
    }

    private boolean isOperSwitch(Token token) {
        return token.getType() == TokenType.SWITCH;
    }

    private void formalParameters() {
        Token token;
        do {
            token = lexer.next();
            if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
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


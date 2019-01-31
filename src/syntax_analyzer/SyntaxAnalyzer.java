package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;


public abstract class SyntaxAnalyzer {

    private Lexer lexer;

    SyntaxAnalyzer(Lexer lexer) {
        this.lexer = lexer;
    }

    Token nextToken() {
        return lexer.next();
    }

    Token nextToken(TokenType type, String text) {
        Token token = lexer.next();
        if (token.getType() != type)
            printError(text);
        return token;
    }

    Token nextTokenRead() {
        lexer.save();
        Token token = lexer.next();
        lexer.ret();
        return token;
    }

    void printError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
        System.exit(1);
    }

    void expression1() {
        expression2();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.OR) {
            nextToken();
            expression2();
            token = nextTokenRead();
        }
    }

    private void expression2() {
        expression3();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.AND) {
            nextToken();
            expression3();
            token = nextTokenRead();
        }
    }

    private void expression3() {
        expression4();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.EQUAL || token.getType() == TokenType.NOT_EQUAL) {
            nextToken();
            expression4();
            token = nextTokenRead();
        }
    }

    private void expression4() {
        expression5();

        Token token = nextTokenRead();
        while  (token.getType() == TokenType.MORE || token.getType() == TokenType.MORE_EQUAL ||
                token.getType() == TokenType.LESS || token.getType() == TokenType.LESS_EQUAL) {
            nextToken();
            expression5();
            token = nextTokenRead();
        }
    }

    private void expression5() {
        expression6();

        Token token = nextTokenRead();
        while  (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
            nextToken();
            expression6();
            token = nextTokenRead();
        }
    }

    private void expression6() {
        expression7();
        Token token = nextTokenRead();
        while  (token.getType() == TokenType.MULTIPLY || token.getType() == TokenType.DIVIDE || token.getType() == TokenType.MODULUS) {
            nextToken();
            expression7();
            token = nextTokenRead();
        }
    }

    private void expression7() {
        Token token = nextTokenRead();
        while  (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS || token.getType() == TokenType.NOT) {
            nextToken();
            token = nextTokenRead();
        }
        elementaryExpression();
    }

    private void elementaryExpression() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.ID) {
            nextToken();
            token = nextTokenRead();
            if (token.getType() == TokenType.BRACKET_OPEN) {
                callFunction();
            }
        }
        else if (token.getType() == TokenType.BRACKET_OPEN) {
            nextToken();
            token = nextTokenRead();
            if (isExpression1(token))
                expression1();
            else {
                printError("Ожидалось выражение");
            }
            nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");
        }
        else if (token.getType() == TokenType.TYPE_INT)
            constInt();
        else if (token.getType() == TokenType.TYPE_HEX)
            constHex();
        else if (token.getType() == TokenType.TRUE)
            constTrue();
        else if (token.getType() == TokenType.FALSE)
            constFalse();
        else
            printError("Ошибка");
    }

    protected abstract void constInt();
    protected abstract void constHex();
    protected abstract void constTrue();
    protected abstract void constFalse();

    boolean isExpression1(Token token) {
        return isExpression2(token);
    }

    private boolean isExpression2(Token token) {
        return isExpression3(token);
    }

    private boolean isExpression3(Token token) {
        return isExpression4(token);
    }

    private boolean isExpression4(Token token) {
        return isExpression5(token);
    }

    private boolean isExpression5(Token token) {
        return isExpression6(token);
    }

    private boolean isExpression6(Token token) {
        return isExpression7(token);
    }

    private boolean isExpression7(Token token) {
        return isElementaryExpresion(token) || token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS || token.getType() == TokenType.NOT;
    }

    protected abstract boolean isElementaryExpresion(Token token);

    abstract void callFunction();
}


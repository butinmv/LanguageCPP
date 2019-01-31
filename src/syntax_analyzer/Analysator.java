package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Analysator extends SyntaxAnalyzer {
    public Analysator(Lexer lexer) {
        super(lexer);
    }

    public void program() {
        Token token = nextTokenRead();
        while (token.getType() != TokenType.EOF && token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            functionOrData(token);
            token = nextTokenRead();
            nextToken(TokenType.CURLY_BRACKET_CLOSE, "Ожидается сивол }");
        }
    }

    private void functionOrData(Token token) {
        if (token.getType() == TokenType.VOID || token.getType() == TokenType.BOOL || token.getType() == TokenType.INT) {
            nextToken();
            nextToken(TokenType.ID, "Ожидался идентификатор");
            token = nextTokenRead();
            if (isFunction(token))
                function();
            else if (isData(token))
                data();
            else
                printError("Неизвестный символ");
        }
    }

    private boolean isFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private void function() {
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        Token token;
        do {
            formalParameters();
            token = nextTokenRead();
        } while (token.getType() != TokenType.BRACKET_CLOSE);
        nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");

        token = nextTokenRead();
        if (isCompoundOperator(token))
            compoundOperator();
        else
            printError("Ожидался символ {");
    }

    private void formalParameters() {
        Token token;
        do {
            token = nextTokenRead();
            if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL)
                nextToken();
            else
                printError("Ожидался тип INT или BOOL");
            nextToken(TokenType.ID, "Ожидался идентификатор");
            token = nextTokenRead();
            if (token.getType() == TokenType.COMMA)
                nextToken();
        } while (token.getType() == TokenType.COMMA);
    }

    private boolean isData(Token token) {
        return token.getType() == TokenType.COMMA || token.getType() == TokenType.SEMICOLON || token.getType() == TokenType.ASSIGN;
    }

    private void data() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.SEMICOLON) {
            nextToken();
        } else if (token.getType() == TokenType.COMMA) {
            nextToken();
            nextToken(TokenType.ID, "Ожидался идентификатор");
            data();
        } else if (token.getType() == TokenType.ASSIGN) {
            nextToken();
            token = nextTokenRead();
            if (isExpression1(token))
                expression1();
            data();
        }
    }

    private boolean isCompoundOperator(Token token) {
        return token.getType() == TokenType.CURLY_BRACKET_OPEN;
    }

    private void compoundOperator() {
        Token token = nextTokenRead();
        nextToken(TokenType.CURLY_BRACKET_OPEN, "Ожидался символ {");
        token = nextTokenRead();
        while (token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            operatorOrData();
            token = nextTokenRead();
        }
    }

    private void operatorOrData() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
            nextToken();
            nextToken(TokenType.ID, "Ожидался идентификатор");
            token = nextTokenRead();
            if (isData(token))
                data();
        }
        else if (isOperator(token))
            operator();
        else
            printError("Ожидались данные или оператор");
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
            nextToken();
            token = nextTokenRead();
            if (isAssigment(token))
                assign();
            else if (isCallFunction(token))
                callFunction();
            else
                printError("Ошибка");
        }

    }

    private boolean isCallFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    @Override
    void callFunction() {
        Token token = nextTokenRead();
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        do {
            token = nextTokenRead();
            if (isExpression1(token))
                expression1();
            token = nextTokenRead();
        } while (token.getType() == TokenType.COMMA);
        nextToken(TokenType.BRACKET_CLOSE, "Ожидася символ )");
        nextToken(TokenType.SEMICOLON, "Ожидася символ ;");
    }

    private void assign() {
        Token token = nextTokenRead();
        if(isExpression1(token))
            expression1();
        nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
    }



    private void operReturn() {
        nextToken(TokenType.RETURN, "Ожидался return");
        Token token = nextTokenRead();
        if (isExpression1(token))
            expression1();
        nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
    }

    //TODO: Сделай operSwitch
    private void operSwitch() {

    }

    @Override
    protected void constFalse() {
        nextToken();
    }

    @Override
    protected void constTrue() {
        nextToken();
    }

    @Override
    protected void constHex() {
        nextToken();
    }

    @Override
    protected void constInt() {
        nextToken();
    }

    private boolean isAssigment(Token token) {
        return token.getType() == TokenType.ASSIGN;
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

    protected boolean isElementaryExpresion(Token token) {
        return  token.getType() == TokenType.ID ||
                token.getType() == TokenType.BRACKET_OPEN ||
                token.getType() == TokenType.TYPE_INT ||
                token.getType() == TokenType.TYPE_HEX ||
                token.getType() == TokenType.FALSE ||
                token.getType() == TokenType.TRUE;
    }
}

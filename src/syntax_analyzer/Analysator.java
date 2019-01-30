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
        while (token.getType() != TokenType.EOF) {
            operatorOrData(token);
            token = nextTokenRead();
        }
    }

    void operatorOrData(Token token) {
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
        else {
            printError("Неизвестный оператор");
        }
    }

    private boolean isFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private void function() {
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        Token token;
        do {
            token = nextTokenRead();
            formalParameters();
        } while (token.getType() != TokenType.BRACKET_CLOSE);
        nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");

        token = nextTokenRead();
        if (isCompoundOperator(token))
            compoundOperator();
        else
            printError("Ожидался символ {");
    }

    private boolean isData(Token token) {
        return token.getType() == TokenType.COMMA || token.getType() == TokenType.SEMICOLON || token.getType() == TokenType.ASSIGN;
    }

    private void data() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.SEMICOLON) {
            nextToken();
            return;
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
        Token token = nextToken(TokenType.CURLY_BRACKET_OPEN, "Ожидался символ {");
        while (token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            if (isData(token))
                data();
            else if (isOperator(token))
                operator();
            else
                printError("Неизвестный символ");
            token = nextTokenRead();
        }
        nextToken(TokenType.CURLY_BRACKET_CLOSE, "Ожидался символ }");
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

    private boolean isCallFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private void callFunction() {
    }

    private void assign() {
        Token token = nextTokenRead();
        if(isExpression1(token))
            expression1();
        nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
    }

    protected void expression1() {
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
            constfalse();
        else
            printError("Ошибка");
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

    //TODO:  Доделать формальные параметры
    private void formalParameters() {
        Token token;
        do {
            token = nextTokenRead();
            if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
                nextToken();
                nextToken(TokenType.ID, "Ожидался идентификатор");
                token = nextTokenRead();
            }
            if (token.getType() == TokenType.COMMA)
                nextToken();
        } while (token.getType() == TokenType.COMMA);
    }

    private void constfalse() {
        nextToken();
    }

    private void constTrue() {
        nextToken();
    }

    private void constHex() {
        nextToken();
    }

    private void constInt() {
        nextToken();
    }

    protected boolean isExpression1(Token token) {
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

    private boolean isElementaryExpresion(Token token) {
        return  token.getType() == TokenType.ID ||
                token.getType() == TokenType.BRACKET_OPEN ||
                token.getType() == TokenType.TYPE_INT ||
                token.getType() == TokenType.TYPE_HEX ||
                token.getType() == TokenType.FALSE ||
                token.getType() == TokenType.TRUE;
    }
}

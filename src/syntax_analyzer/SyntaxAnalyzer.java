package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import objects.TypeData;

public class SyntaxAnalyzer {

    private Lexer lexer;

    public SyntaxAnalyzer(Lexer lexer) {
        this.lexer = lexer;
    }

    public void program() {
        Token token = nextTokenRead();
        while (token.getType() != TokenType.EOF) {
            if (isData(token))
                data();
            else if (isFunction(token))
                function();
            else
                printError("Неизвестный символ");
            nextToken(TokenType.CURLY_BRACKET_CLOSE, "Ожидается символ }");
        }
    }

    private boolean isData(Token token) {
        return (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL);
    }

    private void data() {
        Token token = lexer.next();
        TypeData typeData;
        if (token.getType() == TokenType.INT)
            typeData = TypeData.INT;
        else
            typeData = TypeData.BOOL;

        token = nextTokenRead();
        if (isVariable(token))
            variable(typeData);
        else
            printError("Ожидается идентификатор");
        token = nextTokenRead();

        nextToken(TokenType.SEMICOLON, "Ожидается символ ;");

    }

    private boolean isVariable(Token token) {
        return token.getType() == TokenType.ID;
    }

    private void variable(TypeData typeData) {
        Token varName = nextToken(TokenType.ID, "Ожидался идентификатор");

        Token token = nextTokenRead();
        if (token.getType() == TokenType.ASSIGN) {
            nextToken(TokenType.ASSIGN, "Ожидался символ =");
            token = nextTokenRead();
            if (isConstTen(token))
                constTen();
            else if (isConstHex(token))
                constHex();
            else if (isConstLogical(token))
                constLogical();
            else if (isExpressionVar(token))
                expressionVar();
            else
                printError("Неизвестный символ");
        }
    }

    private boolean isConstTen(Token token) {
        return token.getType() == TokenType.TYPE_INT;
    }

    private void constTen() {
        nextToken(TokenType.TYPE_INT,"Ожидалась десятичная константа");
    }

    private boolean isConstHex(Token token) {
        return  token.getType() == TokenType.TYPE_HEX;
    }

    private void constHex() {
        nextToken(TokenType.TYPE_HEX,"Ожидалась шестнадцатиричная константа");
    }

    private boolean isConstLogical(Token token) {
        return token.getType() == TokenType.TRUE || token.getType() ==TokenType.FALSE;
    }

    private void constLogical() {
        nextToken(TokenType.BOOL, "Ожидалась булевая константа");
    }

    private boolean isExpressionVar(Token token) {
        return  token.getType() == TokenType.NOT ||
                token.getType() == TokenType.PLUS ||
                token.getType() == TokenType.MINUS ||
                isExpressionAddend(token);
    }

    private void expressionVar() {
        Token token = nextTokenRead();
        while (token.getType() == TokenType.NOT) {
            lexer.next();
            token = nextTokenRead();
        }

        token = nextTokenRead();
        if (isExpressionAddend(token))
            expressionAddend();
    }

    private boolean isExpressionAddend(Token token) {
        return  token.getType() == TokenType.PLUS ||
                token.getType() == TokenType.MINUS;
    }

    private void expressionAddend() {
        Token token = nextTokenRead();
        while (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
            lexer.next();
            token = nextTokenRead();
        }

        expressionFactor();

        token = nextTokenRead();
        while (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
            lexer.next();
            expressionFactor();
            token = nextTokenRead();
        }
    }

    private void expressionFactor() {
        Token token = nextTokenRead();

        expressionElementary();

        while (token.getType() == TokenType.MULTIPLY || token.getType() == TokenType.DIVIDE || token.getType() == TokenType.MODULUS) {
            lexer.next();
            expressionElementary();
            token = nextTokenRead();
        }
    }

    private void expressionElementary() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.ID)
            token = lexer.next();
        else if (token.getType() == TokenType.INT)
            lexer.next();
        else if (token.getType() == TokenType.TRUE)
            lexer.next();
        else if (token.getType() == TokenType.FALSE)
            lexer.next();
    }

    private boolean isExpressionFactor(Token token) {
        return  token.getType() == TokenType.MULTIPLY ||
                token.getType() == TokenType.DIVIDE ||
                token.getType() == TokenType.MODULUS ||
                isExpressionElementary(token);
    }

    private boolean isExpressionElementary(Token token) {
        return  token.getType() == TokenType.ID ||
                token.getType() == TokenType.TYPE_INT ||
                token.getType() == TokenType.TYPE_HEX ||
                token.getType() == TokenType.TRUE ||
                token.getType() == TokenType.FALSE ||
                isExpressionFunctionCall(token) ||
                token.getType() == TokenType.BRACKET_OPEN;
    }

    //TODO: Сделать вызов функции
    private boolean isExpressionFunctionCall(Token token) {
        return true;
    }

    private boolean isFunction(Token token) {
        return true;
    }

    private void function() {

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

    public void printError(String text) {
        System.out.println(text + " строка " + lexer.getNumberRow() + ", столбец " + lexer.getNumberCol());
    }

}

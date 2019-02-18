package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import objects.Node;
import objects.TypeData;


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
        if (token.getType() != type) {
            System.out.println(token);
            printError(text);
        }
        return token;
    }

    Token nextTokenRead() {
        lexer.save();
        Token token = lexer.next();
        lexer.ret();
        return token;
    }

    public void printError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
        System.exit(1);
    }

    void printSemError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
    }

    Node expression1() {
        Node node = expression2();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.OR) {
            nextToken();
            expression2();
            node.typeData = TypeData.BOOL;
            token = nextTokenRead();
        }

        return node;
    }

    private Node expression2() {
        Node node = expression3();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.AND) {
            nextToken();
            expression3();
            node.typeData = TypeData.BOOL;
            token = nextTokenRead();
        }

        return node;
    }

    private Node expression3() {
        Node node = expression4();

        Token token = nextTokenRead();
        while(token.getType() == TokenType.EQUAL || token.getType() == TokenType.NOT_EQUAL) {
            nextToken();
            expression4();
            node.typeData = TypeData.BOOL;
            token = nextTokenRead();
        }

        return node;
    }

    private Node expression4() {
        Node node = expression5();

        Token token = nextTokenRead();
        while  (token.getType() == TokenType.MORE || token.getType() == TokenType.MORE_EQUAL ||
                token.getType() == TokenType.LESS || token.getType() == TokenType.LESS_EQUAL) {
            nextToken();
            expression5();
            node.typeData = TypeData.BOOL;
            token = nextTokenRead();
        }
        return node;
    }

    private Node expression5() {
        Node node = expression6();

        Token token = nextTokenRead();
        while  (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
            nextToken();
            expression6();
            node.typeData = TypeData.INTEGER;
            token = nextTokenRead();
        }
        return node;
    }

    private Node expression6() {
        Node node = expression7();
        Token token = nextTokenRead();
        while  (token.getType() == TokenType.MULTIPLY || token.getType() == TokenType.DIVIDE || token.getType() == TokenType.MODULUS) {
            nextToken();
            expression7();
            node.typeData = TypeData.INTEGER;
            token = nextTokenRead();
        }

        return  node;
    }

    private Node expression7() {
        Token token = nextTokenRead();
        int isZnak = -1;
        while  (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS || token.getType() == TokenType.NOT) {
            if (token.getType() == TokenType.PLUS)
                isZnak = 1;
            else if (token.getType() == TokenType.MINUS)
                isZnak = 2;
            else
                isZnak = 3;
            nextToken();
            token = nextTokenRead();
        }

        Node node = elementaryExpression();
        if (isZnak == 1 || isZnak == 2) {
            node.typeData = TypeData.INTEGER;
            return node;
        }
        else {
            node.typeData = TypeData.BOOL;
            return node;
        }
    }

    private Node elementaryExpression() {
        Token token = nextTokenRead();

        if (token.getType() == TokenType.ID) {
            Token tokenName = token;
            nextToken();
            token = nextTokenRead();

            if (token.getType() == TokenType.BRACKET_OPEN) {
                callFunction();
                return null;
            }
            return null;
        }
        else if (token.getType() == TokenType.BRACKET_OPEN) {
            nextToken();
            token = nextTokenRead();
            Node node = null;
            if (isExpression1(token)) {
                node = expression1();
            }
            else {
                printError("Ожидалось выражение");
            }
            nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");
            return node;
        }
        else if (token.getType() == TokenType.TYPE_INT) {
            constInt();
            return Node.createConst(TypeData.INTEGER);
        }
        else if (token.getType() == TokenType.TYPE_HEX) {
            constHex();
            return Node.createConst(TypeData.INTEGER);
        }
        else if (token.getType() == TokenType.TRUE) {
            constTrue();
            return Node.createConst(TypeData.BOOL);
        }
        else if (token.getType() == TokenType.FALSE) {
            constFalse();
            return Node.createConst(TypeData.BOOL);
        }
        else {
            printError("Ошибка");
            return null;
        }
    }

    private Boolean isNumber(TypeData typeData) {
        return typeData == TypeData.INTEGER;
    }

    private Boolean isBool(TypeData typeData) {
        return typeData == TypeData.TRUE || typeData == TypeData.FALSE;
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


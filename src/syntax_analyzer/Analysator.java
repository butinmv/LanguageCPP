package syntax_analyzer;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import objects.Node;
import objects.ProgramTree;
import objects.TypeData;
import objects.TypeObject;

import java.util.*;

public class Analysator {
    private ProgramTree tree;
    private ProgramTree thisTree;
    private Deque<ProgramTree> stack = new LinkedList<>();
    boolean parameters = false;

    private TypeData typeData;
    private Lexer lexer;

    public Analysator(Lexer lexer) {
        this.lexer = lexer;
        tree = thisTree = new ProgramTree();
    }

    public void program() {
        Token token = nextTokenRead();
        while (token.getType() != TokenType.EOF && token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            if(isFunctionOrData(token)) {
                functionOrData();
            }
            else
                printError("Ожидались данные или функция");
            token = nextTokenRead();
        }
    }

    private boolean isFunctionOrData(Token token) {
        return token.getType() == TokenType.BOOL || token.getType() == TokenType.INT;
    }

    private void functionOrData() {
        Token token = nextTokenRead();

        if (TokenType.BOOL == token.getType())
            typeData = TypeData.BOOL;
        else
            typeData = TypeData.INTEGER;

        nextToken();
        Token tokenID = nextToken(TokenType.ID, "Ожидался идентификатор");

        token = nextTokenRead();
        if (isFunction(token)) {
            Node node = addFunc(tokenID, typeData);
            ArrayList<TypeData> parameters = function();
            node.setParameters(parameters);

            token = nextTokenRead();
            if (isCompoundOperator(token))
                compoundOperator();
            else
                printError("Ожидался символ {");
        }
        else if (isData(token)) {
            addVar(typeData, tokenID);
            data(typeData);
        }
        else
            printError("Неизвестный символ");
    }

    private boolean isFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private ArrayList<TypeData> function() {
        in();
        this.parameters = true;
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        Token token;
        ArrayList<TypeData> parameters;
        do {
            parameters = formalParameters();
            token = nextTokenRead();
        } while (token.getType() != TokenType.BRACKET_CLOSE);
        nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");

        return parameters;
    }

    private ArrayList<TypeData> formalParameters() {
        Token token;
        ArrayList<TypeData> parameters = new ArrayList<>();
        do {
            token = nextTokenRead();
            if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
                if (token.getType() == TokenType.INT) {
                    typeData = TypeData.INTEGER;
                    parameters.add(TypeData.INTEGER);
                }
                else {
                    typeData = TypeData.BOOL;
                    parameters.add(TypeData.BOOL);
                }

                nextToken();
                Token tokenName = nextToken(TokenType.ID, "Ожидался идентификатор");
                addVar(typeData, tokenName);
            }
            else
                printError("Ожидался тип INT или тип BOOL");
            token = nextTokenRead();
            if (token.getType() == TokenType.COMMA)
                nextToken();
        } while (token.getType() == TokenType.COMMA);

        return parameters;
    }

    private Node addFunc(Token token, TypeData typeData) {
        if (thisTree.findUpFunction(token.getText(), typeData) != null)
            printSemError("Функция '" + token.getText() + "' уже была объявлена");
        Node node = Node.createFunction(token.getText(), typeData);
        thisTree.setLeft(node);
        thisTree = thisTree.left;
        return node;
    }

    private void addVar(TypeData typeData, Token token) {
        if (thisTree.findUpVar(token.getText()) != null && !parameters) {
            printSemError("Переменная " + token.getText() + " уже существует");
            if (thisTree.findUpVarLevel(token.getText()) == null && !parameters) {
                Node node = Node.createVar(token.getText(), typeData);
                thisTree.setLeft(node);
                thisTree = thisTree.left;
            }
        }
        else {
            Node node = Node.createVar(token.getText(), typeData);
            thisTree.setLeft(node);
            thisTree = thisTree.left;
        }
    }

    private boolean isData(Token token) {
        return token.getType() == TokenType.COMMA || token.getType() == TokenType.SEMICOLON || token.getType() == TokenType.ASSIGN;
    }

    private void data(TypeData typeData) {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.SEMICOLON) {
            nextToken();
        } else if (token.getType() == TokenType.COMMA) {
            nextToken();
            Token tokenID = nextToken(TokenType.ID, "Ожидался идентификатор");
            addVar(typeData, tokenID);
            data(typeData);
        } else if (token.getType() == TokenType.ASSIGN) {
            nextToken();
            token = nextTokenRead();
            if (isExpression1(token))
                expression1();
            data(typeData);
        }
        else
            printError("Ожидался символ ;");
    }

    private boolean isCompoundOperator(Token token) {
        return token.getType() == TokenType.CURLY_BRACKET_OPEN;
    }

    private void compoundOperator() {
        if(!this.parameters)
            in();
        else
            this.parameters = false;
        nextToken(TokenType.CURLY_BRACKET_OPEN, "Ожидался символ {");
        Token token = nextTokenRead();
        while (token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            operatorOrData();
            token = nextTokenRead();
        }

        out();
        nextToken(TokenType.CURLY_BRACKET_CLOSE, "Ожидался символ }");
    }

    private void in() {
        if (thisTree.right != null) {
            thisTree.setLeft(Node.createEmptyNode());
            thisTree = thisTree.left;
        }
        stack.push(thisTree);
        thisTree.setRight(Node.createEmptyNode());
        thisTree = thisTree.right;
    }
    private void out() {
        thisTree = stack.pop();
    }

    private void operatorOrData() {
        Token token = nextTokenRead();
        if (token.getType() == TokenType.INT || token.getType() == TokenType.BOOL) {
            if (token.getType() == TokenType.INT)
                typeData = TypeData.INTEGER;
            else
                typeData = TypeData.BOOL;
            nextToken();
            Token tokenName = nextToken(TokenType.ID, "Ожидался идентификатор");
            token = nextTokenRead();
            if (isData(token)) {
                addVar(typeData, tokenName);
                data(typeData);
            }
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
            Token token1 = nextTokenRead();
            if (isAssigment(token1)) {
                nextToken();
                assign(token);
            }
            else if (isCallFunction(token1)) {
                callFunction(token);
            }
            else
                printError("Ошибка");
        }

    }

    private boolean isCallFunction(Token token) {
        return token.getType() == TokenType.BRACKET_OPEN;
    }

    private Node callFunction(Token tokenIn) {
        Token token;
        ArrayList<TypeData> parameters = new ArrayList<>();
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        do {
            token = nextTokenRead();
            Node node;

            /*
            if (token.getType() == TokenType.TYPE_DEC || token.getType() == TokenType.TYPE_HEX) {
                typeData = TypeData.INTEGER;
                parameters.add(typeData);
            } else if (token.getType() == TokenType.TRUE || token.getType() ==TokenType.FALSE) {
                typeData = TypeData.BOOL;
                parameters.add(typeData);
            } else {
                typeData = TypeData.UNKNOWN;
                printSemError("Неизвестный тип");
            }
            */


            if (isExpression1(token)) {
                node = expression1();
                parameters.add(node.typeData);
            }

            token = nextTokenRead();
            if (token.getType() == TokenType.COMMA)
                nextToken();
        } while (token.getType() == TokenType.COMMA);
        Node function;
        nextToken(TokenType.BRACKET_CLOSE, "Ожидася символ )");
        if (thisTree.findUp(tokenIn.getText()) == null) {
            printSemError("Данная функция " + tokenIn.getText() + " была необъявленна");
            return thisTree.node;
        } else {
            function = thisTree.findUp(tokenIn.getText()).node;
        }
        if (function != null && function.getTypeObject() == TypeObject.FUNCTION) {
            if (function.getParameters().size() == parameters.size())
                return thisTree.node;
            else {
                printSemError("Ошибка входных типов входных параметров в функции " + tokenIn.getText());
                return thisTree.node;
            }

        } else {
            printSemError("Данная функция " + tokenIn.getText() + " была необъявленна");
            return null;
        }
    }

    private void assign(Token tokenID) {
        Token token = nextTokenRead();
        if(isExpression1(token)) {

            if (thisTree.findUpVar(tokenID.getText()) == null) {
                printSemError("Переменная " + tokenID.getText() + " не найдена");
            } else {
                // TODO:
            }
            Node node = expression1();
        }
        token = nextTokenRead();
        if (token.getType() == TokenType.SEMICOLON)
            nextToken();
        else
            printError("Ожидался символ ;");
    }

    private void operReturn() {
        nextToken(TokenType.RETURN, "Ожидался return");
        Token token = nextTokenRead();
        if (isExpression1(token)) {
            expression1();
            nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
        } else
            printError("Ожидалось выражение");
    }

    private void operSwitch() {
        nextToken(TokenType.SWITCH, "Ожидался switch");
        nextToken(TokenType.BRACKET_OPEN, "Ожидался символ (");
        Token token = nextTokenRead();
        if(isExpression1(token))
            expression1();
        nextToken(TokenType.BRACKET_CLOSE, "Ожидался символ )");
        nextToken(TokenType.CURLY_BRACKET_OPEN, "Ожидался символ {");
        token = nextTokenRead();
        while (token.getType() != TokenType.CURLY_BRACKET_CLOSE) {
            if (isCases(token))
                cases();
            else if (isDefault(token))
                defaultOper();
            token = nextTokenRead();
        }
        nextToken(TokenType.CURLY_BRACKET_CLOSE, "Ожидался символ }");
    }

    private void defaultOper() {
        nextToken(TokenType.DEFAULT, "Ожидался оператор default");
        nextToken(TokenType.COLON, "Ожидался символ :");
        Token token = nextTokenRead();
        if(isCaseOper(token))
            caseOper();
        else
            printError("Ожидались case операторы");
    }

    private void cases() {
        nextToken(TokenType.CASE, "Ожидался оператор case");
        nextToken(TokenType.TYPE_DEC, "Ожидалась целая константа");
        nextToken(TokenType.COLON, "Ожидался символ :");
        Token token = nextTokenRead();
        if (isCaseOper(token))
            caseOper();
        else
            printError("Ожидались case-операторы");


    }

    private void caseOper() {
        Token token = nextTokenRead();
        while (token.getType() != TokenType.CASE && token.getType() != TokenType.CURLY_BRACKET_CLOSE  && token.getType() != TokenType.DEFAULT) {
            if (isOperator(token)) {
                operator();
            } else if (token.getType() == TokenType.BREAK) {
                nextToken(TokenType.BREAK, "Ожидался оператор break");
                nextToken(TokenType.SEMICOLON, "Ожидался символ ;");
            }
            token = nextTokenRead();
        }
    }

    private boolean isCaseOper(Token token) {
        return token.getType() == TokenType.BREAK || isOperator(token);
    }

    private boolean isDefault(Token token) {
        return token.getType() == TokenType.DEFAULT;
    }

    private boolean isCases(Token token) {
        return token.getType() == TokenType.CASE;
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
                token.getType() == TokenType.TYPE_DEC ||
                token.getType() == TokenType.TYPE_HEX ||
                token.getType() == TokenType.FALSE ||
                token.getType() == TokenType.TRUE;
    }

    public ProgramTree getTree() {
        return tree;
    }

    private Token nextToken() {
        return lexer.next();
    }

    private Token nextToken(TokenType type, String text) {
        Token token = lexer.next();
        if (token.getType() != type) {
            System.out.println(token);
            printError(text);
        }
        return token;
    }

    private Token nextTokenRead() {
        lexer.save();
        Token token = lexer.next();
        lexer.ret();
        return token;
    }

    public void printError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
        System.exit(1);
    }

    private void printSemError(String errorText) {
        System.out.println(errorText + " строка " + lexer.getNumberRow() + " столбик " + lexer.getNumberCol());
    }

    private Node expression1() {
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
        while  (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS || token.getType() == TokenType.NOT) {
            nextToken();
            token = nextTokenRead();
        }

        return elementaryExpression();

    }

    private Node elementaryExpression() {
        Token token = nextTokenRead();
        Node nodeFunc;

        if (token.getType() == TokenType.ID) {
            Token tokenName = token;
            nextToken();
            token = nextTokenRead();

            if (token.getType() == TokenType.BRACKET_OPEN) {
                nodeFunc = callFunction(tokenName);
                return nodeFunc;
            } else {
                // TODO!!!!
                if (thisTree.findUpVar(tokenName.getText()) != null) {
                    if (!parameters) {
                        return thisTree.node;
                    } else {
                        printSemError(tokenName.getText());
                    }
                } else {
                    printSemError(tokenName.getText());
                    return thisTree.node;
                }
            }


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
        else if (token.getType() == TokenType.TYPE_DEC) {
            nextToken();
            return Node.createConst(TypeData.INTEGER);
        }
        else if (token.getType() == TokenType.TYPE_HEX) {
            nextToken();
            return Node.createConst(TypeData.INTEGER);
        }
        else if (token.getType() == TokenType.TRUE) {
            nextToken();
            return Node.createConst(TypeData.BOOL);
        }
        else if (token.getType() == TokenType.FALSE) {
            nextToken();
            return Node.createConst(TypeData.BOOL);
        }
        else {
            printError("Ошибка");
            return null;
        }
        return null;
    }

    private boolean isExpression1(Token token) {
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
}
/*

bool a = true, b = false, c = 10 , d = -10;

int gg(bool t) {
    return t;
}

bool gg(int t) {
{int t, h;}
{int t, h;}
{int t, h;}
{int t, h;}
    int x;
    return b;
    switch (x) {
        case 1:
            break;

        case 2:
            c = gg(x);

        default:
        ;
    }

    int a = gg(9);
}

int main(int c) {
    main(3);
    int x;
    switch (x) {
        case 1:
            break;

        case 2:
            c = gg(x);

        default:
            break;
    }
}




 */
package lexer;

public class Lexer {

    private String text;
    private int numberRow;
    private int numberCol;
    private int numberSymbol;

    private int oldNumberRow;
    private int oldNumberCol;
    private int oldNumberSymbol;

    private String tokenMathSymbols = "+-/%*;(),{}";
    private TokenType[] tokenMathMass = {
            TokenType.PLUS,
            TokenType.MINUS,
            TokenType.DIVIDE,
            TokenType.MODULUS,
            TokenType.MULTIPLY,
            TokenType.BOOL.SEMICOLON,
            TokenType.BRACKET_OPEN,
            TokenType.BRACKET_CLOSE,
            TokenType.COMMA,
            TokenType.CURLY_BRACKET_OPEN,
            TokenType.CURLY_BRACKET_CLOSE
    };

    private String tokenDigitCompareSymbols = "<>=";
    private TokenType[] tokenDigitCompareMass = {
            TokenType.LESS,
            TokenType.MORE,
            TokenType.ASSIGN
    };

    private TokenType[] tokenDigitCompareEqualas = {
            TokenType.LESS_EQUAL,
            TokenType.MORE_EQUAL,
            TokenType.EQUAL,
            TokenType.NOT_EQUAL,
    };

    public Lexer(String text) {
        this.text = text;
        numberRow = 1;
        numberCol = 1;
    }

    public Token next() {
        ignoringSymbols();

        if (isIdSymbol(text.charAt(numberSymbol)))
            return id();
        if (text.charAt(numberSymbol) == '0')
            return typeHex();
        if (isCompareDigit(text.charAt(numberSymbol)))
            return compareDigit();
        if (text.charAt(numberSymbol) == '&' || text.charAt(numberSymbol) == '|')
            return compareLogical();
        if (text.charAt(numberSymbol) == '!')
            return lexerNotEqual();
        if (isMathOrSpec(text.charAt(numberSymbol)))
            return lexerMathOrSpec();
        if (isDigit(text.charAt(numberSymbol)))
            return typeInt();
        if (text.charAt(numberSymbol) == '\0')
            return eof();
        else {
            char ch = text.charAt(numberSymbol);
            addNumberSymbol();
            return lexerError("Неизвестный символ \'" + ch + "\'");
        }
    }

    private Token id() {
        String str = "" + text.charAt(numberSymbol);
        addNumberSymbol();
        while(isIdSymbol(text.charAt(numberSymbol)) || isDigit(text.charAt(numberSymbol))) {
            str += text.charAt(numberSymbol);
            addNumberSymbol();
        }
        if (str.equals("int"))
            return new Token(str, TokenType.INT);
        if (str.equals("bool"))
            return new Token(str, TokenType.BOOL);
        if (str.equals("void"))
            return new Token(str, TokenType.VOID);
        if (str.equals("TRUE") || str.equals("true"))
            return new Token(str, TokenType.TRUE);
        if (str.equals("FALSE") || str.equals("false"))
            return new Token(str, TokenType.FALSE);
        if (str.equals("return"))
            return new Token(str, TokenType.RETURN);
        if (str.equals("switch"))
            return new Token(str, TokenType.SWITCH);
        if (str.equals("case"))
            return new Token(str, TokenType.CASE);
        if (str.equals("default"))
            return new Token(str, TokenType.DEFAULT);

        return new Token(str, TokenType.ID);
    }

    private Token compareDigit() {
        int index = tokenDigitCompareSymbols.indexOf(text.charAt(numberSymbol));
        addNumberSymbol();
        if (text.charAt(numberSymbol) == '=') {
            addNumberSymbol();
            return new Token(tokenDigitCompareEqualas[index]);
        } else
            return new Token(tokenDigitCompareMass[index]);
    }

    private Token compareLogical() {
        if (text.charAt(numberSymbol) == '&') {
            addNumberSymbol();
            if (text.charAt(numberSymbol) == '&') {
                addNumberSymbol();
                return new Token(TokenType.AND);
            }
        }
        if (text.charAt(numberSymbol) == '|') {
            addNumberSymbol();
            if (text.charAt(numberSymbol) == '|') {
                addNumberSymbol();
                return new Token(TokenType.OR);
            }
        }
        addNumberSymbol();
        return lexerError("Неизвестный оператор");
    }

    private Token lexerNotEqual() {
        addNumberSymbol();
        if (text.charAt(numberSymbol) == '=') {
            addNumberSymbol();
            return new Token(TokenType.NOT_EQUAL);
        }
        return new Token(TokenType.NOT);
    }

    private Token lexerMathOrSpec() {
        int index = tokenMathSymbols.indexOf(text.charAt(numberSymbol));
        addNumberSymbol();
        return new Token(tokenMathMass[index]);
    }


    private Token typeInt() {
        String str = "" + text.charAt(numberSymbol);
        addNumberSymbol();
        while (isDigit(text.charAt(numberSymbol))) {
            str += text.charAt(numberSymbol);
            addNumberSymbol();
        }
        try {
            Integer.parseInt(str);
            return new Token(str, TokenType.TYPE_INT);
        } catch (NumberFormatException e) {
            return lexerError("Ошибка считывания TYPE_INT. Слишком длинная константа");
        }
    }

    private Token typeHex() {
        String str = "" + text.charAt(numberSymbol);
        if (text.charAt(numberSymbol) == '0') {
            addNumberSymbol();
            if (text.charAt(numberSymbol) == 'x' || text.charAt(numberSymbol) == 'X') {
                str += text.charAt(numberSymbol);
                addNumberSymbol();
                if (!isDigit(text.charAt(numberSymbol)) && !(isHexAlphabet(text.charAt(numberSymbol))))
                    return lexerError("Ошибка считывания TYPE_HEX");
                else {
                    while (isHexAlphabet(text.charAt(numberSymbol)) || isDigit(text.charAt(numberSymbol))) {
                        str += text.charAt(numberSymbol);
                        addNumberSymbol();
                    }
                    return new Token(TokenType.TYPE_HEX);
                }
            }
            return new Token(TokenType.TYPE_INT);
        }
        return new Token(str, TokenType.ID);
    }

    private boolean isMathOrSpec(char c) {
        return tokenMathSymbols.indexOf(c) != -1;
    }

    private void ignoringSymbols() {
        boolean canLexer = false;
        while (!canLexer) {
            canLexer = true;

            while ( text.charAt(numberSymbol) == ' ' ||
                    text.charAt(numberSymbol) == '\t' ||
                    text.charAt(numberSymbol) == '\n' ||
                    text.charAt(numberSymbol) == '\r')
            {
                addNumberSymbol();
                canLexer = false;
            }

            while (text.charAt(numberSymbol) == '/') {
                addNumberSymbol();
                if (text.charAt(numberSymbol) == '/') {
                    while (text.charAt(numberSymbol) != '\n')
                        addNumberSymbol();
                    addNumberSymbol();
                    canLexer = false;
                }
            }
        }
    }

    private void addNumberSymbol() {
        numberCol++;
        if (text.charAt(numberSymbol) == '\n') {
            numberRow++;
            numberCol = 1;
        }
        numberSymbol++;
    }

    private boolean isIdSymbol(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_';
    }

    private boolean isHexAlphabet(char c) {
        return ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private boolean isCompareDigit(char c) {
        return tokenDigitCompareSymbols.indexOf(c) != -1;
    }

    private Token eof() {
        return new Token(TokenType.EOF);
    }

    private Token lexerError(String s) {
        return new Token(s + " row " + numberRow + " col " + numberCol + " ", TokenType.ERROR);
    }

    public int getNumberRow() {
        return numberRow;
    }

    public int getNumberCol() {
        return numberCol;
    }

    public void save() {
        oldNumberCol = numberCol;
        oldNumberRow = numberRow;
        oldNumberSymbol = numberSymbol;
    }

    public void ret() {
        numberCol = oldNumberCol;
        numberRow = oldNumberRow;
        numberSymbol = oldNumberSymbol;
    }
}

import io.Reader;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class MainLexer {
    public static void main(String[] args) {
        String code;
        try {
            code = Reader.getCode("code.mcpp");
        } catch (Exception e) {
            System.out.println("Неверный тип файла");
            return;
        }

        /*
        Token token = new Token("main", TokenType.MAIN);
        String str = token.toString();
        System.out.println(str);
        */


        Lexer lexer = new Lexer(code);
        Token token;
        try {
            do {
                token = lexer.next();
                System.out.println(token);
            } while (token.getType() != TokenType.EOF);
        } catch (Exception e) {
            e.fillInStackTrace();
        }

    }
}

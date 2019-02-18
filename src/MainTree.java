import io.Reader;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import syntax_analyzer.Analysator;

public class MainTree {
    public static void main(String[] args) {
        String text;
        try {
            text = Reader.getCode("code.mcpp");
        } catch (Exception e) {
            System.out.println("Неверны тип файла");
            return;
        }

        Lexer lexer = new Lexer(text);
        Analysator analysator = new Analysator(lexer);

        analysator.program();

        Token token = lexer.next();
        if (token.getType() == TokenType.EOF)
            System.out.println("Синтаксических ошибок не обнаружено!");
        else
            analysator.printError("Лишний текст в конце программы");

        System.out.println();
        analysator.getTree().print(0);
    }
}

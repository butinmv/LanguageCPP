import io.Reader;
import lexer.Lexer;
import syntax_analyzer.Analysator;
import syntax_analyzer.SyntaxAnalyzer;

public class MainSyntaxAnalyzer {
    public static void main(String[] args) {
        String code;
        try {
            code = Reader.getCode("code.mcpp");
        } catch (Exception e) {
            System.out.println("Неверный тип файла");
            return;
        }

        Lexer lexer = new Lexer(code);
        Analysator syntaxAnalyzer = new Analysator(lexer);

        syntaxAnalyzer.program();
        System.out.println("Ваш код идеален!");
    }
}

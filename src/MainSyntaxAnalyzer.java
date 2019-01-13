import io.Reader;
import lexer.Lexer;
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
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexer);

        syntaxAnalyzer.program();
    }
}

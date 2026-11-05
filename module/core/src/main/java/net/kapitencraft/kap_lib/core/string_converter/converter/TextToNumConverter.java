package net.kapitencraft.kap_lib.core.string_converter.converter;

import net.kapitencraft.kap_lib.core.string_converter.Token;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class TextToNumConverter<T extends Number> extends TextConverter<T> {

    public TextToNumConverter(Operation<T> operation) {
        super(operation);
    }

    protected static List<Token> parseTokens(String args) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < args.length()) {
            switch (args.charAt(i)) {
                case '/' -> tokens.add(new Token("/", Token.TokenType.DIV));
                case '*' -> tokens.add(new Token("*", Token.TokenType.MUL));
                case '+' -> tokens.add(new Token("+", Token.TokenType.ADD));
                case '-' -> tokens.add(new Token("-", Token.TokenType.SUB));
                case '%' -> tokens.add(new Token("%", Token.TokenType.MOD));
                case '^' -> tokens.add(new Token("^", Token.TokenType.POW));
                case ' ' -> {}
                default -> {
                    int sI = i;
                    if (args.charAt(i) >= '0' && args.charAt(i) <= '9') {
                        do {
                            i++;
                        } while (i < args.length() && Character.isDigit(args.charAt(i)));
                        tokens.add(new Token(args.substring(sI, i), Token.TokenType.LIT));
                    } else {
                        do {
                            i++;
                        } while (i < args.length() && Character.isAlphabetic(args.charAt(i)));
                        tokens.add(new Token(args.substring(sI, i), Token.TokenType.REF));
                    }
                }
            }
            i++;
        }
        return tokens;
    }
}
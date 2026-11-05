package net.kapitencraft.kap_lib.core.string_converter;

import net.kapitencraft.kap_lib.core.string_converter.args.LiteralValueOperation;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;

import java.util.List;

public class AlgebraParser<T extends Number> {
    private final List<Token> code;
    private int idx = 0;
    private final AlgebraOperationsStorage<T> literalConverter;

    public AlgebraParser(List<Token> code, AlgebraOperationsStorage<T> literalConverter) {
        this.code = code;
        this.literalConverter = literalConverter;
    }

    public Operation<T> parse() {
        return dashAlgebra();
    }

    private Operation<T> dashAlgebra() {
        Operation<T> left = dotAlgebra();

        while (check(Token.TokenType.ADD, Token.TokenType.SUB)) {
            Token token = advance();
            Operation<T> right = dotAlgebra();

            switch (token.type()) {
                case ADD -> left = literalConverter.add().apply(left, right);
                case SUB -> left = literalConverter.sub().apply(left, right);
            }
        }
        return left;
    }

    private Operation<T> dotAlgebra() {
        Operation<T> left = powAlgebra();

        while (check(Token.TokenType.MUL, Token.TokenType.DIV, Token.TokenType.MOD)) {
            Token token = advance();
            Operation<T> right = powAlgebra();

            switch (token.type()) {
                case MUL -> left = literalConverter.mul().apply(left, right);
                case DIV -> left = literalConverter.div().apply(left, right);
                case MOD -> left = literalConverter.mod().apply(left, right);
            }
        }
        return left;
    }

    private Operation<T> powAlgebra() {
        Operation<T> left = literal();

        while (check(Token.TokenType.POW)) {
            advance();
            Operation<T> right = literal();
            left = literalConverter.pow().apply(left, right);
        }
        return left;
    }

    private Operation<T> literal() {
        if (check(Token.TokenType.LIT)) {
            return new LiteralValueOperation<>(literalConverter.literalExtractor().apply(advance().name()));
        }
        return literalConverter.refExtractor().apply(advance().name());
    }

    private boolean check(Token.TokenType... types) {
        if (isAtEnd()) return false;
        Token.TokenType type = peek().type();
        for (Token.TokenType tokenType : types) {
            if (tokenType == type) {
                return true;
            }
        }
        return false;
    }

    private boolean isAtEnd() {
        return code.size() <= idx;
    }

    private Token peek() {
        return code.get(idx);
    }

    private Token advance() {
        return code.get(idx++);
    }
}

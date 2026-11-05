package net.kapitencraft.kap_lib.core.string_converter;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record Token(String name, TokenType type) {

    public enum TokenType {
        DIV, MUL,
        ADD, SUB,
        MOD, POW,
        REF, LIT;
    }
}

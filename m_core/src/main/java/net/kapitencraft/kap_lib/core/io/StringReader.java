package net.kapitencraft.kap_lib.core.io;

import java.util.ArrayList;
import java.util.List;

public class StringReader {

    public static List<String> collectBracketContent(String in, char openBracket, char closeBracket) {
        List<String> strings = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean adding = false;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == openBracket) {
                if (adding) throw new IllegalStateException("detected missing closing ]");
                adding = true;
            } else if (c == closeBracket) {
                if (adding) {
                    strings.add(builder.toString());
                    builder = new StringBuilder();
                    adding = false;
                }
            } else if (adding) {
                builder.append(in.charAt(i));
            }
        }
        return strings;
    }

}

package net.kapitencraft.kap_lib.publish.changelog;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Objects;

public final class PlainChangelog implements Changelog {
    private String[] content;

    //add HTML / MD line feed character
    private static String makeLineFeedFormatting(String[] lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            builder.append(line);
            if (!line.startsWith("#") && !line.isEmpty() && i < line.length() - 1)
                builder.append("<br>");
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toHtml() {
        return makeLineFeedFormatting(content);
    }

    @Override
    public String toMd() {
        return makeLineFeedFormatting(content);
    }

    @Override
    public String toPlainText() {
        return String.join("\n", content);
    }

    @Override
    public void parse(BufferedReader reader) {
        content = reader.lines().toArray(String[]::new);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PlainChangelog) obj;
        return Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "PlainChangelog[" +
                "content=" + Arrays.toString(content) + ']';
    }
}
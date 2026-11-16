package net.kapitencraft.kap_lib.publish.changelog;

import java.io.BufferedReader;

public interface Changelog {

    String toHtml();

    String toMd();

    String toPlainText();

    void parse(BufferedReader reader);
}
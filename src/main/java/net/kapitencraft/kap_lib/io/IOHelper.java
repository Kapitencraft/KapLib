package net.kapitencraft.kap_lib.io;

import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOHelper {
    public static File SCRIPTED_DIRECTORY = new File("./scripted"); //bruh

    private IOHelper() {
    }

    public static List<File> listResources(File file) {
        if (!file.exists()) return List.of();
        if (!file.isDirectory()) return List.of(file);
        List<File> finals = new ArrayList<>();
        List<File> queue = new ArrayList<>();
        queue.add(file);
        while (!queue.isEmpty()) {
            if (queue.get(0).isDirectory()) {
                String[] childNames = queue.get(0).list();
                if (childNames != null) for (String childName : childNames) {
                    queue.add(new File(queue.get(0), childName));
                }
            } else {
                finals.add(queue.get(0));
            }
            queue.remove(0);
        }
        return finals;
    }

    public static List<ResourceLocation> toNames(List<File> files) {
        File file = new File("test");
        return files.stream().map(File::getPath).map(s -> {
            String[] directories = s.split("\\\\"); //why 4 bro?
            return new ResourceLocation("a");
        }).toList();
    }

    /**
     * @param in the String to check
     * @param openRegex the char that opens the bracket
     * @param closeRegex the char that closes the bracket
     * @return a list of StringSegments that contain the start and end position and the substring inclusive the brackets
     */
    public static List<StringSegment> collectBracketContent(String in, String openRegex, String closeRegex) {
        List<StringSegment> strings = new ArrayList<>();
        Matcher openMatcher = Pattern.compile(openRegex).matcher(in);
        Matcher closeMatcher = Pattern.compile(closeRegex).matcher(in);
        for (int i = 0; i < in.length(); i++) {
            if (openMatcher.find(i) && closeMatcher.find(openMatcher.end())) {
                strings.add(StringSegment.fromString(openMatcher.start(), closeMatcher.end(), in));
            }
        }
        return strings;
    }
}
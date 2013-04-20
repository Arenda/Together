package com.sysfeather.together.util;

public class StringUtil {
    public static String replaceLineFeed(String input) {
        return input.replace("'", "\\'").replace("\\r\\n\\r\\n", "</p><p>").replace("\\n\\n", "</p><p>")
                .replace("\\r\\n", "<br />").replace("\\n", "<br />");
    }
}

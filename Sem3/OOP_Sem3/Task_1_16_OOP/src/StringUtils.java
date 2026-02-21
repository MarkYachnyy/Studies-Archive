import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static String padRight(String s, int n, char padding) {
        StringBuilder builder = new StringBuilder(s.length() + n);
        builder.append(s);
        for (int i = 0; i < n; i++) {
            builder.append(padding);
        }
        return builder.toString();
    }

    public static String padLeft(String s, int n, char padding) {
        StringBuilder builder = new StringBuilder(s.length() + n);
        for (int i = 0; i < n; i++) {
            builder.append(padding);
        }
        return builder.append(s).toString();
    }

    public static String red(String s) {
        return ANSI_RED + s + ANSI_RESET;
    }

    public static String yellow(String s) {
        return ANSI_YELLOW + s + ANSI_RESET;
    }

    public static int realLength(String s) {
        int res = s.length();
        Pattern pattern = Pattern.compile("\\u001B\\[0m");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            res -= 4;
        }
        pattern = Pattern.compile("\\u001B\\[31m");
        matcher = pattern.matcher(s);
        while (matcher.find()) {
            res -= 5;
        }
        pattern = Pattern.compile("\\u001B\\[33m");
        matcher = pattern.matcher(s);
        while (matcher.find()) {
            res -= 5;
        }
        return res;
    }

}

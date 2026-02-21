package ru.cs.vsu.yachnyy_m_a;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task11 {
    public static String processWithRegex(String text) {
        Pattern sentenceRegex = Pattern.compile("[A-Za-zА-Яа-я0-9,\\s]+[?!.](\\s|$)");
        Matcher sentenceMatcher = sentenceRegex.matcher(text);
        Pattern wordRegex = Pattern.compile("[A-Za-zА-Яа-я0-9]+");
        String result = "";
        int maxWordCount = 0;
        while (sentenceMatcher.find()) {
            int start = sentenceMatcher.start();
            int end = sentenceMatcher.end();
            String sentence = text.substring(start, end);
            Matcher wordMatcher = wordRegex.matcher(sentence);
            int wordCount = 0;
            while (wordMatcher.find()) {
                wordCount++;
            }
            if (wordCount > maxWordCount) {
                result = sentence;
                maxWordCount = wordCount;
            }
        }
        return result;
    }
}

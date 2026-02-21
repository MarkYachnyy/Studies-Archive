import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ConsoleFileComparator {

    public static class FileCompareResult {
        private Iterable<LineCompareResult> lineComparisons;

        private FileCompareResult(Iterable<LineCompareResult> lineComparison) {
            this.lineComparisons = lineComparison;
        }

        public Iterable<LineCompareResult> getLineComparisons() {
            return lineComparisons;
        }

        public void print() {
            for(LineCompareResult result: lineComparisons){
                StringBuilder builder = new StringBuilder();
                for (WordData wordData: result.getLine1Data()){
                    builder.append(wordData.status == WordData.COINCIDES ? wordData.value : wordData.status == WordData.SLIGHTLY_DIFFERS ?
                            StringUtils.yellow(wordData.value) : StringUtils.red(wordData.value)).append(' ');
                }
                builder = new StringBuilder(StringUtils.padRight(builder.toString(), 50 - StringUtils.realLength(builder.toString()), ' '));
                builder.append('|');
                for (WordData wordData: result.getLine2Data()){
                    builder.append(wordData.status == WordData.COINCIDES ? wordData.value : wordData.status == WordData.SLIGHTLY_DIFFERS ?
                            StringUtils.yellow(wordData.value) : StringUtils.red(wordData.value)).append(' ');
                }
                System.out.println(builder);
            }
        }
    }

    public static class LineCompareResult {
        private WordData[] line1Data;
        private WordData[] line2Data;

        private LineCompareResult(WordData[] line1Data, WordData[] line2Data) {
            this.line1Data = line1Data;
            this.line2Data = line2Data;
        }

        public WordData[] getLine1Data() {
            return line1Data;
        }

        public WordData[] getLine2Data() {
            return line2Data;
        }
    }

    public static class WordData {
        private String value;
        private int status;

        public static final int COINCIDES = 1;
        public static final int IS_ABSENT = 0;
        public static final int SLIGHTLY_DIFFERS = -1;

        private WordData(String value, int status) {
            this.value = value;
            this.status = status;
        }

        public String getValue() {
            return value;
        }

        public int getStatus() {
            return status;
        }
    }

    public ConsoleFileComparator() {
    }

    public FileCompareResult compare(String filename1, String filename2) throws FileNotFoundException {
        File file1 = new File("./file1.txt");
        Scanner scanner1 = new Scanner(file1);
        File file2 = new File("./file2.txt");
        Scanner scanner2 = new Scanner(file2);

        LinkedList<LineCompareResult> lineCompareResults = new LinkedList<>();

        while (scanner1.hasNext() || scanner2.hasNext()) {
            if (scanner1.hasNext() ^ scanner2.hasNext()) {
                throw new IllegalArgumentException("Line count in both files must be similar!");
            }

            String[] words1 = scanner1.nextLine().split(" +");
            String[] words2 = scanner2.nextLine().split(" +");

            int[] words1Status = new int[words1.length];
            int[] words2Status = new int[words2.length];

            for (int i = 0; i < words1.length; i++) {
                for (int j = 0; j < words2.length; j++) {
                    if (words2Status[j] == WordData.COINCIDES) continue;
                    if (compareWords(words1[i], words2[j]) == 0) {
                        words1Status[i] = WordData.COINCIDES;
                        words2Status[j] = WordData.COINCIDES;
                        break;
                    }
                }
            }

            for (int i = 0; i < words1.length; i++) {
                if(words1Status[i] == WordData.COINCIDES) continue;
                for (int j = 0; j < words2.length; j++) {
                    if(words2Status[j] == WordData.COINCIDES || words2Status[j] == WordData.SLIGHTLY_DIFFERS) continue;
                    if(compareWords(words1[i], words2[j]) == 1){
                        words1Status[i] = WordData.SLIGHTLY_DIFFERS;
                        words2Status[j] = WordData.SLIGHTLY_DIFFERS;
                        break;
                    }
                }
            }

            lineCompareResults.add(new LineCompareResult(IntStream.range(0, words1.length).mapToObj(i -> new WordData(words1[i], words1Status[i])).toArray(WordData[]::new),
                    IntStream.range(0, words2.length).mapToObj(i -> new WordData(words2[i], words2Status[i])).toArray(WordData[]::new)));
        }

        return new FileCompareResult(lineCompareResults);
    }

    private int compareWords(String w1, String w2) {

        if (w1.length() != w2.length()) {
            return -1;
        }
        int diffCount = 0;
        for (int i = 0; i < w1.length(); i++) {
            if (w1.charAt(i) != w2.charAt(i)) diffCount++;
            if (diffCount > 2) return -1;
        }
        if (diffCount == 0) {
            return 0;
        } else {
            return 1;
        }

    }
}

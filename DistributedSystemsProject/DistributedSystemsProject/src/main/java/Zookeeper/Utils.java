package Zookeeper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int[] extractIntegers(String mixedString) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(mixedString);
        int[] integers = new int[matcher.groupCount()];
        int index = 0;

        while (matcher.find()) {
            integers[index] = Integer.parseInt(matcher.group());
            index++;
        }

        return integers;
    }

}

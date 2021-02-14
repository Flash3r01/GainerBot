package gainerbot.services;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFormatService {

    public static String withSeparators(String numberString){
        return withSeparators(numberString, '.', 3);
    }

    public static String withSeparators(String numberString, char separator, int separatorDistance){
        if(separatorDistance <= 0) throw new IllegalArgumentException("SeparatorDistance cant be smaller than 1.");
        Matcher matcher = Pattern.compile("^\\d+$").matcher(numberString);
        if(!matcher.find()) throw new IllegalArgumentException("The input string can only consist of digits 0-9");

        int lastProcessedIndex = numberString.length() + 1;
        int nextIndex = lastProcessedIndex - separatorDistance;
        StringBuilder formatedNumber = new StringBuilder(numberString.substring(nextIndex, lastProcessedIndex));
        lastProcessedIndex = nextIndex;
        nextIndex = lastProcessedIndex - separatorDistance;

        while(nextIndex >= 0){
            formatedNumber.insert(0, numberString.substring(nextIndex, lastProcessedIndex) + separator);
            lastProcessedIndex = nextIndex;
            nextIndex = lastProcessedIndex - separatorDistance;
        }
        if(lastProcessedIndex != 0){
            formatedNumber.insert(0, numberString.substring(0, lastProcessedIndex) + separator);
        }

        return formatedNumber.toString();
    }

    public static String withSeparators(int number){
        return withSeparators(number, '.', 1000);
    }

    public static String withSeparators(int number, char separator, int separatorDistance){
        if(!is10Number(separatorDistance)) throw new IllegalArgumentException("SeparatorDistance has to be 1, 10, 100...");
        if(number == 0) return "0";

        ArrayList<Integer> integers = new ArrayList<>();
        int unprocessed = number;
        while(unprocessed != 0){
            integers.add(unprocessed % separatorDistance);
            unprocessed /= separatorDistance;
        }

        StringBuilder builder = new StringBuilder();
        if(number < 0)builder.append('-');

        for(int i = integers.size() - 1; i > 0; i--){
            builder.append(integers.get(i));
            builder.append(separator);
        }
        builder.append(integers.get(0));

        return builder.toString();
    }

    private static boolean is10Number(int number){
        int current = 1;
        while (current < number) current *= 10;

        return current == number;
    }
}

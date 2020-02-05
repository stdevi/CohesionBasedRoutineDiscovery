package utils;

import java.util.*;
import java.util.stream.Collectors;

public class Formatter {

    public static StringBuilder formatData(StringBuilder data) {
        Map<String, String> actionCode = new HashMap<>();
        Integer[] code = {0};

        String res = Arrays.stream(data.toString().split("\n")).map(itemset -> {
            String[] split = itemset.split(" -1 ");
            List<String> actions = new ArrayList<>(Arrays.asList(split));
            actions.removeIf(el -> el.equals("-2"));
            actions.forEach(action -> actionCode.computeIfAbsent(action, k -> String.valueOf(code[0]++)));
            return actions.stream().map(actionCode::get).collect(Collectors.joining(" -1 "));
        }).collect(Collectors.joining(" -1 -2\n", "", " -1 -2"));

        StringBuilder stringBuilder = new StringBuilder(res);
        actionCode.forEach((key, value) ->
                stringBuilder.insert(0, String.format("@ITEM=%s=%s\n", value, key))
        );
        stringBuilder.insert(0, "@CONVERTED_FROM_TEXT\n");

        return stringBuilder;
    }
}

package cohesion;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CohesionScorer {
    private List<String> sequences;
    private List<List<String>> itemsets;
    private Map<List<String>, Integer> scores;

    public CohesionScorer(List<String> sequences, List<List<String>> itemsets) {
        this.sequences = sequences;
        this.itemsets = itemsets;
    }

    public Map<List<String>, Integer> findScores() {
        scores = new HashMap<>();
        itemsets.forEach(itemset -> scores.put(itemset, getCohesionItemsetScore(itemset)));

        return scores;
    }

    public void printSortedScores() {
        if (scores != null) {
            scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(System.out::println);
        }
    }


    private int getCohesionItemsetScore(List<String> itemset) {
        int medianOutlierCount = getMedianOutlierCount(itemset);

        return itemset.size() - medianOutlierCount;
    }

    private int getMedianOutlierCount(List<String> itemset) {
        List<Integer> list = getOutlierLengthList(itemset);
        Collections.sort(list);

        int median;
        if (list.size() % 2 == 0)
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        else
            median = list.get(list.size() / 2);

        return median;
    }

    private List<Integer> getOutlierLengthList(List<String> itemset) {
        List<Integer> lengthsList = new ArrayList<>();
        sequences.forEach(sequence -> lengthsList.add(getOutliers(sequence, itemset).size()));

        return lengthsList;
    }

    private List<String> getOutliers(String sequence, List<String> itemset) {
        List<String> outliers = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        Matcher m = getPattern(itemset).matcher(sequence);

        for (int i = 2; i < 2 * itemset.size() - 1; i += 2) {
            indexes.add(i);
        }

        if (m.find()) {
            indexes.forEach(index -> outliers.addAll(Arrays.asList(m.group(index).split(","))));
            StringJoiner anyElement = new StringJoiner("|", "[", "]");
            itemset.forEach(anyElement::add);
            outliers.removeIf(item -> item == null || "".equals(item) || item.matches(anyElement.toString()));
        }

        return outliers;
    }

    private Pattern getPattern(List<String> itemset) {
        StringBuilder regex = new StringBuilder();
        StringJoiner itemJoiner = new StringJoiner("|", "([", "])");
        itemset.forEach(itemJoiner::add);
        String item = itemJoiner.toString();

        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < 2 * itemset.size() - 1; i += 2) {
            indexes.add(i);
        }

        regex.append(item);
        for (int i = 1; i < itemset.size(); i++) {
            StringJoiner itemIndexJoiner = new StringJoiner("|", "(?!", ")");
            for (int j = 0; j < i; j++) {
                itemIndexJoiner.add("\\" + indexes.get(j).toString());
            }

            if (i == 1) {
                regex.append("((?!.*?(?:\\1)).*?)").append(itemIndexJoiner).append(item);
            } else {
                regex.append("(.*?)").append(itemIndexJoiner).append(item);
            }
        }

        return Pattern.compile(regex.toString());
    }
}

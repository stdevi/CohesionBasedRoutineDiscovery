package cohesion;

import entity.Pattern;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class CohesionScorer {
    private List<String> sequences;
    private List<Pattern> patterns;
    private Map<Pattern, Integer> scores;

    public List<Pattern> findScoresForPatterns(List<String> sequences, List<Pattern> patterns) {
        this.sequences = sequences;
        this.patterns = patterns;

        patterns = patterns.stream()
                .peek(pattern -> pattern.setCohesionScore(getCohesionPatternScore(pattern.getItems())))
                .collect(Collectors.toList());

        return patterns;
    }

    public Map<Pattern, Integer> findScores() {
        scores = new HashMap<>();
        patterns.forEach(pattern -> scores.put(pattern, getCohesionPatternScore(pattern.getItems())));

        return scores;
    }

    public void printSortedScores() {
        if (scores != null) {
            scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(System.out::println);
        }
    }


    private int getCohesionPatternScore(List<String> pattern) {
        int medianOutlierCount = getMedianOutlierCount(pattern);

        return pattern.size() - medianOutlierCount;
    }

    private int getMedianOutlierCount(List<String> pattern) {
        List<Integer> list = getOutlierLengthList(pattern);
        Collections.sort(list);

        int median;
        if (list.size() % 2 == 0)
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        else
            median = list.get(list.size() / 2);

        return median;
    }

    private List<Integer> getOutlierLengthList(List<String> pattern) {
        List<Integer> lengthsList = new ArrayList<>();
        sequences.forEach(sequence -> lengthsList.add(getOutliers(sequence, pattern).size()));

        return lengthsList;
    }

    private List<String> getOutliers(String sequence, List<String> pattern) {
        List<String> outliers = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        Matcher m = getPattern(pattern).matcher(sequence);

        for (int i = 2; i < 2 * pattern.size() - 1; i += 2) {
            indexes.add(i);
        }

        if (m.find()) {
            indexes.forEach(index -> {
                outliers.addAll(Arrays.asList(m.group(index).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")));
            });
            StringJoiner anyElement = new StringJoiner("|", "", "");
            pattern.forEach(anyElement::add);
            outliers.removeIf(item -> item == null || "".equals(item) || item.matches(anyElement.toString()));
        }

        return outliers;
    }

    private java.util.regex.Pattern getPattern(List<String> pattern) {
        StringBuilder regex = new StringBuilder();
        StringJoiner itemJoiner = new StringJoiner("|", "(", ")");
        pattern.forEach(itemJoiner::add);
        String item = itemJoiner.toString();

        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < 2 * pattern.size() - 1; i += 2) {
            indexes.add(i);
        }

        regex.append(item);
        for (int i = 1; i < pattern.size(); i++) {
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

        return java.util.regex.Pattern.compile(regex.toString());
    }
}

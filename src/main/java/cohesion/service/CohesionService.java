package cohesion.service;

import pattern.entity.Pattern;
import sequence.Sequence;

import java.util.*;
import java.util.stream.Collectors;

public class CohesionService {
    private static final CohesionService INSTANCE = new CohesionService();

    private CohesionService() {

    }

    public static CohesionService getInstance() {
        return INSTANCE;
    }

    public int getCohesionScore(Pattern pattern, List<Sequence> sequences) {
        int medianOutlierCount = getMedianOutlierCount(pattern, sequences);

        return pattern.getItems().size() - medianOutlierCount;
    }

    private int getMedianOutlierCount(Pattern pattern, List<Sequence> sequences) {
        List<Integer> list = getOutlierLengthList(pattern, sequences);
        Collections.sort(list);

        int median;
        if (list.size() % 2 == 0)
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        else
            median = list.get(list.size() / 2);

        return median;
    }

    private List<Integer> getOutlierLengthList(Pattern pattern, List<Sequence> sequences) {
        List<Integer> lengthsList = new ArrayList<>();
        sequences.forEach(sequence -> lengthsList.add(getOutliers(pattern, sequence).size()));

        return lengthsList;
    }

    private List<String> getOutliers(Pattern pattern, Sequence sequence) {
        List<String> window = getMinimalWindow(pattern, sequence);

        return getPatternOutliersInWindow(pattern, window);
    }

    private List<String> getPatternOutliersInWindow(Pattern pattern, List<String> window) {
        return window.stream()
                .filter(el -> !new TreeSet<>(pattern.getItemsValues()).contains(el))
                .collect(Collectors.toList());
    }

    private List<String> getMinimalWindow(Pattern pattern, Sequence sequence) {
        List<List<String>> windows = getWindows(pattern, sequence);
        windows = windows.stream().map(this::minimizeWindow).collect(Collectors.toList());
        windows.sort(Comparator.comparingInt(List::size));

        return windows.size() > 0 ? windows.get(0) : new ArrayList<>();
    }

    private List<List<String>> getWindows(Pattern pattern, Sequence sequence) {
        List<List<String>> windows = new ArrayList<>();
        List<String> window = new ArrayList<>();
        Map<String, Boolean> patternMap = new HashMap<>();
        for (String event : pattern.getItemsValues()) {
            patternMap.putIfAbsent(event, false);
        }

        int index = indexOfFirstOccurrence(pattern, sequence);
        if (index == -1) {
            return windows;
        }

        for (int i = index; i < sequence.getItems().size(); i++) {
            String event = sequence.getItems().get(i);

            if (patternMap.get(event) != null) {
                patternMap.put(event, true);
            }

            window.add(event);

            if (patternMap.values().stream().allMatch(v -> v)) {
                windows.add(new ArrayList<>(window));
                patternMap.keySet().forEach(key -> patternMap.put(key, false));
                window.clear();
            }
        }

        return windows;
    }

    private int indexOfFirstOccurrence(Pattern pattern, Sequence sequence) {
        List<Integer> indexes = new ArrayList<>();
        Set<String> uniquePatternItems = new TreeSet<>(pattern.getItemsValues());

        for (String item : uniquePatternItems) {
            indexes.add(sequence.getItems().indexOf(item));
        }

        indexes = indexes.stream().filter(index -> index != -1).collect(Collectors.toList());
        indexes.sort(Integer::compareTo);

        return indexes.size() >= uniquePatternItems.size() ? indexes.get(0) : -1;
    }

    private List<String> minimizeWindow(List<String> window) {
        List<String> minWindow = new ArrayList<>();
        for (String event : window) {
            if (minWindow.size() > 0 && minWindow.get(0).equals(event)) {
                minWindow.remove(0);
                minWindow = minimizeWindow(minWindow);
            }

            minWindow.add(event);
        }

        return minWindow;
    }
}

package spmf;

import entity.Action;

import java.util.ArrayList;
import java.util.List;

public class SPMFParser {
    private static List<List<Action>> sequences;

    public SPMFParser() {
        sequences = new ArrayList<>();
    }

    public StringBuilder transformSequencesToSPMF(List<List<Action>> sequences) {
        StringBuilder result = new StringBuilder();

        for (List<Action> sequence : sequences) {
            for (Action action : sequence) {
                assembleAction(result, action, true);
            }
            result.append("-2\n");
        }

        return result;
    }

    public StringBuilder transformActionsToSPMF(List<Action> actions) {
        createSequencesFromActions(actions);
        StringBuilder result = new StringBuilder();

        for (List<Action> sequence : sequences) {
            for (Action action : sequence) {
                assembleAction(result, action, true);
            }
            result.append("-2\n");
        }

        return result;
    }

    private void createSequencesFromActions(List<Action> actions) {
        List<Action> sequence = new ArrayList<>();
        for (Action action : actions) {
            sequence.add(action);
            if ((action.getLabel() != null && action.getLabel().equals("confirm")) ||
                    (action.getType() != null && action.getType().equals("submit"))) {
                sequences.add(new ArrayList<>(sequence));
                sequence.clear();
            }
        }

        if (sequence.size() != 0) {
            sequences.add(new ArrayList<>(sequence));
        }
    }

    private void assembleAction(StringBuilder result, Action action, boolean columnPresented) {
        result.append(action.getConceptName());

        if (columnPresented) {
            if (action.getColumn() != null) {
                result.append("_").append(action.getColumn()).append(" -1 ");
                return;
            }
        } else {
            if (action.getRow() != null) {
                result.append("_").append(action.getRow()).append(" -1 ");
                return;
            }
        }

        if (action.getLabel() != null) {
            result.append("_").append(action.getLabel().replaceAll("\\s+", "")).append(" -1 ");
            return;
        }

        result.append(" -1 ");
    }
}

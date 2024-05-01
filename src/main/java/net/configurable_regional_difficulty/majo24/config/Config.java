package net.configurable_regional_difficulty.majo24.config;

import net.configurable_regional_difficulty.majo24.config.selection.Selection;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private final ArrayList<Selection> selections;

    public Config() {
        selections = new ArrayList<>();
    }

    public List<Selection> getSelections() {
        return selections;
    }

    public void addSelection(Selection selection) {
        selections.add(selection);
    }

    /**
    @return True if removal was successful
     */
    public boolean removeSelection(Selection selectionToRemove) {
        for (Selection selection : selections) {
            if (selection.getSelection().equals(selectionToRemove.getSelection())) {
                return selections.remove(selection);
            }
        }
        return false;
    }
}

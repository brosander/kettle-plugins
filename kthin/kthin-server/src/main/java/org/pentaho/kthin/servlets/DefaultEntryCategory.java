package org.pentaho.kthin.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 6/5/14.
 */
public class DefaultEntryCategory implements EntryCategory {
    private String label;
    private List<Entry> entries;

    public DefaultEntryCategory(String label) {
        this.label = label;
        this.entries = new ArrayList<Entry>();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }
}

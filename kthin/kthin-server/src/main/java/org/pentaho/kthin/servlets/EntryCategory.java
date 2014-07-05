package org.pentaho.kthin.servlets;

import java.util.List;

/**
 * Created by bryan on 6/5/14.
 */
public interface EntryCategory {
    public String getLabel();
    public List<Entry> getEntries();
}

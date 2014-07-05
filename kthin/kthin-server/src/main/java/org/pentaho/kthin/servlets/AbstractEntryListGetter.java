package org.pentaho.kthin.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 6/5/14.
 */
public abstract class AbstractEntryListGetter implements EntryListGetter {
    private EntryCategory getCategory(String categoryLabel, List<EntryCategory> categoryList, Map<String, EntryCategory> categoryMap) {
        EntryCategory result = categoryMap.get(categoryLabel);
        if ( result == null ) {
            result = new DefaultEntryCategory(categoryLabel);
            categoryList.add(result);
            categoryMap.put(categoryLabel, result);
        }
        return result;
    }

    @Override
    public List<EntryCategory> getCategories() {
        List<EntryCategory> result = new ArrayList<EntryCategory>();
        Map<String, EntryCategory> categoryMap = new HashMap<String, EntryCategory>();
        for (String categoryLabel : getCategoryLabels()) {
            getCategory(categoryLabel, result, categoryMap);
        }
        return result;
    }

    protected abstract List<String> getCategoryLabels();
}

package org.pentaho.kettle.ftl;

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.ui.xul.XulEventSourceAdapter;

/**
 * Created by bryan on 2/8/14.
 */
public class FTLValueMetaEventSourceAdapter extends XulEventSourceAdapter {
    private final ValueMetaInterface valueMetaInterface;

    public FTLValueMetaEventSourceAdapter(ValueMetaInterface valueMetaInterface) {
        this.valueMetaInterface = valueMetaInterface;
    }

    public String getName() {
        return valueMetaInterface.getName();
    }

    public String getTypeDesc() {
        return valueMetaInterface.getTypeDesc();
    }
}

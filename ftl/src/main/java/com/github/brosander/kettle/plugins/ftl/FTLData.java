package com.github.brosander.kettle.plugins.ftl;

import freemarker.template.Template;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Created by bryan on 2/1/14.
 */
public class FTLData extends BaseStepData implements StepDataInterface {
    private RowMetaInterface outputRowMeta;
    private int fieldIndex;
    private Template template;

    public RowMetaInterface getOutputRowMeta() {
        return outputRowMeta;
    }

    public void setOutputRowMeta(RowMetaInterface outputRowMeta) {
        this.outputRowMeta = outputRowMeta;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}

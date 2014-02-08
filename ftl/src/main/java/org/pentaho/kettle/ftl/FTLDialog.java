package org.pentaho.kettle.ftl;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.ui.trans.step.BaseStepXulDialog;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 2/2/14.
 */
public class FTLDialog extends BaseStepXulDialog {
    private String tempStepName = null;
    private TransMeta transMeta;
    private List<FTLValueMetaEventSourceAdapter> valueMetaInterfaces;

    private String outputIDFieldName = null;
    private String templateString = "";

    public FTLDialog( Shell parent, Object in, TransMeta tr, String sname ) {
        super("org/pentaho/kettle/ftl/ftlDialog.xul", parent, (BaseStepMeta) in, tr, sname);
        loadMeta((FTLMeta) baseStepMeta);
        stepname = sname;
        tempStepName = sname;
        transMeta = tr;
        try {
            List<ValueMetaInterface> rawInterfaces = Collections.unmodifiableList(transMeta.getPrevStepFields(sname).getValueMetaList());
            valueMetaInterfaces = new ArrayList<FTLValueMetaEventSourceAdapter>(rawInterfaces.size());
            for (ValueMetaInterface vmi : rawInterfaces) {
                valueMetaInterfaces.add(new FTLValueMetaEventSourceAdapter(vmi));
            }
            bf.setBindingType(Binding.Type.ONE_WAY);
            bf.createBinding(this, "valueMetaInterfaces", "fieldsTable", "elements").fireSourceChanged();
            bf.setBindingType(Binding.Type.BI_DIRECTIONAL);
            bf.createBinding(this, "tempStepName", "step-name", "value").fireSourceChanged();
            bf.createBinding(this, "outputIDFieldName", "output-field", "value").fireSourceChanged();
            bf.createBinding(this, "templateString", "template-string", "value").fireSourceChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAccept() {
        if (Const.isEmpty(tempStepName)) {
            return;
        }
        stepname = tempStepName;
        saveMeta((FTLMeta)baseStepMeta);
        dispose();
    }

    @Override
    public void onCancel() {
        dispose();
    }

    @Override
    protected Class<?> getClassForMessages() {
        return FTL.class;
    }

    public String getTempStepName() {
        return tempStepName;
    }

    public void setTempStepName(String tempStepName) {
        this.tempStepName = tempStepName;
    }

    public List<FTLValueMetaEventSourceAdapter> getValueMetaInterfaces() {
        return valueMetaInterfaces;
    }

    public String getOutputIDFieldName() {
        return outputIDFieldName;
    }

    public void setOutputIDFieldName(String outputIDFieldName) {
        this.outputIDFieldName = outputIDFieldName;
    }

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    public void saveMeta(FTLMeta meta) {
        meta.setOutputIDFieldName(outputIDFieldName);
        meta.setTemplateString(templateString);
    }

    public void loadMeta(FTLMeta meta) {
        outputIDFieldName = meta.getOutputIDFieldName();
        templateString = meta.getTemplateString();
    }
}

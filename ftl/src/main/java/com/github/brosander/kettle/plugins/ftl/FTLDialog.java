package com.github.brosander.kettle.plugins.ftl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.ui.trans.step.BaseStepXulDialog;
import org.pentaho.ui.xul.binding.Binding;

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
    private String templateFile = "";
    private boolean useTemplateFile = false;
    private String templateFileEncoding = "";

    public FTLDialog( Shell parent, Object in, TransMeta tr, String sname ) {
        super("com/github/brosander/kettle/plugins/ftl/ftlDialog.xul", parent, (BaseStepMeta) in, tr, sname);
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
            bf.createBinding(this, FTLMeta.USE_TEMPLATE_FILE_FIELD, "template-string", "disabled").fireSourceChanged();
            bf.createBinding(this, FTLMeta.USE_TEMPLATE_FILE_FIELD, "template-file", "!disabled").fireSourceChanged();
            bf.createBinding(this, FTLMeta.USE_TEMPLATE_FILE_FIELD, "template-file-browse", "!disabled").fireSourceChanged();
            bf.createBinding(this, FTLMeta.USE_TEMPLATE_FILE_FIELD, "template-file-encoding", "!disabled").fireSourceChanged();
            bf.setBindingType(Binding.Type.BI_DIRECTIONAL);
            bf.createBinding(this, "tempStepName", "step-name", "value").fireSourceChanged();
            bf.createBinding(this, FTLMeta.OUTPUT_ID_FIELD_NAME_FIELD, "output-field", "value").fireSourceChanged();
            bf.createBinding(this, FTLMeta.TEMPLATE_STRING_FIELD, "template-string", "value").fireSourceChanged();
            bf.createBinding(this, FTLMeta.TEMPLATE_FILE_FIELD, "template-file", "value").fireSourceChanged();
            bf.createBinding(this, FTLMeta.USE_TEMPLATE_FILE_FIELD, "template-usefile-checkbox", "checked").fireSourceChanged();
            bf.createBinding(this, FTLMeta.TEMPLATE_FILE_ENCODING_FIELD, "template-file-encoding", "value").fireSourceChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAccept() {
        if (Const.isEmpty(tempStepName)) {
            return;
        }
        if (!stepname.equals(tempStepName)) {
            baseStepMeta.setChanged();
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

    public String getTemplateFile() {
        return templateFile;
    }

    public String getTemplateFileEncoding() {
        return templateFileEncoding;
    }

    public void setTemplateFileEncoding(String templateFileEncoding) {
        String oldVal = this.templateFileEncoding;
        this.templateFileEncoding = templateFileEncoding;
        firePropertyChange(FTLMeta.TEMPLATE_FILE_ENCODING_FIELD, oldVal, templateFileEncoding);
    }

    public void setTemplateFile(String templateFile) {
        String oldVal = this.templateFile;
        this.templateFile = templateFile;
        firePropertyChange(FTLMeta.TEMPLATE_FILE_FIELD, oldVal, templateFile);
    }

    public void selectTemplateFile() {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
        if (templateFile != null) {
            fileDialog.setFileName(transMeta.environmentSubstitute(templateFile));
        }
        if (fileDialog.open() != null) {
            setTemplateFile(fileDialog.getFilterPath() + System.getProperty("file.separator") + fileDialog.getFileName());
        }
    }

    public boolean isUseTemplateFile() {
        return useTemplateFile;
    }

    public void setUseTemplateFile(boolean useTemplateFile) {
        boolean oldVal = this.useTemplateFile;
        this.useTemplateFile = useTemplateFile;
        firePropertyChange(FTLMeta.USE_TEMPLATE_FILE_FIELD, oldVal, useTemplateFile);
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    public void saveMeta(FTLMeta meta) {
        meta.setOutputIDFieldName(outputIDFieldName);
        meta.setTemplateString(templateString);
        meta.setTemplateFile(templateFile);
        meta.setUseTemplateFile(useTemplateFile);
        meta.setTemplateFileEncoding(templateFileEncoding);
    }

    public void loadMeta(FTLMeta meta) {
        outputIDFieldName = meta.getOutputIDFieldName();
        templateString = meta.getTemplateString();
        templateFile = meta.getTemplateFile();
        useTemplateFile = meta.isUseTemplateFile();
        templateFileEncoding = meta.getTemplateFileEncoding();
    }
}

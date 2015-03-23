package com.github.brosander.kettle.plugins.ftl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 2/1/14.
 */
public class FTL extends BaseStep implements StepInterface {
    private FTLMeta meta;
    private FTLData data;
    private Object[] nextRow;
    private RowMetaInterface nextRowMeta;

    public FTL(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        meta = (FTLMeta) smi;
        data = (FTLData) sdi;

        final Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put(environmentSubstitute(meta.getFirstVariableName()), first);

        final Object[] thisRow;
        final RowMetaInterface thisRowMeta;
        if (first) {
            final Configuration config = new Configuration();
            final StringTemplateLoader templateLoader = new StringTemplateLoader();
            config.setTemplateLoader(templateLoader);
            if (meta.isUseTemplateFile()) {
                String template = KettleVFS.getTextFileContent(environmentSubstitute(meta.getTemplateFile()), environmentSubstitute(meta.getTemplateFileEncoding()));
                templateLoader.putTemplate("template", template);
            } else {
                templateLoader.putTemplate("template", meta.getTemplateString());
            }

            try {
                data.setTemplate(config.getTemplate("template"));
            } catch (IOException e) {
                throw new KettleException(e);
            }

            String outputField = environmentSubstitute(meta.getOutputIDFieldName());
            thisRow = getRow();
            thisRowMeta = getInputRowMeta();
            data.setOutputRowMeta(thisRowMeta.clone());
            meta.getFields(data.getOutputRowMeta(), getStepname(), null, null, this, repository, metaStore);
            data.setFieldIndex(data.getOutputRowMeta().indexOfValue(outputField));
            first = false;
        } else {
            thisRow = nextRow;
            thisRowMeta = nextRowMeta;
        }

        nextRow = getRow();
        nextRowMeta = getInputRowMeta();
        if (nextRow == null) {
            templateMap.put(environmentSubstitute(meta.getLastVariableName()), true);
        } else {
            templateMap.put(environmentSubstitute(meta.getLastVariableName()), false);
        }

        if (thisRow == null) {
            setOutputDone();
            return false;
        }

        for (String variable : listVariables()) {
            templateMap.put(variable, getVariable(variable));
        }

        for (int i = 0; i < thisRowMeta.size(); i++) {
            templateMap.put(thisRowMeta.getValueMeta(i).getName(), thisRow[i]);
        }

        Object[] outputRow = RowDataUtil.resizeArray(thisRow, data.getOutputRowMeta().size());
        StringWriter stringWriter = new StringWriter();
        try {
            data.getTemplate().process(templateMap, stringWriter);
        } catch (Exception e) {
            throw new KettleException(e);
        }
        outputRow[data.getFieldIndex()] = stringWriter.toString();
        putRow(data.getOutputRowMeta(), outputRow);

        return true;
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (FTLMeta) smi;
        data = (FTLData) sdi;
        if (!super.init(smi, sdi)) {
            return false;
        }
        return true;
    }
}

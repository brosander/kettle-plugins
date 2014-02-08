package org.pentaho.kettle.ftl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

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

    public FTL(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        meta = (FTLMeta) smi;
        data = (FTLData) sdi;
        Object[] inputRow = getRow();
        RowMetaInterface inputRowMeta = getInputRowMeta();

        if (inputRow == null) {
            setOutputDone();
            return false;
        }

        if (first) {
            final Configuration config = new Configuration();
            final StringTemplateLoader templateLoader = new StringTemplateLoader();
            config.setTemplateLoader(templateLoader);
            templateLoader.putTemplate("template", meta.getTemplateString());

            try {
                data.setTemplate(config.getTemplate("template"));
            } catch (IOException e) {
                throw new KettleException(e);
            }

            String outputField = environmentSubstitute(meta.getOutputIDFieldName());
            data.setOutputRowMeta(getInputRowMeta().clone());
            meta.getFields(data.getOutputRowMeta(), getStepname(), null, null, this, repository, metaStore);
            data.setFieldIndex(data.getOutputRowMeta().indexOfValue(outputField));
            first = false;
        }

        Map<String, Object> templateMap = new HashMap<String, Object>();
        for (int i = 0; i < inputRowMeta.size(); i++) {
            templateMap.put(inputRowMeta.getValueMeta(i).getName(), inputRow[i]);
        }

        Object[] outputRow = RowDataUtil.resizeArray(inputRow, data.getOutputRowMeta().size());
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

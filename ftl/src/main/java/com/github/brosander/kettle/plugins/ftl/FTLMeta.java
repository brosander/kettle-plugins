package com.github.brosander.kettle.plugins.ftl;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by bryan on 2/1/14.
 */

@Step(id = "FTL", image = "FTL.png", i18nPackageName = "org.brosander.kettle.plugins.ftl", name = "FTL.TransName", description = "FTL.TransDescription", categoryDescription = "FTL.CategoryDescription")
public class FTLMeta extends BaseStepMeta implements StepMetaInterface {
    private static final Class<?> PKG = FTLMeta.class;

    public static final String OUTPUT_ID_FIELD_NAME_FIELD = "outputIDFieldName";
    public static final String TEMPLATE_STRING_FIELD = "templateString";
    public static final String TEMPLATE_FILE_FIELD = "templateFile";
    public static final String USE_TEMPLATE_FILE_FIELD = "useTemplateFile";
    public static final String TEMPLATE_FILE_ENCODING_FIELD = "templateFileEncoding";

    private String outputIDFieldName = "ftl.output";
    private String templateString = "";
    private String templateFile = "";
    private boolean useTemplateFile = false;
    private String templateFileEncoding = "UTF-8";

    public String getOutputIDFieldName() {
        return outputIDFieldName;
    }

    public void setOutputIDFieldName(String outputIDFieldName) {
        if (this.outputIDFieldName != null &&
            !this.outputIDFieldName.equals(outputIDFieldName)) {
            setChanged();
        }
        this.outputIDFieldName = outputIDFieldName;
    }

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString){
        if (this.templateString != null &&
            !this.templateString.equals(templateString)) {
            setChanged();
        }
        this.templateString = templateString;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        if ( this.templateFile != null && !this.templateFile.equals(templateFile)) {
            setChanged();
        }
        this.templateFile = templateFile;
    }

    public boolean isUseTemplateFile() {
        return useTemplateFile;
    }

    public String getTemplateFileEncoding() {
        return templateFileEncoding;
    }

    public void setTemplateFileEncoding(String templateFileEncoding) {
        if (this.templateFileEncoding != null &&
            !this.templateFileEncoding.equals(templateFileEncoding)) {
            setChanged();
        }
        this.templateFileEncoding = templateFileEncoding;
    }

    public void setUseTemplateFile(boolean useTemplateFile) {
        if (this.useTemplateFile != useTemplateFile) {
            setChanged();
        }
        this.useTemplateFile = useTemplateFile;
    }

    @Override
    public void setDefault() {
        outputIDFieldName = "ftl.output";
        templateString = "";
        templateFile = "";
        useTemplateFile = false;
        templateFileEncoding = "UTF-8";
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int i, TransMeta transMeta, Trans trans) {
        return new FTL(stepMeta, stepDataInterface, i, transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData() {
        return new FTLData();
    }

    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
        String outputField = space.environmentSubstitute(getOutputIDFieldName());
        ValueMetaInterface outputInterface = null;
        try {
            outputInterface = ValueMetaFactory.createValueMeta(outputField, ValueMetaInterface.TYPE_STRING);
        } catch (KettlePluginException e) {
            throw new KettleStepException(e);
        }
        boolean fieldFound = false;
        for (int i = 0; i < inputRowMeta.size(); i++) {
            ValueMetaInterface valueMetaInterface = inputRowMeta.getValueMeta(i);
            String interfaceName = valueMetaInterface.getName();
            if (outputField.equals(interfaceName)) {
                fieldFound = true;
                inputRowMeta.setValueMeta(i, outputInterface);
                break;
            }
        }
        if (!fieldFound) {
            inputRowMeta.addValueMeta(outputInterface);
        }
    }

    @Override
    public String getXML() throws KettleException {
        StringBuilder retval = new StringBuilder();
        retval.append("    ").append(XMLHandler.addTagValue(OUTPUT_ID_FIELD_NAME_FIELD, outputIDFieldName));
        retval.append("    ").append(XMLHandler.addTagValue(TEMPLATE_STRING_FIELD, templateString));
        retval.append("    ").append(XMLHandler.addTagValue(TEMPLATE_FILE_FIELD, templateFile));
        retval.append("    ").append(XMLHandler.addTagValue(USE_TEMPLATE_FILE_FIELD, useTemplateFile));
        retval.append("    ").append(XMLHandler.addTagValue(TEMPLATE_FILE_ENCODING_FIELD, templateFileEncoding));
        return retval.toString();
    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
        outputIDFieldName = XMLHandler.getTagValue( stepnode, OUTPUT_ID_FIELD_NAME_FIELD);
        templateString = XMLHandler.getTagValue( stepnode, TEMPLATE_STRING_FIELD);
        templateFile = XMLHandler.getTagValue( stepnode, TEMPLATE_FILE_FIELD);
        useTemplateFile = "Y".equalsIgnoreCase(XMLHandler.getTagValue( stepnode, USE_TEMPLATE_FILE_FIELD));
        templateFileEncoding = XMLHandler.getTagValue( stepnode, TEMPLATE_FILE_ENCODING_FIELD);
    }

    @Override
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        rep.saveStepAttribute( id_transformation, id_step, OUTPUT_ID_FIELD_NAME_FIELD, outputIDFieldName);
        rep.saveStepAttribute( id_transformation, id_step, TEMPLATE_STRING_FIELD, templateString);
        rep.saveStepAttribute( id_transformation, id_step, TEMPLATE_FILE_FIELD, templateFile);
        rep.saveStepAttribute( id_transformation, id_step, USE_TEMPLATE_FILE_FIELD, useTemplateFile);
        rep.saveStepAttribute( id_transformation, id_step, TEMPLATE_FILE_ENCODING_FIELD, templateFileEncoding);
    }

    @Override
    public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
        outputIDFieldName = rep.getStepAttributeString( id_step, OUTPUT_ID_FIELD_NAME_FIELD);
        templateString = rep.getStepAttributeString( id_step, TEMPLATE_STRING_FIELD);
        templateFile = rep.getStepAttributeString( id_step, TEMPLATE_FILE_FIELD);
        useTemplateFile = rep.getStepAttributeBoolean( id_step, USE_TEMPLATE_FILE_FIELD);
        templateFileEncoding = rep.getStepAttributeString( id_step, TEMPLATE_FILE_ENCODING_FIELD);
    }
}

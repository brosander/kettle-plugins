package org.pentaho.kettle.ftl;

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

@Step(id = "FTL", image = "FTL.png", i18nPackageName = "org.pentaho.kettle.ftl", name = "FTL.TransName", description = "FTL.TransDescription", categoryDescription = "FTL.CategoryDescription")
public class FTLMeta extends BaseStepMeta implements StepMetaInterface {
    private static final Class<?> PKG = FTLMeta.class;

    private String outputIDFieldName = null;
    private String templateString = "";

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

    @Override
    public void setDefault() {
        outputIDFieldName = "ftl.output";
        templateString = "";
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
        retval.append("    ").append(XMLHandler.addTagValue("outputIDFieldName", outputIDFieldName));
        retval.append("    ").append(XMLHandler.addTagValue("templateString", templateString));
        return retval.toString();
    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
        outputIDFieldName = XMLHandler.getTagValue( stepnode, "outputIDFieldName" );
        templateString = XMLHandler.getTagValue( stepnode, "templateString" );
    }

    @Override
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        rep.saveStepAttribute( id_transformation, id_step, "outputIDFieldName", outputIDFieldName);
        rep.saveStepAttribute( id_transformation, id_step, "templateString", templateString);
    }

    @Override
    public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
        outputIDFieldName = rep.getStepAttributeString( id_step, "outputIDFieldName");
        templateString = rep.getStepAttributeString( id_step, "templateString");
    }
}

package org.pentaho.kthin.servlets;

import org.mortbay.util.ajax.JSON;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.www.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 6/1/14.
 */
@org.pentaho.di.core.annotations.CarteServlet(id = "org.pentaho.kthin.servlets.KThinJobEntryListServlet", name = "org.pentaho.kthin.servlets.KThinJobEntryListServlet", description = "Servlet to generate job entry list json", i18nPackageName = "org.pentaho.kthin.servlets")
public class KThinJobEntryListServlet extends HttpServlet implements CartePluginInterface {
    public static final String  CONTEXT_PATH    = "/kettle/kthin/jobEntryList";
    private boolean jettyMode = false;

    @Override
    public void setup(TransformationMap transformationMap, JobMap jobMap, SocketRepository socketRepository, List<SlaveServerDetection> detections) {

    }

    @Override
    public String getContextPath() {
        return CONTEXT_PATH;
    }

    @Override
    public void setJettyMode(boolean jettyMode) {
        this.jettyMode = jettyMode;
    }

    @Override
    public boolean isJettyMode() {
        return jettyMode;
    }

    @Override
    public String getService() {
        return CONTEXT_PATH + "(Get job entry list)";
    }

    private Map<String, Object> getCategoryMap(String category, List<Map<String, Object>> categoryList, Map<String, Map<String, Object>> categoryMap) {
        Map<String, Object> result = categoryMap.get(category);
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put("category", category);
            result.put("jobEntries", new ArrayList<Map<String, String>>());
            categoryList.add(result);
            categoryMap.put(category, result);
        }
        return result;
    }

    private void addEntry(List<Map<String, Object>> categoryList, Map<String, Map<String, Object>> categoryMap, String category, String name, String label) {
        Map<String, String> entry = new HashMap<String, String>();
        entry.put("name", name);
        entry.put("label", label);
        entry.put("image", KThinJobEntryImageServlet.CONTEXT_PATH.substring(1) + "/?name=" + name);
        ((List<Map<String, String>>)getCategoryMap(category, categoryList, categoryMap).get("jobEntries")).add(entry);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        final PluginRegistry registry = PluginRegistry.getInstance();
        final List<PluginInterface> basesteps = registry.getPlugins(JobEntryPluginType.class);
        final List<String> basecat = registry.getCategories(JobEntryPluginType.class);

        List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
        Map<String, Map<String, Object>> categoryMap = new HashMap<String, Map<String, Object>>();
        for (String category : basecat) {
            getCategoryMap(category, categoryList, categoryMap);
        }
        addEntry(categoryList, categoryMap, "General", JobMeta.STRING_SPECIAL_START, "Start");
        addEntry(categoryList, categoryMap, "General", JobMeta.STRING_SPECIAL_DUMMY, "Dummy");
        for (PluginInterface pluginInterface : basesteps) {
            // Because these are called out differently in kettle
            if ("SPECIAL".equals(pluginInterface.getIds()[0])) {
                continue;
            }
            addEntry(categoryList, categoryMap, pluginInterface.getCategory(), pluginInterface.getIds()[0], pluginInterface.getName()) ;
        }

        resp.getWriter().print(JSON.toString(categoryList));
        resp.getWriter().flush();
    }
}

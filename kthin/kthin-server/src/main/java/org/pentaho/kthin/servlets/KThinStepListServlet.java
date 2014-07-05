package org.pentaho.kthin.servlets;

import org.mortbay.util.ajax.JSON;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.www.CartePluginInterface;
import org.pentaho.di.www.JobMap;
import org.pentaho.di.www.SlaveServerDetection;
import org.pentaho.di.www.SocketRepository;
import org.pentaho.di.www.TransformationMap;

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
@org.pentaho.di.core.annotations.CarteServlet(id = "org.pentaho.kthin.servlets.KThinStepListServlet", name = "org.pentaho.kthin.servlets.KThinStepListServlet", description = "Servlet to generate step list json", i18nPackageName = "org.pentaho.kthin.servlets")
public class KThinStepListServlet extends HttpServlet implements CartePluginInterface {
    public static final String  CONTEXT_PATH    = "/kettle/kthin/entryList";
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
        return CONTEXT_PATH + "(Get step list)";
    }

    private Map<String, Object> getCategoryMap(String category, List<Map<String, Object>> categoryList, Map<String, Map<String, Object>> categoryMap) {
        Map<String, Object> result = categoryMap.get(category);
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put("category", category);
            result.put("steps", new ArrayList<Map<String, String>>());
            categoryList.add(result);
            categoryMap.put(category, result);
        }
        return result;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        final String entryType = req.getParameter("entryType");
        final PluginRegistry registry = PluginRegistry.getInstance();
        final List<PluginInterface> basesteps = registry.getPlugins(StepPluginType.class);
        final List<String> basecat = registry.getCategories(StepPluginType.class);

        List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
        Map<String, Map<String, Object>> categoryMap = new HashMap<String, Map<String, Object>>();
        for (String category : basecat) {
            getCategoryMap(category, categoryList, categoryMap);
        }
        for (PluginInterface pluginInterface : basesteps) {
            Map<String, Object> thisCategory = getCategoryMap(pluginInterface.getCategory(), categoryList, categoryMap);
            List<Map<String, String>> stepList = (List<Map<String, String>>) thisCategory.get("steps");
            Map<String, String> step = new HashMap<String, String>();
            step.put("name", pluginInterface.getIds()[0]);
            step.put("label", pluginInterface.getName());
            step.put("image", KThinStepImageServlet.CONTEXT_PATH.substring(1) + "/?name=" + pluginInterface.getIds()[0]);
            stepList.add(step);
        }
        resp.getWriter().print(JSON.toString(categoryList));
        resp.getWriter().flush();
    }
}

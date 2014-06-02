package org.pentaho.kthin.servlets;

import org.apache.commons.io.IOUtils;
import org.mortbay.util.ajax.JSON;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.www.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 6/1/14.
 */
@org.pentaho.di.core.annotations.CarteServlet(id = "org.pentaho.kthin.servlets.KThinStepImageServlet", name = "org.pentaho.kthin.servlets.KThinStepImageServlet", description = "Servlet to serve step images", i18nPackageName = "org.pentaho.kthin.servlets")
public class KThinStepImageServlet extends HttpServlet implements CartePluginInterface {
    public static final String  CONTEXT_PATH    = "/kettle/kthin/stepImage";
    private boolean jettyMode = false;
    private final Map<String, PluginInterface> images = new HashMap<String, PluginInterface>();
    private final PluginRegistry registry = PluginRegistry.getInstance();

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

    private InputStream getImage(PluginInterface pluginInterface) throws KettlePluginException {
        ClassLoader classLoader = registry.getClassLoader(pluginInterface);
        InputStream result = classLoader.getResourceAsStream(pluginInterface.getImageFile());
        if (result == null) {
            result = classLoader.getResourceAsStream("/" + pluginInterface.getImageFile());
        }
        if (result == null) {
            try {
                result = KettleVFS.getInputStream(pluginInterface.getImageFile());
            } catch(Exception e) {
                //ignore
            }
        }
        return result;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("image/png");
        String id = req.getParameter("name");
        PluginInterface pluginInterface = images.get(id);
        if (pluginInterface == null) {
            for (PluginInterface pluginInterface1 : registry.getPlugins(StepPluginType.class)) {
                if (pluginInterface1.getIds()[0].equalsIgnoreCase(id)) {
                    pluginInterface = pluginInterface1;
                }
            }
        }
        OutputStream outputStream = resp.getOutputStream();
        try {
            IOUtils.copy(getImage(pluginInterface), outputStream);
        } catch (KettlePluginException e) {
            throw new ServletException(e);
        }
        outputStream.flush();
    }
}

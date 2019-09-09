package ru.khayz.server.servlets;

import ru.khayz.server.rg.ResponseGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletUtils {
    private ServletUtils() {}

    public static void createErrorMessage(HttpServletResponse resp, String templateName, String message) throws IOException {
        Map<String, Object> varsMap = new HashMap<>();
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        varsMap.put("message", message);
        resp.getWriter().write(ResponseGenerator.fillResponseTemplate(templateName, varsMap));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    public static void createResponse(HttpServletResponse resp, String templateName, Map<String, Object> varsMap) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(ResponseGenerator.fillResponseTemplate(templateName, varsMap));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    public static void createInternalErrorMessage(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

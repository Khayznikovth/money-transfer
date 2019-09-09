package ru.khayz.server.rg;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class ResponseGenerator {
    private static final String HTML_DIR = "src\\main\\resources\\templates";

    private static final Configuration CONFIGURATION = new Configuration();

    public static String fillResponseTemplate(String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try {
            Template template = CONFIGURATION.getTemplate(HTML_DIR + File.separator + filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }

    private ResponseGenerator() {
    }
}
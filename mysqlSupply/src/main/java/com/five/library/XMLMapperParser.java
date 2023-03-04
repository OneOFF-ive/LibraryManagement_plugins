package com.five.library;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XMLMapperParser {

    private class ParaInfo {
        public String paraType;
        public String sql;

        public ParaInfo(String paraType, String sql) {
            this.paraType = paraType;
            this.sql = sql;
        }
    }

    private Map<String, ParaInfo> sqlMap;
    private Document document;

    public Map<String, ParaInfo> getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(Map<String, ParaInfo> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    XMLMapperParser(String xmlPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlPath)) {
            document = builder.parse(inputStream);
            sqlMap = new HashMap<>();
        }
    }

    void paresXml() {
        NodeList nodeList = document.getElementsByTagName("mapper");
        for(int i = 0; i < nodeList.getLength(); i++){
            Node mapperNode = nodeList.item(i);
            String namespace = mapperNode.getAttributes().getNamedItem("namespace").getTextContent();
            NodeList childNodes = mapperNode.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    String id = childNode.getAttributes().getNamedItem("id").getTextContent();

                    var tmp = childNode.getAttributes().getNamedItem("parameterType");

                    String paraType = (tmp != null) ? tmp.getTextContent() : null;
                    String statement = childNode.getTextContent().trim();
                    sqlMap.put(namespace + "." + id, new ParaInfo(paraType, statement));
                }
            }
        }
    }

    String getSql(String id, Object parameter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ParaInfo paraInfo = sqlMap.get(id);
        if (paraInfo == null) return null;

        String clzName = paraInfo.paraType;
        String sql = paraInfo.sql;
        if (clzName == null) return sql;

        Class<?> clz = null;
        try {
            clz = Class.forName(clzName);
            for (var field: clz.getDeclaredFields()) {
                String fieldName = field.getName();
                String placeholder = "#{" + fieldName + "}";
                Object value = clz.getMethod("get" + capitalize(fieldName)).invoke(parameter);
                if (value == null) {
                    sql = sql.replace(placeholder, "NULL");
                } else if (value instanceof String) {
                    sql = sql.replace(placeholder, "'" + value + "'");
                } else {
                    sql = sql.replace(placeholder, value.toString());
                }
            }
        } catch (ClassNotFoundException e) {
            if (Objects.equals(clzName, "String")) {
                sql = sql.replaceAll("#\\{.*?}", "'" + parameter + "'");
            }
            else {
                sql = sql.replaceAll("#\\{.*?}", parameter.toString());
            }
        }

        return sql;
    }

    public static String capitalize(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

}

package com.five.library.sql;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XMLMapperParser {

    public static class TagInfo {
        public String paraType;
        public String sql;
        public String resType;

        public TagInfo(String sql, String paraType, String resType) {
            this.paraType = paraType;
            this.sql = sql;
            this.resType = resType;
        }
    }

    private Map<String, TagInfo> id2TagInfo;
    private Document document;

    public Map<String, TagInfo> getId2TagInfo() {
        return id2TagInfo;
    }

    public void setId2TagInfo(Map<String, TagInfo> id2TagInfo) {
        this.id2TagInfo = id2TagInfo;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public XMLMapperParser(String xmlPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlPath)) {
            document = builder.parse(inputStream);
            id2TagInfo = new HashMap<>();
        }
    }

    public void paresXml() {
        NodeList nodeList = document.getElementsByTagName("mapper");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node mapperNode = nodeList.item(i);
            String namespace = mapperNode.getAttributes().getNamedItem("namespace").getTextContent();
            NodeList childNodes = mapperNode.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    String id = childNode.getAttributes().getNamedItem("id").getTextContent();

                    var tmp = childNode.getAttributes().getNamedItem("parameterType");
                    String paraType = (tmp != null) ? tmp.getTextContent() : null;

                    tmp = childNode.getAttributes().getNamedItem("resultType");
                    String resType = (tmp != null) ? tmp.getTextContent() : null;

                    String statement = childNode.getTextContent().trim();
                    id2TagInfo.put(namespace + "." + id, new TagInfo(statement, paraType, resType));
                }
            }
        }
    }

}

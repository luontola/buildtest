// Copyright © 2011-2013 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.luontola.buildtest;

import org.intellij.lang.annotations.Language;
import org.w3c.dom.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

public class XmlUtils {

    public static Document parseXml(File file) throws Exception {
        return newDocumentBuilder().parse(file);
    }

    public static Document parseXml(String xml) throws Exception {
        return newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);
        return domFactory.newDocumentBuilder();
    }

    public static String xpath(@Language("XPath") String expression, Node node) throws XPathExpressionException {
        return (String) xpath(expression, node, XPathConstants.STRING);
    }

    public static Object xpath(@Language("XPath") String expression, Node item, QName returnType) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return xpath.evaluate(expression, item, returnType);
    }
}

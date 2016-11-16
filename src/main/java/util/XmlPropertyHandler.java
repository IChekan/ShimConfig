package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
public class XmlPropertyHandler {

    public String readXmlPropertyValue (String pathToFile, String property) {
        // Read *-site.xml file and return property value, return null if property was not found

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(pathToFile);

            NodeList list = doc.getElementsByTagName("property");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if(property.equals(node.getChildNodes().item(1).getFirstChild().getNodeValue())) {
                    return node.getChildNodes().item(3).getFirstChild().getNodeValue();
                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }

        return null;
    }

    public Node createProperty(Document doc, String name, String value) {
        Element property = doc.createElement("property");
        property.appendChild(createPropertyElements(doc, property, "name", name));
        property.appendChild(createPropertyElements(doc, property, "value", value));
        return property;
    }

    // utility method to create text node
    private Node createPropertyElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

}

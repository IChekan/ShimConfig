package modifiers;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import util.XmlPropertyHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created by Ihar_Chekan on 10/13/2016.
 */
public class AddCrossPlatform {

    public void addCrossPlatform(String pathToMapredSiteXML) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(pathToMapredSiteXML);

            Node configuration = doc.getElementsByTagName("configuration").item(0);
            XmlPropertyHandler xmlPropertyHandler = new XmlPropertyHandler();
            configuration.appendChild(xmlPropertyHandler.createProperty(doc, "mapreduce.app-submission.cross-platform", "true"));

            // write the content into xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathToMapredSiteXML));
            transformer.transform(source, result);

            System.out.println("cross-platform added");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
    }

}

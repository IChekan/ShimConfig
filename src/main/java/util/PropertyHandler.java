package util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.*;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
public class PropertyHandler {

    public String getPropertyFromFile (String pathToFile, String property) {
        // Read property file and return property value, return null if property was not found
        PropertiesConfiguration prop = new PropertiesConfiguration();
        InputStream input = null;

        try {
            input = new FileInputStream(pathToFile);

            prop.load(input);
            return prop.getProperty( property ).toString();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ConfigurationException ce) {
            ce.printStackTrace();
        }

        return null;
    }


    public void setProperty( String file, String property, String value ) {
        PropertiesConfiguration prop = new PropertiesConfiguration( );
        InputStream input = null;
        OutputStream output = null;

        try {
            input = new FileInputStream( file );

            prop.load( input );
            prop.setProperty(property,value);

            output = new FileOutputStream( file );
            prop.save(output, null);

            System.out.println("property \"" + property + "\" is set to value \"" + value + "\" in file \"" + file + "\"" );

        } catch ( IOException ex ) {
            ex.printStackTrace( );
        } catch ( ConfigurationException ce ) {
            ce.printStackTrace();
        } finally {
            if ( input != null ) {
                try {
                    input.close( );
                } catch ( IOException e ) {
                    e.printStackTrace( );
                }
            }
            if ( output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
public class PropertyHandler {

    final static Logger logger = Logger.getLogger(PropertyHandler.class);

    public static String getPropertyFromFile( String pathToFile, String property ) {
        // Read property file and return property value, return null if property was not found
        PropertiesConfiguration prop = new PropertiesConfiguration();
        InputStream input = null;

        try {
            input = new FileInputStream( pathToFile );

            prop.load( input );
            Object resProperty = prop.getProperty( property );
            if ( resProperty == null ) {
                return null;
            }
            return resProperty.toString();

        } catch ( IOException ex ) {
            logger.error("IOException: " + ex);
            //ex.printStackTrace();
        } catch ( ConfigurationException ce ) {
            logger.error("ConfigurationException: " + ce);
            //ce.printStackTrace();
        }

        return null;
    }


    public static void setProperty( String file, String property, String value ) {
        PropertiesConfiguration prop = new PropertiesConfiguration( );
        InputStream input = null;
        OutputStream output = null;

        try {
//            System.out.println( "property \"" + property + "\" is trying to set to value \"" + value + "\" in file \"" + file + "\"" );
            input = new FileInputStream( file );

            prop.load( input );
            prop.setProperty( property, value );

            output = new FileOutputStream( file );
            prop.save( output, null );

            logger.info( "property \"" + property + "\" is set to value \"" + value + "\" in file \"" + file + "\"" );

        } catch ( IOException ex ) {
            logger.error("IOException: " + ex);
            //ex.printStackTrace( );
        } catch ( ConfigurationException ce ) {
            logger.error("ConfigurationException: " + ce);
            //ce.printStackTrace();
        } finally {
            if ( input != null ) {
                try {
                    input.close( );
                } catch ( IOException e ) {
                    logger.error("IOException: " + e);
                    //e.printStackTrace( );
                }
            }
            if ( output != null ) {
                try {
                    output.close();
                } catch ( IOException e ) {
                    logger.error("IOException: " + e);
                    //e.printStackTrace();
                }
            }
        }
    }
}

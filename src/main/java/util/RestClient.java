package util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ihar_Chekan on 12/22/2016.
 */
public class RestClient {

    public enum HttpMethod {
        HTTP_METHOD_GET,
        HTTP_METHOD_POST,
        HTTP_METHOD_PUT,
        HTTP_METHOD_DELETE,
        HTTP_METHOD_HEAD,
        HTTP_METHOD_OPTIONS
    }

    public enum AuthMethod {
        BASIC
    }

    public static byte[] callRest( String requestUrl, HttpMethod httpMethod, AuthMethod authMethod,
                                   @Nullable String restUser, @Nullable String restPassword,
                                   @Nullable MediaType mediaType, @Nullable HashMap<String, String> header,
                                   @Nullable String bodyValue ) {

        // create an instance of the com.sun.jersey.api.client.Client class
        Client client = ApacheHttpClient.create();
        switch ( authMethod ) {
            case BASIC:
                HTTPBasicAuthFilter basicAuthFilter = new HTTPBasicAuthFilter( restUser, restPassword );
                client.addFilter( basicAuthFilter );
                break;
        }
        // create a WebResource object, which encapsulates a web resource for the client
        WebResource webResource = client.resource( requestUrl );
        WebResource.Builder builder = webResource.getRequestBuilder();

        // Add headers
        if ( header != null ) {
            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                builder = builder.header( entry.getKey(), entry.getValue() );
            }
        }

        byte[] response = null;
        try {
            switch ( httpMethod ) {
                case HTTP_METHOD_GET:
                    try {
                        response = IOUtils
                                .toByteArray( builder.get( ClientResponse.class ).getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
                case HTTP_METHOD_POST:
                    try {
                        response = IOUtils
                                .toByteArray( builder.type( mediaType ).post( ClientResponse.class, bodyValue ).getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
                case HTTP_METHOD_PUT:
                    try {
                        response = IOUtils
                                .toByteArray( builder.type( mediaType ).put( ClientResponse.class, bodyValue ).getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
                case HTTP_METHOD_DELETE:
                    try {
                        response = IOUtils
                                .toByteArray( builder.type( mediaType ).delete( ClientResponse.class, bodyValue ).getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
                case HTTP_METHOD_HEAD:
                    try {
                        response = IOUtils
                                .toByteArray( builder.head().getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
                case HTTP_METHOD_OPTIONS:
                    try {
                        response = IOUtils
                                .toByteArray( builder.options( ClientResponse.class ).getEntityInputStream() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch ( UniformInterfaceException u ) {
            System.out.println( u.getResponse().getStatus() );
        }
        return response;
    }

}

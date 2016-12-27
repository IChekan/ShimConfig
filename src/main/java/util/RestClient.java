package util;

import com.sun.istack.internal.Nullable;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;

import javax.ws.rs.core.MediaType;
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

    public static String callRest (String requestUrl, HttpMethod httpMethod, AuthMethod authMethod,
                                   @Nullable MediaType mediaType, @Nullable HashMap<String, String> header,
                                   @Nullable String bodyValue ) {

        // create an instance of the com.sun.jersey.api.client.Client class
        Client client = ApacheHttpClient.create();
        switch ( authMethod ) {
            case BASIC:
            HTTPBasicAuthFilter basicAuthFilter = new HTTPBasicAuthFilter(ShimValues.getRestUser(), ShimValues.getRestPassword());
            client.addFilter(basicAuthFilter);
            break;
        }
        // create a WebResource object, which encapsulates a web resource for the client
        WebResource webResource = client.resource( requestUrl );
        WebResource.Builder builder = webResource.getRequestBuilder();

        // Add headers
        if ( header != null ) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }

        ClientResponse response = null;
        try {
            switch ( httpMethod ) {
                case HTTP_METHOD_GET:
                    response = builder.get(ClientResponse.class);
                    break;
                case HTTP_METHOD_POST:
                    response = builder.type( mediaType ).post( ClientResponse.class, bodyValue );
                    break;
                case HTTP_METHOD_PUT:
                    response = builder.type( mediaType ).put( ClientResponse.class, bodyValue );
                    break;
                case HTTP_METHOD_DELETE:
                    response = builder.type( mediaType ).delete( ClientResponse.class, bodyValue );
                    break;
                case HTTP_METHOD_HEAD:
                    response = builder.head();
                    break;
                case HTTP_METHOD_OPTIONS:
                    response = builder.options( ClientResponse.class );
                    break;
            }
        } catch ( UniformInterfaceException u ) {
            response = u.getResponse();
        }
        return response.getEntity( String.class );
    }

}

package util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.KeyStore;
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


    public static byte[] callRest( String requestUrl, HttpMethod httpMethod,
                                   String restUser, String restPassword,
                                   @Nullable String contentType, @Nullable HashMap<String, String> header,
                                   @Nullable String bodyValue ) {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( restUser, restPassword );
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        byte[] response = null;
        HttpResponse httpResponse;
        HttpClient client;
        try {
            HttpClientBuilder cb = HttpClientBuilder.create();
            SSLContextBuilder sslcb = new SSLContextBuilder();
            sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()),
                    new TrustSelfSignedStrategy());
            cb.setSSLContext(sslcb.build());
            client = cb.setDefaultCredentialsProvider(credentialsProvider).build();

            switch ( httpMethod ) {
                case HTTP_METHOD_GET:
                    try {
                        RequestBuilder rb = RequestBuilder.get().setUri( requestUrl );
                        // Add headers
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
                case HTTP_METHOD_POST:
                    try {
                        RequestBuilder rb = RequestBuilder.post().setUri( requestUrl );
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (bodyValue != null && contentType != null){
                            StringEntity stringEntity = new StringEntity(bodyValue);
                            stringEntity.setContentType(contentType);
                        rb.setEntity( stringEntity );
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
                case HTTP_METHOD_PUT:
                    try {
                        RequestBuilder rb = RequestBuilder.put().setUri( requestUrl );
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (bodyValue != null && contentType != null){
                            StringEntity stringEntity = new StringEntity(bodyValue);
                            stringEntity.setContentType(contentType);
                            rb.setEntity( stringEntity );
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
                case HTTP_METHOD_DELETE:
                    try {
                        RequestBuilder rb = RequestBuilder.delete().setUri( requestUrl );
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (bodyValue != null && contentType != null){
                            StringEntity stringEntity = new StringEntity(bodyValue);
                            stringEntity.setContentType(contentType);
                            rb.setEntity( stringEntity );
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
                case HTTP_METHOD_HEAD:
                    try {
                        RequestBuilder rb = RequestBuilder.head().setUri( requestUrl );
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (bodyValue != null && contentType != null){
                            StringEntity stringEntity = new StringEntity(bodyValue);
                            stringEntity.setContentType(contentType);
                            rb.setEntity( stringEntity );
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
                case HTTP_METHOD_OPTIONS:
                    try {
                        RequestBuilder rb = RequestBuilder.options().setUri( requestUrl );
                        if ( header != null ) {
                            for ( Map.Entry<String, String> entry : header.entrySet() ) {
                                rb.setHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (bodyValue != null && contentType != null){
                            StringEntity stringEntity = new StringEntity(bodyValue);
                            stringEntity.setContentType(contentType);
                            rb.setEntity( stringEntity );
                        }
                        HttpUriRequest request = rb.build();
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        System.out.println( "IOException in RestClient: " + e );
                    }
                    break;
            }
        } catch ( Exception e ) {
            System.out.println( "Exception in RestClient: " + e );
        }
        return response;
    }

}

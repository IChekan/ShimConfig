package util;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ihar_Chekan on 12/22/2016.
 */
public class RestClient {

    final static Logger logger = Logger.getLogger(RestClient.class);

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

        byte[] response = null;
        CloseableHttpResponse httpResponse;
        //HttpClient client;
        try {
            HttpClientBuilder builder = HttpClientBuilder.create();

            SSLContextBuilder sslcb = new SSLContextBuilder();
            sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()),
                    new TrustSelfSignedStrategy());
            builder.setSSLContext(sslcb.build());

//            Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
//                    register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
//             builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( restUser, restPassword );
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            credentialsProvider.setCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM, AuthPolicy.SPNEGO), new Credentials() {
                @Override
                public Principal getUserPrincipal() {
                    return null;
                }
                @Override
                public String getPassword() {
                    return null;
                }
            });
            builder.setDefaultCredentialsProvider(credentialsProvider);
            CloseableHttpClient client = builder.build();

            ArrayList<String> authPrefs = new ArrayList<String>();
            authPrefs.add(AuthSchemes.BASIC);
            authPrefs.add(AuthSchemes.SPNEGO);

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
                        RequestConfig config = RequestConfig.custom()
                                .setTargetPreferredAuthSchemes(authPrefs).build();
                        rb.setConfig(config);
                        HttpUriRequest request = rb.build();
                        // TODO: Remove the next "if" as soon as hdp25 cluster would use "normal" handshake model
                        if (ShimValues.getHadoopVendor().equalsIgnoreCase("hdp")) {
                            request.setHeader((new BasicScheme().authenticate(credentials, request, null)));
                        }
                        httpResponse = client.execute( request );
                        response = IOUtils.toByteArray (httpResponse.getEntity().getContent());
                    } catch ( IOException e ) {
                        logger.error( "IOException in RestClient: " + e );
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
                        logger.error( "IOException in RestClient: " + e );
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
                        logger.error( "IOException in RestClient: " + e );
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
                        logger.error( "IOException in RestClient: " + e );
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
                        logger.error( "IOException in RestClient: " + e );
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
                        logger.error( "IOException in RestClient: " + e );
                    }
                    break;
            }
        } catch ( Exception e ) {
            logger.error( "Exception in RestClient: " + e );
        }
        return response;
    }

}

package apiserver;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mnimer on 5/12/15.
 */
@Configuration
public class HttpConfiguration
{
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000 * 5);

    @Autowired
    //private ObjectMapper objectMapper;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());

        /**
         List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();

         for (HttpMessageConverter<?> converter : converters) {
         if (converter instanceof MappingJackson2HttpMessageConverter) {
         MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
         jsonConverter.setObjectMapper(objectMapper);
         }
         }
         **/

        return restTemplate;
    }


    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }


    @Bean
    public HttpClient httpClient() {

        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        HttpClient defaultHttpClient = new DefaultHttpClient(connectionManager);

        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_TOTAL_CONNECTIONS);
        defaultHttpClient.getParams().setIntParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT,
                DEFAULT_READ_TIMEOUT_MILLISECONDS);
        return defaultHttpClient;
    }

}

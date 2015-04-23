package apiserver.grid;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.client.marshaller.optimized.GridClientOptimizedMarshaller;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.sharedfs.TcpDiscoverySharedFsIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 6/11/14.
 */
@Configuration
public class GridManager implements Serializable
{
    @Value("${tmpPath}")
    private String tmpPath;
    private static Ignite grid = null;

    @Bean
    public Ignite grid(){
        if( grid == null ) {
            grid = Ignition.start(getGridConfiguration());
        }
        return grid;
    }



    private IgniteConfiguration getGridConfiguration() {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "ApiAppEngine");
        userAttr.put("ROLE", "image-service");

        GridClientOptimizedMarshaller gom = new GridClientOptimizedMarshaller();

        TcpDiscoverySpi spi = new TcpDiscoverySpi();



        TcpDiscoverySharedFsIpFinder finder = new TcpDiscoverySharedFsIpFinder();
        finder.setPath(tmpPath);
        spi.setLocalAddress("127.0.0.1");
        spi.setIpFinder(finder);


        IgniteConfiguration gc = new IgniteConfiguration();
        gc.setGridName("ApiAppEngine");
        gc.setMBeanServer(null);
        gc.setPeerClassLoadingEnabled(true);
        gc.setUserAttributes(userAttr);
        //gc.setLocalHost("127.0.0.1");
        gc.setDiscoverySpi(spi);
        //gc.setMarshaller(gom);
        return gc;
    }
}

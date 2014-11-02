package apiserver.grid;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGainSpring;
import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.net.UnknownHostException;

/**
 * Created by mnimer on 7/8/14.
 */
@Configuration
public class GridConfig
{
    @ImportResource("classpath:gridgain-context.xml")
    @Configuration
    public static class GridGainConfig
    {

        @Autowired
        private ApplicationContext appCtx;

        @Autowired
        private GridConfiguration gridCfg;


        @Bean(destroyMethod = "close")
        public Grid grid() throws GridException, UnknownHostException
        {
            Grid grid = GridGainSpring.start(gridCfg, appCtx);
            grid.configuration().setMetricsUpdateFrequency(0);
            grid.configuration().setPeerClassLoadingEnabled(true);
            GridOptimizedMarshaller gom = new GridOptimizedMarshaller();
            gom.setRequireSerializable(false);

            //GridServices services = grid.services();
            //AnalysisGridDAO analysisGridDAO = appCtx.getBean(AnalysisGridDAO.class);
            //GridFuture srvFuture1 = services.deployNodeSingleton("checkForUnmarkedJobs", new CheckForUnmarkedJobsService(analysisGridDAO));
            //GridFuture srvFuture2 = services.deployNodeSingleton("processWaitingJobs", new ProcessWaitingJobsService(analysisGridDAO));

            return grid;
        }

    }
}

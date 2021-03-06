package apiserver.filters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mnimer on 10/17/14.
 * @see http://kb.vmware.com/selfservice/microsites/search.do?language=en_US&cmd=displayKC&externalId=2005447
 */

public class MashapeAuthFilter implements Filter
{
    private static final Log log = LogFactory.getLog(MashapeAuthFilter.class);

    @Value("${mashape.key}")
    private String mashapeKey = null;

    private final String MASHAPE_REQUEST = "X-Mashape-Proxy-Secret";


    public void setMashapeKey(String mashapeKey)
    {
        this.mashapeKey = mashapeKey;
    }


    @Override public void init(FilterConfig filterConfig) throws ServletException
    {
        filterConfig.getServletContext();
    }


    @Override public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        //filterChain.doFilter(servletRequest, servletResponse);

        if( !((HttpServletRequest) servletRequest).getServletPath().startsWith("/api/") ) {
            filterChain.doFilter(servletRequest, servletResponse);
        }else {

            String apiKey = ((HttpServletRequest) servletRequest).getHeader(MASHAPE_REQUEST);
            if (apiKey == null) {
                //filterChain.doFilter(servletRequest, servletResponse);
                ((HttpServletResponse) servletResponse).sendError(403);
            } else {
                if (apiKey.equalsIgnoreCase(mashapeKey)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    log.debug("Error authenticating token: " +apiKey);
                    ((HttpServletResponse) servletResponse).sendError(403);
                }
            }
        }
    }


    public void destroy()
    {
       // System.out.println("destroy filer");
    }

}


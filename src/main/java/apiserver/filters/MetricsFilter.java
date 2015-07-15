package apiserver.filters;

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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnimer on 11/5/14.
 */
@Component
public class MetricsFilter implements Filter
{


    @Override public void init(FilterConfig filterConfig) throws ServletException
    {
        filterConfig.getServletContext();
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        Map metric = new HashMap();
        metric.put("path-info", ((HttpServletRequest)request).getPathInfo());
        metric.put("request-size", ((HttpServletRequest)request).getContentLength());


        Map headers = new HashMap();
        Enumeration<String> headerNames = ((HttpServletRequest)request).getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, ((HttpServletRequest)request).getHeader(headerName) );
        }
        metric.put("headers", headers);

        // Time & execute the request
        long startT = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long endT = System.currentTimeMillis();


        //log the in/out
        metric.put("start-time", startT);
        metric.put("end-time", System.currentTimeMillis());
        metric.put("response-size", ((HttpServletResponse) response).getHeader("Content-Length"));
        ((HttpServletResponse) response).addHeader("apiappengine-response-time", endT - startT + "ms");
        //Todo save object
    }


    @Override public void destroy()
    {
        // System.out.println("destroy filer");
    }

}
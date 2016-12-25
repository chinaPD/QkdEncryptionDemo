package CSModel;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by hadoop on 16-12-13.
 */
public class JettyServerWrapper {

    private Server jettyServer = null;

    public JettyServerWrapper(String ip, int port) {
        InetSocketAddress sockAddr = null;
        if (ip == null) {
            sockAddr = new InetSocketAddress(port);
        }else {
            sockAddr = new InetSocketAddress(ip, port);
        }
        jettyServer = new Server(sockAddr);
        ServletContextHandler handler = new ServletContextHandler(jettyServer, "/");
//        handler.addServlet(QkdHttpServlet.class, "/");
        ServletHolder fileUploadHolder = new ServletHolder(new ImageRcvServlet());
        fileUploadHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        handler.addServlet(fileUploadHolder, "/image");
//        ServletHandler servletHandler = new ServletHandler();
//        jettyServer.setHandler(servletHandler);
        handler.addServlet(QkdHttpServlet.class, "/");

        try {
            jettyServer.start();
//            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (jettyServer != null) {
            jettyServer.setStopAtShutdown(true);
            try {
                jettyServer.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            jettyServer.destroy();
        }
    }
}


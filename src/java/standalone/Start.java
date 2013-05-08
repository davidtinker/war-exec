package standalone;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Launches Jetty.
 */
public class Start {

    public static void main(String[] args) throws Exception {

        Properties p = new Properties();
        InputStream ins = Start.class.getResourceAsStream("start.properties");
        p.load(ins);
        ins.close();

        for (Map.Entry e : System.getProperties().entrySet()) {
            String key = (String)e.getKey();
            if (key.startsWith("jetty.")) p.setProperty(key, (String)e.getValue());
        }

        Server server = new Server();

        QueuedThreadPool tp = new QueuedThreadPool();
        tp.setMaxThreads(Integer.parseInt(p.getProperty("jetty.max.threads", "254")));
        tp.setMinThreads(Integer.parseInt(p.getProperty("jetty.min.threads", "8")));
        server.setThreadPool(tp);

        String[] hosts = p.getProperty("jetty.host", "127.0.0.1").split("[\\s]*,[\\s]*");
        int defaultPort = Integer.parseInt(p.getProperty("jetty.port", "8080"));
        Connector[] connectors = new Connector[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            SelectChannelConnector connector = new SelectChannelConnector();
            String host = hosts[i];
            int port;
            int j = host.indexOf('/');
            if (j < 0) {
                port = defaultPort;
            } else {
                port = Integer.parseInt(host.substring(j + 1));
                host = host.substring(0, j);
            }
            connector.setHost(host);
            connector.setPort(port);
            connector.setForwarded(true);
            connectors[i] = connector;
        }
        server.setConnectors(connectors);

        File war = new File(Start.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm());

        File temp = new File(p.getProperty("jetty.tmp", System.getProperty("user.home") + "/.jetty-tmp"));
        if (!temp.exists()) {
            if (!temp.mkdir()) {
                System.err.println("Unable to create [" + temp + "]");
                System.exit(1);
            }
        }

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath(p.getProperty("jetty.context.path", "/"));
        context.setTempDirectory(new File(temp, war.getName()));
        context.setWar(war.toString());

        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
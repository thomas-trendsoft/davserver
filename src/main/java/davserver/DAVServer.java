package davserver;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestParserFactory;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicLineParser;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import davserver.protocol.acl.Principal;
import davserver.protocol.auth.BasicAuthProvider;
import davserver.repository.cal.simple.SimpleCalDAVRepository;
import davserver.repository.card.SimpleCardDAVRepository;
import davserver.repository.file.SimpleFileRepository;
import davserver.repository.simple.SimpleRepository;
import davserver.utils.SimpleCredentialStore;

/**
 * Simple example server class 
 * 
 * @author tkrieger
 *
 */
public class DAVServer {
	
	/**
	 * Default WebDAV Namespace
	 */
	public final static String Namespace = "DAV:";
	
	/**
	 * CalDAV Namespace
	 */
	public final static String CalDAVNS = "urn:ietf:params:xml:ns:caldav";
	
	/**
	 * Base WebDAV Protocol
	 */
	public final static int PROT_WEBDAV  = 0;
	
	/**
	 * CalDAV protocol extension
	 */
	public final static int PROT_CALDAV  = 1;
	
	/**
	 * CardDAV protocol extension
	 */
	public final static int PROT_CARDDAV = 2;
	
	/**
	 * create a simple demo server
	 * 
	 * @return
	 */
	public static DAVServiceMapper createDemoServer() {
		
		// config log 
		Configurator.defaultConfig()
			.writer(new ConsoleWriter())
			.level(Level.DEBUG)
			.activate();

		
		SimpleCredentialStore credentials = new SimpleCredentialStore();
		BasicAuthProvider     auth        = new BasicAuthProvider(credentials);
		DAVServiceMapper      davService  = new DAVServiceMapper("");
		
		// demo admin and user 
		Principal p1 = new Principal("http://myadmin");
		Principal p2 = new Principal("http://mytestuser");
		credentials.addCredentials("admin", "admin", p1);
		credentials.addCredentials("test", "test", p2);
		
		// configure demo repositories
		davService.addRepository("simple", new SimpleRepository());
		
		
		
		// sample calendar and file server
		try {
			SimpleCardDAVRepository carddav = new SimpleCardDAVRepository();
			carddav.setAuthProvider(auth);
			davService.addRepository("contacts", carddav);
			
			SimpleCalDAVRepository caldav = new SimpleCalDAVRepository();
			caldav.setAuthProvider(auth);
			caldav.createCollection("/test",p1);
			caldav.createCollection("/test/mycalendar",p1);
			davService.addRepository("calendars", caldav);

			String root = Paths.get(".").toAbsolutePath().toString() + "/files";
			davService.addRepository("files", new SimpleFileRepository(root));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("failed to add special repositories: " + e.getMessage());
		}
		return davService;
	}
	
	/**
	 * Main server entry 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// create http server
        final IOReactorConfig config = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        DefaultHttpRequestParserFactory     rf = new DefaultHttpRequestParserFactory(BasicLineParser.INSTANCE, new WebDAVRequestFactory());
        DefaultNHttpServerConnectionFactory cf = new DefaultNHttpServerConnectionFactory(null,null,rf,null,null,null);

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8843)
                .setServerInfo("DAVServer/1.1")
                .setIOReactorConfig(config)
                .setConnectionFactory(cf)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .registerHandler("*", createDemoServer())
                .create();

        
        // run http server
        try {
            server.start();
            System.out.println("Serving on " + server.getEndpoint().getAddress());
        } catch (Exception e) {
        		e.printStackTrace();
        }

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
        			System.out.println("Server shutdown");
                server.shutdown(50, TimeUnit.MICROSECONDS);
            }
        });
		
	}

}

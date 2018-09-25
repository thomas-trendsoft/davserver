package davserver;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.Args;

/**
 * HTTP Components Request Factory for WebDAV HTTP methods
 * 	
 * @author tkrieger
 *
 */
public class WebDAVRequestFactory implements HttpRequestFactory {
	
    private static final String[] RFC2616_COMMON_METHODS = {
          "GET"
    };
    
    private static final String[] RFC2616_ENTITY_ENC_METHODS = {
        "POST",
        "PUT"
    };
    
    private static final String[] RFC4918_ENTITY_ENC_METHODS = {
            "PROPFIND",
            "PROPPATCH",
            "MKCOL",
            "LOCK"
    };

    private static final String[] RFC4918_COMMON_METHODS = {
            "COPY",
            "MOVE",
            "BIND",
            "UNLOCK"	
    };

    private static final String[] RFC2616_SPECIAL_METHODS = {
        "HEAD",
        "OPTIONS",
        "DELETE",
        "TRACE",
        "CONNECT"
    };
    
    private static boolean isOneOf(final String[] methods, final String method) {
        for (final String method2 : methods) {
            if (method2.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    public HttpRequest newHttpRequest(final RequestLine requestline)
            throws MethodNotSupportedException {
        Args.notNull(requestline, "Request line");
        final String method = requestline.getMethod();
        System.out.println("check method: " + method);
        if (isOneOf(RFC2616_COMMON_METHODS, method)  || isOneOf(RFC4918_COMMON_METHODS,method)) {
            return new BasicHttpRequest(requestline);
        } else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method) || isOneOf(RFC4918_ENTITY_ENC_METHODS,method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        } else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        } else {
            throw new MethodNotSupportedException(method +  " method not supported");
        }
    }

    public HttpRequest newHttpRequest(final String method, final String uri)
        throws MethodNotSupportedException {
        System.out.println("check method: " + method);
        if (isOneOf(RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        } else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method) || isOneOf(RFC4918_ENTITY_ENC_METHODS,method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        } else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        } else {
            throw new MethodNotSupportedException(method
                    + " method not supported");
        }
    }

}

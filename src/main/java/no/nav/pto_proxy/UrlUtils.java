package no.nav.pto_proxy;

import javax.servlet.http.HttpServletRequest;

public class UrlUtils {

    public static String getPathWithQueryString(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return path;
        } else {
            return path + "?" + queryString;
        }
    }

    /**
     * Removes the start path from a request.
     * Ex: startPath = /proxy, requestPath=/proxy/some/path --> /some/path
     * @param startPath path that will be stripped
     * @param requestPath path that will be stripped from
     * @return the requestPath without the startPath
     */
    public static String stripStartPath(String startPath, String requestPath) {
        if (requestPath.startsWith(startPath)) {
            return requestPath.substring(startPath.length());
        }

        return requestPath;
    }

    /**
     * Returns the first segment of a url path.
     * /some-path/test -> some-path
     * some-path/test -> some-path
     * @param urlPath the path to get the segment from
     * @return the first segment of the path
     */
    public static String getFirstSegment(String urlPath) {
        if (urlPath.startsWith("/")) {
            return urlPath.substring(1, urlPath.indexOf("/", 1));
        }

        return urlPath.substring(0, urlPath.indexOf("/"));
    }

}

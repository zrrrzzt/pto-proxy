package no.nav.pto_proxy;

import javax.servlet.http.HttpServletRequest;

public class UrlUtils {

    /**
     * Returns url with query parameters.
     * For a request such as "http://my-app.com/api/test?data=hello" then "/api/test?data=hello" will be returned
     * @param request the request to retrieve the url from
     * @return the full url which contains the entire path plus query params
     */
    public static String getFullUrl(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
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

}

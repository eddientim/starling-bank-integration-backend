package starlingtechchallenge.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class StarlingHeaders {

    private static String bearerToken;

    public StarlingHeaders(@Value("${starling.bearer-token}") final String bearerToken) {
        StarlingHeaders.bearerToken = bearerToken;
    }

    public static HttpEntity<HttpHeaders> getStarlingHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, APPLICATION_JSON_VALUE);
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.setBearerAuth(bearerToken);

        return new HttpEntity<>(headers);
    }
}

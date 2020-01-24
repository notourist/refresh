package eu.kvfg.refresh.util.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

/**
 * Builder for all the default responses.
 *
 * @author Lukas Nasarek
 */
public class ResponseBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Instant timestamp;

    private int status;

    private String path;

    private int code;

    private ResponseBuilder withCode(int status, String path, int code) {
        this.timestamp = Instant.ofEpochMilli(System.currentTimeMillis());
        this.status = status;
        this.path = path;
        this.code = code;
        return this;
    }

    public ResponseBuilder ok(String path) {
        return withCode(HttpServletResponse.SC_OK, path, 1);
    }

    public ResponseBuilder invalidClassException(String path) {
        return withCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, path, 10);
    }

    public ResponseBuilder unauthorized(String path, int code) {
        return withCode(HttpServletResponse.SC_UNAUTHORIZED, path, code);
    }

    public ResponseBuilder unauthorized(String path) {
        return unauthorized(path, 1);
    }

    private ResponseBuilder notFound(String path, int code) {
        return withCode(HttpServletResponse.SC_NOT_FOUND, path, code);
    }

    public ResponseBuilder sqlDataNotFound(String path) {
        return notFound(path, 4);
    }

    public ResponseBuilder stationNotFound(String path) {
        return notFound(path, 3);
    }

    public ResponseBuilder typeNotFound(String path) {
        return notFound(path, 2);
    }

    public ResponseBuilder notFound(String path) {
        return notFound(path, 1);
    }

    public ResponseBuilder methodNotAllowed(String path) {
        return withCode(HttpServletResponse.SC_METHOD_NOT_ALLOWED, path, 1);
    }

    public ResponseBuilder impossibleInterval(String path) {
        return withCode(HttpServletResponse.SC_CONFLICT, path, 1);
    }

    public ResponseBuilder internalServerError(String path) {
        return withCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, path, 1);
    }

    public ResponseBuilder missingParameter(String path) {
        return withCode(HttpServletResponse.SC_BAD_REQUEST, path, 1);
    }

    public Response build() {
        return new Response(timestamp, status, path, code);
    }

    public void writeTo(HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setHeader("Content-Type", "application/json");

        OutputStream outputStream = response.getOutputStream();
        MAPPER.writeValue(outputStream, build());
        outputStream.flush();
        outputStream.close();
    }
}

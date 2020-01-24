package eu.kvfg.refresh;

import eu.kvfg.refresh.grade.GradeComparatorException;
import eu.kvfg.refresh.grade.GradeParseException;
import eu.kvfg.refresh.logging.exception.ExceptionLogger;
import eu.kvfg.refresh.util.response.Response;
import eu.kvfg.refresh.util.response.ResponseBuilder;
import eu.kvfg.refresh.weather.exception.ImpossibleIntervalException;
import eu.kvfg.refresh.weather.exception.StationNotFoundException;
import eu.kvfg.refresh.weather.exception.TypeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.InvalidClassException;

/**
 * Catches every {@code Exception} produced by this server.
 *
 * @author Lukas Nasarek
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionLogger exceptionLogger;

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    public GlobalExceptionHandler(ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Response notFound(HttpServletRequest request) {
        return new ResponseBuilder()
            .notFound(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response parameterNotPresent(HttpServletRequest request) {
        return new ResponseBuilder()
                .missingParameter(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    public Response methodNotAllowed(HttpServletRequest request) {
        return new ResponseBuilder()
            .methodNotAllowed(request.getRequestURI())
            .build();
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Response internalServerError(HttpServletRequest request, Exception exception) {
        exceptionLogger.addException(exception);

        LOGGER.error("Unknown exception occurred!", exception);

        return new ResponseBuilder()
            .internalServerError(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(InvalidClassException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Response invalidClassException(HttpServletRequest request) {
        return new ResponseBuilder()
                .invalidClassException(request.getRequestURI())
                .build();
    }

    // Grade related exceptions

    @ExceptionHandler(GradeParseException.class)
    public void gradeParseException(GradeParseException exception) {
        LOGGER.error("Can't parse grade {}", exception.getGradeLiteral());
    }

    @ExceptionHandler(GradeComparatorException.class)
    public void gradeComparatorException(GradeComparatorException exception) {
        LOGGER.error("First grade was cover parsed.", exception);
    }

    // Weather related exceptions

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Response stationDataNotFound(HttpServletRequest request) {
        return new ResponseBuilder()
            .sqlDataNotFound(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Response stationNotFound(HttpServletRequest request) {
        return new ResponseBuilder()
            .stationNotFound(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(TypeNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Response typeNotFound(HttpServletRequest request) {
        return new ResponseBuilder()
            .typeNotFound(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(ImpossibleIntervalException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public Response impossibleInterval(HttpServletRequest request) {
        return new ResponseBuilder()
            .impossibleInterval(request.getRequestURI())
            .build();
    }
}

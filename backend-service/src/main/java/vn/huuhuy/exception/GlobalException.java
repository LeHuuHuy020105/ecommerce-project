package vn.huuhuy.exception;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Hidden
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler({ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class}
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                        {
                                            "timestamp": "2025-06-09T12:26:48.840+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "Not found"
                                        }
                                        """
                            )
                    )}
            )
    })
    public ErrorResponse handleValidationException(Exception e , WebRequest request){
        vn.huuhuy.exception.ErrorResponse errorResponse = new vn.huuhuy.exception.ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=",""));

        String message = e.getMessage();
        if(e instanceof MethodArgumentNotValidException){
            int start = message.lastIndexOf("[")+1;
            int end = message.lastIndexOf("]")-1;
            message = message.substring(start,end);
            errorResponse.setError("Invalid Payload");
            errorResponse.setMessage(message);
        } else if (e instanceof MissingServletRequestParameterException) {
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message.substring(message.indexOf("")+1));
        }else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }
        return errorResponse;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad request",
                content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                        examples = @ExampleObject(
                                name = "404 Response",
                                summary = "Handle exception when resource not found",
                                value = """
                                        {
                                            "timestamp": "2025-06-09T12:26:48.840+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "Not found"
                                        }
                                        """
                        )
                )}
            )
    })
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e , WebRequest request){
        vn.huuhuy.exception.ErrorResponse errorResponse = new vn.huuhuy.exception.ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getDescription(false).replace("uri=",""));
        return errorResponse;
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                        {
                                            "timestamp": "2025-06-09T12:26:48.840+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "conflict"
                                            "message": "{data} exists , Please try again!"
                                        }
                                        """ 
                            )
                    )}
            )
    })
    public ErrorResponse handleDuplicateKeyException(InvalidDataException e , WebRequest request){
        vn.huuhuy.exception.ErrorResponse errorResponse = new vn.huuhuy.exception.ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getDescription(false).replace("uri=",""));
        return errorResponse;
    }

}

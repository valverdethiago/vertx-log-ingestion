package com.ilegra.laa.models.exceptions;

import com.zandero.rest.exception.ExceptionHandler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.core.Response;

/**
 * Exception handler for @{@link ValidationException}
 *
 * @author valverde.thiago
 */
public class ValidationExceptionHandler implements ExceptionHandler<ValidationException> {
  @Override
  public void write(ValidationException e,
                    HttpServerRequest httpServerRequest,
                    HttpServerResponse httpServerResponse) throws Throwable {
    httpServerResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
    httpServerResponse.end(e.getMessage());
  }


}

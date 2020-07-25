package com.ilegra.laa.models.exceptions;

/**
 * Generic exception for validation errors
 *
 * @author valverde.thiago
 */
public class ValidationException extends RuntimeException{

  public ValidationException(String message) {
    super(message);
  }
}

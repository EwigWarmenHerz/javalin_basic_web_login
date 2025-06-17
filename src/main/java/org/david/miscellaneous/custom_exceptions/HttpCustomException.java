package org.david.miscellaneous.custom_exceptions;

public class HttpCustomException extends RuntimeException {
  public final int statusCode;
  public final String message;

  public HttpCustomException(String message, int statusCode) {
    var validatedMesage = "";
    if (message != null && !message.isBlank()) {
      validatedMesage = message;
    }
    this.message = validatedMesage;
    this.statusCode = statusCode;
  }
}

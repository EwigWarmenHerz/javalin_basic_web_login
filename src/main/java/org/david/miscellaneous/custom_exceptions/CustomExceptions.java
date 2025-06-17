package org.david.miscellaneous.custom_exceptions;

public interface CustomExceptions {

  static class InvalidBodyException extends HttpCustomException {
    public InvalidBodyException(String message) {
      super(defaultMessage(message), 400);
    }
  }

  static class ElementDoesNotExistException extends HttpCustomException {
    public ElementDoesNotExistException(String message) {
      super(defaultMessage(message), 400);
    }
  }

  static class DataIntegrityException extends HttpCustomException {
    public DataIntegrityException(String message) {
      super(defaultMessage(message), 400);
    }
  }

  static class InvalidPasswordException extends HttpCustomException {
    public InvalidPasswordException(String message) {
      super(defaultMessage(message), 403);
    }
  }

  static class FailedToCreateUserException extends HttpCustomException {
    public FailedToCreateUserException(String message) {
      super(defaultMessage(message), 500);
    }
  }

  private static String defaultMessage(String message) {
    return (message == null || message.isBlank()) ? "" : message;
  }
}

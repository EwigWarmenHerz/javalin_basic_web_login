package org.david.miscellaneous.custom_exceptions;

public interface CustomExceptions {
    class InvalidBodyException extends HttpCustomException{
        public InvalidBodyException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 400);
        }
    }

    class ElementDoNotExistException extends HttpCustomException{
        public ElementDoNotExistException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 400);
        }
    }

    class DataIntegrityException extends HttpCustomException{
        public DataIntegrityException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 400);
        }
    }

    class InvalidPasswordException extends HttpCustomException{
        public InvalidPasswordException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 403);
        }
    }
    class FailedToCreateUserException extends HttpCustomException{
        public FailedToCreateUserException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 40);
        }
    }
    class GenericSQLException extends HttpCustomException{
        public GenericSQLException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message, 400);
        }
    }
}

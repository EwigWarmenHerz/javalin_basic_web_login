package org.david.miscellaneous;

public interface CustomExceptions {
    class InvalidBodyException extends RuntimeException{
        public InvalidBodyException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message);
        }
    }

    class ElementDoNotExistException extends RuntimeException{
        public ElementDoNotExistException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message);
        }
    }

    class DataIntegrityException extends RuntimeException{
        public DataIntegrityException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message);
        }
    }

    class InvalidPasswordException extends RuntimeException{
        public InvalidPasswordException(String message){
            if(message == null || message.isBlank()){
                message = "";
            }
            super(message);
        }
    }
}

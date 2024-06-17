package com.forumsystem.еxceptions;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message){
        super(message);
    }
}

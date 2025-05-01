package com.example.springjwt.global.error.exception;

import com.example.springjwt.global.error.ErrorCode;

public class NotFoundUserException extends BusinessException{
    public NotFoundUserException(ErrorCode errorCode) {super(errorCode);}
}

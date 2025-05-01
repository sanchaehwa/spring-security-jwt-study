package com.example.springjwt.global.error.exception;

import com.example.springjwt.global.error.ErrorCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

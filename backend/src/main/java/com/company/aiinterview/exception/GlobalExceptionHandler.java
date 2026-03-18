package com.company.aiinterview.exception;

import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.common.Result;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public Result<Void> handleApiException(ApiException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidation(Exception e) {
        String msg = "validation failed";
        if (e instanceof MethodArgumentNotValidException m) {
            msg = m.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException b) {
            msg = b.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail(ErrorCode.INTERNAL_ERROR.getCode(), e.getMessage());
    }
}

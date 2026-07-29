package com.emiliaovo.yukitodo.exception;

import com.emiliaovo.yukitodo.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 负责捕获业务异常，并转换成统一的 HTTP 错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
     * 处理注册时，两次输入的密码不一致
     */
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(
            PasswordMismatchException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "PASSWORD_MISMATCH",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 处理用户名或邮箱已存在。
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExists(
            AccountAlreadyExistsException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "ACCOUNT_ALREADY_EXISTS",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * 处理请求参数校验失败。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        FieldError fieldError =
                exception.getBindingResult().getFieldError();

        String message = fieldError == null
                ? "请求参数校验失败"
                : fieldError.getDefaultMessage();

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 处理用户名或密码错误/账号不存在。
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "INVALID_CREDENTIALS",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 处理用户未登录。
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "UNAUTHORIZED",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 处理当前用户创建同名课程。
     */
    @ExceptionHandler(CourseAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCourseAlreadyExists(
            CourseAlreadyExistsException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "COURSE_ALREADY_EXISTS",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * 处理课程不存在或不属于当前用户。
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(
            CourseNotFoundException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "COURSE_NOT_FOUND",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}

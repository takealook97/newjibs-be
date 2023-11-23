package com.ssafy.newjibs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private Integer errorCode;
    private String errorMsg;
}

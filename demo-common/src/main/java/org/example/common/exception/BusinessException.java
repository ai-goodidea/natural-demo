package org.example.common.exception;

import lombok.Getter;
import org.example.common.result.ResultCode;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
    }

    public BusinessException(ResultCode rc) {
        super(rc.getMessage());
        this.code = rc.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}

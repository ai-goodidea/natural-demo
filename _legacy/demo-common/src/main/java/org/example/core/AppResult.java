package org.example.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goodidea.core.enums.AppBaseEnum;
import com.goodidea.core.lang.LangUtils;
import com.goodidea.core.service.AppBaseResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResult<T> implements Serializable {

    private String id;
    private String tokenId;
    private String key;
    private String group;
    private String code;
    private String status;
    private String name;
    private String value;
    private String token;
    private String tokenKey;
    private String traceId;

    private String timestamp;
    private String message;

    private String error;
    private String exception;
    private T data;
    private T result;

    private AppPage page;

    private Boolean success;


    public static <T> AppResult<T>  data(T data) {
        return (AppResult<T>) AppResult.success().setData(data);
    }

    public AppResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public void error() {
        this.error(null);
    }

    public AppResult error(String error) {
        this.setError(error);
        this.setSuccess(false);
        return this;
    }


    public Boolean isSuccess() {
        if (LangUtils.isEmpty(this.error)) {
            return true;
        }
        return false;
    }

    public static <T> AppResult<T> success() {
        return success("操作成功");
    }

    public static <T> AppResult<T> success(Object result) {
        if (LangUtils.isNotEmpty(result)) {
            if (result instanceof String) {
                String rs = (String) result;
                if (rs.startsWith("{") || rs.startsWith("[")) {
                    return success("", rs);
                }
                return success(rs, "");
            } else {
                return success("", result);
            }
        } else {
            AppResult ar = new AppResult();
            ar.setSuccess(true);
            ar.setData(result);
            return ar;
        }
    }

    public static <T> AppResult<T> success(AppParameter ap, Object result) {
        AppResult ar = new AppResult();
        ar.setSuccess(true);
        ar.setAppParameter(ap);
        ar.setData(result);
        if (LangUtils.isNotEmpty(ap)) {
            ar.setPage(ap.getPage());
        }
        return ar;

    }

    public static <T> AppResult<T> success(String message, Object result) {
        AppResult ar = new AppResult();
        ar.setSuccess(true);
        ar.setMessage(message);
        if (LangUtils.isNotEmpty(result)) {
            ar.setData(result);
        }
        return ar;
    }
    public static AppResult failed() {
        AppResult res = new AppResult();
        res.setError("failed");
        res.setMessage("error");
        return res;
    }

    public static AppResult failed(AppBaseResultCode resultCode) {
        AppResult res = new AppResult();
        res.setCode(resultCode.getCode());
        res.setError("failed");
        res.setMessage(resultCode.getMessage());
        return res;
    }

    public static <T> AppResult<T> failed(String message) {
        return failed(message, null);
    }

    public static <T> AppResult<T> failed(String message, Object... params) {
        AppResult res = new AppResult();
        res.setSuccess(false);
        res.setMessage(LangUtils.formatMessage(message, params));
        return res;
    }

    public static AppResult failed(AppBaseEnum baseEnum, String... params) {
        AppResult res = new AppResult();
        res.setSuccess(false);
        res.setMessage(LangUtils.formatMessage(baseEnum.getText(), params));
        return res;
    }

    public String getError() {
        return error;
    }

    public AppResult setError(String error) {
        this.error = error;
        this.setSuccess(false);
        return this;
    }

    public AppResult setAppParameter(AppParameter ap) {
        if (LangUtils.isNotEmpty(ap) && LangUtils.isNotEmpty(ap.getPage())) {
            this.setPage(ap.getPage());
        }
        return this;
    }

    public <T> T getData() {
        return (T) data;
    }

    public <T> T getData(Class<T> clazz) {
        return (T) data;
    }

    public AppResult setKey(String key) {
        this.key = key;
        return this;
    }
}

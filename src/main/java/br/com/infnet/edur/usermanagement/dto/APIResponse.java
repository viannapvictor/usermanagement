package br.com.infnet.edur.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    
    private boolean success;
    private T data;
    private int code;
    private String errorMessage;
    
    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(true, data, 200, null);
    }
    
    public static <T> APIResponse<T> success(T data, int code) {
        return new APIResponse<>(true, data, code, null);
    }
    
    public static <T> APIResponse<T> error(String errorMessage, int code) {
        return new APIResponse<>(false, null, code, errorMessage);
    }
}
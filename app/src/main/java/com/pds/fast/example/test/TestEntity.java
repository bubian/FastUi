package com.pds.fast.example.test;

import com.google.gson.annotations.SerializedName;

public class TestEntity {

    @SerializedName(value = "code_code" )
    private String codeCode;
    private String action;
    private String message;
    private DataDTO data;

    public String getCode() {
        return codeCode;
    }

    public void setCode(String code) {
        this.codeCode = code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }


    public static class DataDTO {
        private String dot;

        public String getDot() {
            return dot;
        }

        public void setDot(String dot) {
            this.dot = dot;
        }
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "code='" + codeCode + '\'' +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

package com.ilegra.laa.models;

public class ResponseModel {

  private int code;
  private String message;

  public ResponseModel(int code, String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public ResponseModel() { }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ResponseModel [code=" + code + ", message=" + message + "]";
  }
}

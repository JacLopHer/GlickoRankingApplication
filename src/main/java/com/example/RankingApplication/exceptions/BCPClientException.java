package com.example.RankingApplication.exceptions;

public class BCPClientException extends RuntimeException {
  public BCPClientException(String message) {
    super(message);
  }

  public BCPClientException(String message, Throwable cause) {
    super(message, cause);
  }
}

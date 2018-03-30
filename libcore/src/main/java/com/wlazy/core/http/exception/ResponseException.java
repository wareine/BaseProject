package com.wlazy.core.http.exception;

public class ResponseException extends Exception {
        public int code;
        public String message;

        public ResponseException(Throwable throwable, int code) {
            super(throwable);
            this.code = code;

        }
    }
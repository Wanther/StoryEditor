package org.nojob.storyeditor.exception;

/**
 * Created by wanghe on 16/8/4.
 */
public class AppException extends Exception {
    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}

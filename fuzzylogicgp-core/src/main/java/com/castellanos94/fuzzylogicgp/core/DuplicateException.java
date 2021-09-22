package com.castellanos94.fuzzylogicgp.core;

public class DuplicateException extends Exception {
    /**
	 *
	 */
	private static final long serialVersionUID = 1997753363232807019L;

	public DuplicateException() {
    }

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(Throwable cause) {
        super(cause);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
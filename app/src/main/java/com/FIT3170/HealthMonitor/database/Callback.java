package com.FIT3170.HealthMonitor.database;

/**
 * This interface represents a simple callback
 * @param <T> the type of the result provided to the callback
 */
public interface Callback <T, E extends  Exception> {

    /**
     * The onCall method is called once the result is ready
     * @param result if the action succeeded, result holds the
     *               result of the action, it is null otherwise.
     *
     * @param exception if the action succeeded exception will be null, otherwise
     *             it contains the exception that caused the action to fail.
     *
     * implementations of this interface should always check the error first,
     * if it is null then they can assume the action was successful.
     */
    void onCall(T result, E exception);
}

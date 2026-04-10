package com.movieticketapp.firebase;

public interface DataCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}

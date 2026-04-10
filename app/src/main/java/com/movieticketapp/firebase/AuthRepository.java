package com.movieticketapp.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.movieticketapp.models.User;
import com.movieticketapp.utils.DateTimeUtils;

import java.util.Date;

public class AuthRepository {
    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseManager.getAuth();
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return auth.getCurrentUser();
    }

    public void login(String email, String password, DataCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(mapAuthError(e)));
    }

    public void register(String fullName, String email, String phone, String password, DataCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) {
                        callback.onError("Unable to create account right now.");
                        return;
                    }
                    User user = new User(firebaseUser.getUid(), fullName, email, phone, "", DateTimeUtils.toIsoString(new Date()));
                    FirebaseManager.getFirestore()
                            .collection("users")
                            .document(firebaseUser.getUid())
                            .set(user.toMap())
                            .addOnSuccessListener(unused -> callback.onSuccess(firebaseUser))
                            .addOnFailureListener(e -> callback.onError("Account created but profile setup failed. Please try again."));
                })
                .addOnFailureListener(e -> callback.onError(mapAuthError(e)));
    }

    public void getCurrentUserProfile(DataCallback<User> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("No logged in user.");
            return;
        }
        FirebaseManager.getFirestore()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    User user = snapshot.toObject(User.class);
                    if (user != null && user.getUserId() == null) {
                        user.setUserId(snapshot.getId());
                    }
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onError("Unable to load profile."));
    }

    public void logout() {
        auth.signOut();
    }

    private String mapAuthError(@NonNull Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) exception).getErrorCode();
            if ("ERROR_INVALID_EMAIL".equals(code)) {
                return "Email không đúng định dạng";
            }
            if ("ERROR_USER_NOT_FOUND".equals(code) || "ERROR_WRONG_PASSWORD".equals(code) || "ERROR_INVALID_CREDENTIAL".equals(code)) {
                return "Email hoặc mật khẩu không chính xác";
            }
            if ("ERROR_EMAIL_ALREADY_IN_USE".equals(code)) {
                return "Email này đã được sử dụng";
            }
            if ("ERROR_WEAK_PASSWORD".equals(code)) {
                return "Mật khẩu phải có ít nhất 6 ký tự";
            }
        }
        return exception.getMessage() == null ? "Không thể kết nối Firebase lúc này" : exception.getMessage();
    }
}

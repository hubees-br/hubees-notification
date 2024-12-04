package com.hubees.hubeesnotification;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = null;

        try {
            // Obtém o token de autenticação do Firebase
            token = FirebaseAuth.getInstance().getCurrentUser()
                    .getIdToken(true)
                    .getResult()
                    .getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adiciona o token ao cabeçalho Authorization
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request modifiedRequest = builder.build();
        return chain.proceed(modifiedRequest);
    }
}

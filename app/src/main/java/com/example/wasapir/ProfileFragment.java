package com.example.wasapir;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private TextView profileLabel;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileLabel = view.findViewById(R.id.profileLabel);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Mostrar correo del usuario actual
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        profileLabel.setText("Usuario actual: " + email);

        // Acción de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Redirigir al LoginActivity (asegúrate de tenerlo creado)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
package com.example.wasapir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment {

    private TextView profileLabel;
    private Button logoutButton, saveButton;
    private EditText nicknameInput, ageInput, descriptionInput;
    private ImageView profileImageView;

    private DatabaseReference dbRef;
    private String myUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileLabel = view.findViewById(R.id.profileLabel);
        logoutButton = view.findViewById(R.id.logoutButton);
        saveButton = view.findViewById(R.id.saveButton);
        nicknameInput = view.findViewById(R.id.nicknameInput);
        ageInput = view.findViewById(R.id.ageInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        profileImageView = view.findViewById(R.id.profileImageView);

        // Mostrar correo del usuario actual
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        profileLabel.setText("Usuario actual: " + email);

        // Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Cargar datos existentes
        dbRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if (profile != null) {
                    nicknameInput.setText(profile.getNickname());
                    if (profile.getAge() != null) {
                        ageInput.setText(String.valueOf(profile.getAge()));
                    }
                    descriptionInput.setText(profile.getDescription());
                    // Aquí podrías cargar la imagen con Glide/Picasso si hay URL
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("ProfileFragment", "Error al cargar perfil: " + error.getMessage());
            }
        });

        // Guardar cambios
        saveButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            String ageStr = ageInput.getText().toString().trim();
            Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);
            String description = descriptionInput.getText().toString().trim();

            // Por ahora dejamos la foto en null (se puede integrar Firebase Storage después)
            UserProfile profile = new UserProfile(nickname, age, description, null);
            dbRef.child(myUid).setValue(profile);
        });

        // Acción de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
package com.example.wasapir;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<User> contactsList = new ArrayList<>();
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = view.findViewById(R.id.contactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ContactsAdapter(contactsList, contact -> {
            // Al tocar un contacto, abrir ChatActivity
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("contactUid", contact.getUid());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 🔧 Conectar a Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Escuchar cambios en tiempo real
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear(); // limpiar lista antes de recargar
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String uid = userSnap.getKey();
                    String name = userSnap.child("name").getValue(String.class);
                    if (uid != null && name != null) {
                        contactsList.add(new User(uid, name));
                    }
                }
                adapter.notifyDataSetChanged(); // refrescar RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error si falla la lectura
            }
        });

        return view;
    }
}
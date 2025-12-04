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

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<User> contactsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = view.findViewById(R.id.contactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔧 Aquí deberías cargar los contactos desde Firebase Database/Firestore
        // Ejemplo de contactos de prueba:
        contactsList.add(new User("uid123", "Ana"));
        contactsList.add(new User("uid456", "Carlos"));

        adapter = new ContactsAdapter(contactsList, contact -> {
            // Al tocar un contacto, abrir ChatActivity
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("contactUid", contact.getUid());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
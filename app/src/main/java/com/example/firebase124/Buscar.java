package com.example.firebase124;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Buscar extends AppCompatActivity {

    EditText editTextBuscar;
    ListView listViewBuscar;
    List<Pessoa> pessoaList = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        editTextBuscar = findViewById(R.id.editTextBuscarNome);
        listViewBuscar = findViewById(R.id.listViewBuscar);

        inicializarFirebase();
        eventoEdit();

    }

    private void eventoEdit() {
        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String palavra = editTextBuscar.getText().toString();
                pesquisarPalavra(palavra);

            }
        });
    }

    private void pesquisarPalavra(String palavra) {
        Query query;

        if (palavra.equals("")) {
            query = databaseReference.child("Pessoa").orderByChild("nome");
        }
        else {
            query = databaseReference.child("Pessoa").orderByChild("nome").
                    startAt(palavra).endAt(palavra + "\uf8ff");
        }

        pessoaList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Pessoa p = dataSnapshot1.getValue(Pessoa.class);
                    pessoaList.add(p);
                }

                pessoaArrayAdapter = new ArrayAdapter<>(
                      Buscar.this,
                      android.R.layout.simple_list_item_1,
                      pessoaList
                );

                listViewBuscar.setAdapter(pessoaArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Buscar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        pesquisarPalavra("");
    }
}

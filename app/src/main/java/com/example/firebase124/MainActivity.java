package com.example.firebase124;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edNome, edEmail;
    ListView listView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Pessoa> pessoaList = new ArrayList<>();
    List<DadosLojas> dadosLojasLista = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;
    ArrayAdapter<DadosLojas> dadosLojasArrayAdapter;
    Pessoa pessoaSelecionada;

    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edEmail = findViewById(R.id.editTextEmail);
        edNome = findViewById(R.id.editTextNome);
        listView = findViewById(R.id.listView1);

        inicializarFirebase();


        //TODO importante: leia abaixo

        //base de dados (nó User):
        //https://console.firebase.google.com/u/1/project/fir-124/database/fir-124/data

        //1) trazer todos as lojas de um usuário específico
       // eventoDatabase();

        //2) trazer todas as lojas (inclusive de usuários diferentes) de uma categoria específica
        lojasPorCat();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pessoaSelecionada = (Pessoa) adapterView.getItemAtPosition(i);
                edNome.setText(pessoaSelecionada.getNome());
                edEmail.setText(pessoaSelecionada.getEmail());
            }
        });



    }

    private void lojasPorCat() {
        final String categoria = "cat2"; //trazer via formulário ou pelo Auth
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot: dataSnapshot.getChildren()) { //este for itera os usuários
                    //este for itera os objetos (lojas) dos usuários
                    for (DataSnapshot objDataSnapshotLojas: objDataSnapshot.getChildren()) {
                        if (!objDataSnapshotLojas.getKey().equals("nome")){ //p/ não pegar dados do usuário
                            if (objDataSnapshotLojas.child("cat").getValue().toString().equals(categoria)) { //p/ só trazer de uma categoria específica
                                DadosLojas dadosLojas = new DadosLojas();
                                dadosLojas.setBox(objDataSnapshotLojas.child("box").getValue().toString());
                                dadosLojas.setCategoria(objDataSnapshotLojas.child("cat").getValue().toString());

                                dadosLojasLista.add(dadosLojas);
                            }
                            i++;
                        }
                    }
                }

                dadosLojasArrayAdapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        dadosLojasLista
                );

                listView.setAdapter(dadosLojasArrayAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void eventoDatabase() {
        String userId = "87654321"; //trazer via formulário ou pelo Auth

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot objDataSnapshot: dataSnapshot.getChildren()) {

                    if (!objDataSnapshot.getKey().toString().equals("nome")){ //para não pegar nó com atributos do usuário
                        DadosLojas dadosLojas = new DadosLojas();
                        dadosLojas.setBox(objDataSnapshot.child("box").getValue().toString());
                        dadosLojas.setCategoria(objDataSnapshot.child("cat").getValue().toString());

                        dadosLojasLista.add(dadosLojas);
                    }
                }

                dadosLojasArrayAdapter = new ArrayAdapter<>(
                  MainActivity.this,
                  android.R.layout.simple_list_item_1,
                        dadosLojasLista
                );

                listView.setAdapter(dadosLojasArrayAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_novo) {
            Pessoa p = new Pessoa();
            p.setNome(edNome.getText().toString());
            p.setEmail(edEmail.getText().toString());
            p.setId(UUID.randomUUID().toString());

            databaseReference.child("Pessoa").
                    child(p.getNome()).setValue(p);

            limparCampos();

        }

        if (id == R.id.menu_editar) {
            Pessoa p = new Pessoa();
            p.setNome(edNome.getText().toString());
            p.setEmail(edEmail.getText().toString());
            p.setId(pessoaSelecionada.getId());

            databaseReference.child("Pessoa").child(pessoaSelecionada.getNome()).setValue(p);

        }
        if (id == R.id.menu_excluir) {
            Pessoa p = new Pessoa();
            p.setNome(pessoaSelecionada.getNome());

            databaseReference.child("Pessoa").child(p.getNome()).removeValue();
        }
        if (id == R.id.menu_buscar) {
            Intent intent = new Intent(MainActivity.this, Buscar.class);
            startActivity(intent);
        }






        return super.onOptionsItemSelected(item);
    }

    private void limparCampos() {
        edNome.setText("");
        edEmail.setText("");
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }
}

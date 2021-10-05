package ca.qc.bdeb.c5gm.stageplanif;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Compte> listeCompte = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListeCompteAdapter compteAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        //Donnees de test, a enlever
        listeCompte.add(new Compte("Paquet", "Xavier", null, 1));
        listeCompte.add(new Compte("Havekes", "Mathias", null, 1));
        listeCompte.add(new Compte("Hadjeres", "Amar", null, 1));
        creationRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    /**
     * Cree le recycler view dans l'activitee
     */
    private void creationRecyclerView() {
        recyclerView = findViewById(R.id.rv_eleves);
        compteAdapter = new ListeCompteAdapter(this, listeCompte);
        recyclerView.setAdapter(compteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
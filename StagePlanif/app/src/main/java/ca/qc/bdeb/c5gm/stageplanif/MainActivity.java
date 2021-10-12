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
    private Stockage dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        dbHelper = Stockage.getInstance(getApplicationContext());
        ajouterComptes();
        ajouterCompteDansBD();
        creationRecyclerView();
    }

    private void ajouterCompteDansBD() {
        for (Compte compte: listeCompte) {
            dbHelper.ajouterClient(compte);
        }
    }

    private void ajouterComptes() {
        listeCompte.add(new Compte("Boucher", "Mikaël", null, 2));
        listeCompte.add(new Compte("Caron", "Thomas", null, 2));
        listeCompte.add(new Compte("Gingras", "Simon", null, 2));
        listeCompte.add(new Compte("Leblanc", "Kevin", null, 2));
        listeCompte.add(new Compte("Masson", "Cédric", null, 2));
        listeCompte.add(new Compte("Monette", "Vanessa", null, 2));
        listeCompte.add(new Compte("Picard", "Vincent", null, 2));
        listeCompte.add(new Compte("Poulain", "Mélissa", null, 2));
        listeCompte.add(new Compte("Vargas", "Diego", null, 2));
        listeCompte.add(new Compte("Tremblay", "Geneviève", null, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Stockage.getInstance(getApplicationContext()).close();
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
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
    private ArrayList<Compte> listeCompte = new ArrayList<>();
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
        listeCompte = dbHelper.getComptes(TypeComptes.ELEVE);
        creationRecyclerView();
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
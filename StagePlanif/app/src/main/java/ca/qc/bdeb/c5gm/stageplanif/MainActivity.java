package ca.qc.bdeb.c5gm.stageplanif;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Compte> listeComptes = new ArrayList<>();
    private final ArrayList<Compte> listeComptesMasques = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListeCompteAdapter compteAdapter;
    private Toolbar toolbar;
    private ItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            listeComptes.addAll(listeComptesMasques);
            listeComptesMasques.clear();
            trierListeCompte(listeComptes);
            ArrayList<Integer> ListePrioritesSelectionnees = calculerPrioritesSelectionnees(selection);
            filtrerListeCompte(ListePrioritesSelectionnees);
        });

        //Donnees de test, a enlever
        listeComptes.add(new Compte("Paquet", "Xavier", null, 1, Priorite.MAXIMUM));
        listeComptes.add(new Compte("Havekes", "Mathias", null, 1, Priorite.MOYENNE));
        listeComptes.add(new Compte("xxHavekes", "Mathias", null, 1, Priorite.MOYENNE));
        listeComptes.add(new Compte("ggHavekes", "Mathias", null, 1, Priorite.MINIMUM));
        listeComptes.add(new Compte("ffHavekes", "Mathias", null, 1, Priorite.MINIMUM));
        listeComptes.add(new Compte("lHavekes", "Mathias", null, 1, Priorite.MAXIMUM));
        listeComptes.add(new Compte("kkHavekes", "Mathias", null, 1, Priorite.MOYENNE));
        listeComptes.add(new Compte("ttHavekes", "Mathias", null, 1, Priorite.MOYENNE));
        listeComptes.add(new Compte("hgfhHavekes", "Mathias", null, 1, Priorite.MAXIMUM));
        listeComptes.add(new Compte("Hadjeres", "Amar", null, 1, Priorite.MINIMUM));

        creationRecyclerView();
    }

    private ArrayList<Integer> calculerPrioritesSelectionnees(int selection) {
        ArrayList<Integer> ListePrioritesSelectionnees = new ArrayList<Integer>();

        for (Priorite p : Priorite.values()) {
            if ((selection & p.getValeur()) > 0) {
                ListePrioritesSelectionnees.add(p.getValeur());
            }
        }
        return ListePrioritesSelectionnees;
    }

    private void filtrerListeCompte(ArrayList<Integer> ListePrioritesSelectionnees) {
        for (Compte c : listeComptes) {
            if (!ListePrioritesSelectionnees.contains(c.getPriorite().getValeur())) {
                listeComptesMasques.add(c);
            }
        }
        listeComptes.removeAll(listeComptesMasques);
        compteAdapter.notifyDataSetChanged();
    }

    private void trierListeCompte(ArrayList<Compte> liste) {
        Collections.sort(liste, new CompteChainedComparateur(
                new ComptePrioriteComparateur(),
                new CompteNomComparateur(),
                new ComptePrenomComparateur()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.passer_sur_carte) {
            Intent intent = new Intent(this, GoogleMaps.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
        compteAdapter = new ListeCompteAdapter(this, listeComptes);
        recyclerView.setAdapter(compteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

        compteAdapter.setOnItemClickListener(new ListeCompteAdapter.OnItemClickListener() {
            @Override
            public void OnDrapeauClick(int position, ImageView favoriteView) {
                changerPrioriteEleve(position);
            }

            @Override
            public void OnImageEleveClick(int position) {

            }
        });
    }

    private void changerPrioriteEleve(int positionEleve) {
        int prioriteActuel = listeComptes.get(positionEleve).getPriorite().ordinal();
        int prochainePriorite = prioriteActuel++;
        Priorite[] priorites = Priorite.values();
        prochainePriorite %= priorites.length;
        listeComptes.get(positionEleve).setPriorite(priorites[prochainePriorite]);
        compteAdapter.notifyItemChanged(positionEleve);
    }

    public void lancerActiviteAjoutEleve(View view) {
        Intent intent = new Intent(this, DemandeInfoEleve.class);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int removedItemIndex = viewHolder.getAdapterPosition();
            listeComptes.remove(removedItemIndex);
            compteAdapter.notifyItemRemoved(removedItemIndex);
        }
    };
}
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton btnAjouterEleve;
    private Stockage dbHelper;
    private ArrayList<Stage> listeStages;
    private ArrayList<Stage> listeStagesMasques;
    private RecyclerView recyclerView;
    private ListeStageAdapter StageAdapter;
    private Toolbar toolbar;
    private ItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById((R.id.toolbar));
        btnAjouterEleve = findViewById(R.id.btn_ajouter_eleve);
        setSupportActionBar(toolbar);
        dbHelper = Stockage.getInstance(getApplicationContext());
        listeStages = dbHelper.getStages();
        listeStagesMasques = new ArrayList<>();

        creationViewModel();

        creationRecyclerView();
        mettreAJourlisteStages();
        btnAjouterEleve.setOnClickListener(ajouterEleveOnClickListener);
    }

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            mettreAJourlisteStages(selection);
            StageAdapter.notifyDataSetChanged();
        });
    }

    private void mettreAJourlisteStages() {
        listeStages = Utils.trierListeStages(listeStages, new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        StageAdapter.notifyDataSetChanged();
    }

    private void mettreAJourlisteStages(int selection) {
        listeStages.addAll(listeStagesMasques);
        listeStagesMasques.clear();

        ArrayList<Integer> listePrioritesSelectionnees = Utils.calculerPrioritesSelectionnees(selection);
        listeStagesMasques = Utils.filtrerListeStages(listePrioritesSelectionnees, listeStages);
        listeStages.removeAll(listeStagesMasques);
        listeStages = Utils.trierListeStages(listeStages, new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        StageAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.passer_sur_carte) {
            startGoogleMapActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startGoogleMapActivity() {
        listeStages.addAll(listeStagesMasques);
        Intent intent = new Intent(this, GoogleMaps.class);
        intent.putParcelableArrayListExtra("liste_des_stages", listeStages);
        startActivity(intent);
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
        StageAdapter = new ListeStageAdapter(this, listeStages);
        recyclerView.setAdapter(StageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

        StageAdapter.setOnItemClickListener(new ListeStageAdapter.OnItemClickListener() {
            @Override
            public void OnDrapeauClick(int position, ImageView favoriteView) {

            }

            @Override
            public void OnImageEleveClick(int position) {

            }

            @Override
            public void OnItemViewClick(int position) {

            }
        });
    }

    private final View.OnClickListener ajouterEleveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lancerActiviteAjoutEleve();
        }
    };

    public void lancerActiviteAjoutEleve() {
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
            listeStages.remove(removedItemIndex);
            StageAdapter.notifyItemRemoved(removedItemIndex);
        }
    };
}
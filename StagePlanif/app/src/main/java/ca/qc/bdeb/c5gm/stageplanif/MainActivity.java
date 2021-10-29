package ca.qc.bdeb.c5gm.stageplanif;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Classe qui s'occupe de l'activite activity_main
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Onclick listener pour lancer l'activitee d'ajout de stage
     */
    private final View.OnClickListener ajouterEleveOnClickListener = view -> lancerActiviteAjoutStage(view);
    /**
     * Contient le SwipeRefreshLayout
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Contient le FloatingActionButton d'ajout d'eleve
     */
    private FloatingActionButton btnAjouterEleve;
    /**
     * Contient le lien avec la BD
     */
    private Stockage dbHelper;
    /**
     * Liste des stages
     */
    private ArrayList<Stage> listeStages;
    /**
     * Contient le recycler view des stages
     */
    private RecyclerView recyclerView;
    /**
     * L'adapteur des stages
     */
    private ListeStageAdapter stageAdapter;
    /**
     * Defini le logique de swipe d'un item du recycler view
     */
    final ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog alertDialog = new AlertDialog.Builder(viewHolder.itemView.getContext()).create();
            alertDialog.setTitle(R.string.titre_avertissement);
            alertDialog.setMessage(getString(R.string.message_supprimer));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.message_oui),
                    (dialogInterface, i) -> {
                        int indexEnleve = viewHolder.getAdapterPosition();
                        dbHelper.deleteStage(listeStages.get(indexEnleve));
                        listeStages.remove(indexEnleve);
                        stageAdapter.notifyItemRemoved(indexEnleve);
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.annuler_message),
                    (dialogInterface, i) -> {
                        int indexEnleve = viewHolder.getAdapterPosition();
                        stageAdapter.notifyItemChanged(indexEnleve);
                    });
            alertDialog.show();
            return;
        }
    };
    /**
     * La toolbar
     */
    private Toolbar toolbar;
    /**
     * Contient le view model pour communiquer avec le fragment de selection de priorite
     */
    private SelectionViewModel viewModel;
    /**
     * Contient la valeur des selections de priorite, est a jour en tout temps
     */
    private int selectionPriorites;
    /**
     * Lance l'activitee d'information de stage en demandant un resultat et analyse du resultat
     */
    final ActivityResultLauncher<Intent> envoyerInfoStageActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Stage stage = intent.getParcelableExtra("stage");
                    mettreDansRV(stage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        btnAjouterEleve = findViewById(R.id.btn_ajouter_eleve);
        setSupportActionBar(toolbar);
        dbHelper = Stockage.getInstance(getApplicationContext());
        listeStages = dbHelper.getStages();
        selectionPriorites = Priorite.getTotalValeursPriorites();

        creationViewModel();
        creationSwipeRefreshLayout();
        creationRecyclerView();
        btnAjouterEleve.setOnClickListener(ajouterEleveOnClickListener);
    }

    /**
     * Creation et logique du swipe refresh layout
     */
    private void creationSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            stageAdapter.filtrerListeStages(selectionPriorites);
            stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
            stageAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    /**
     * Creation et logique du view model pour communiquer avec le fragment de selection
     */
    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(SelectionViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            this.selectionPriorites = selection;
            stageAdapter.filtrerListeStages(selectionPriorites);
            stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
            stageAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.passer_sur_carte) {
            startGoogleMapActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Commence l'activitee de Google Maps
     */
    private void startGoogleMapActivity() {
        Intent intent = new Intent(this, GoogleMapsActivity.class);
        ArrayList<GoogleMapsObject> googleMapsObjects = new ArrayList<>();
        for (Stage stage : listeStages) {
            googleMapsObjects.add(stage.getGoogleMapsObject());
        }
        intent.putParcelableArrayListExtra("liste_des_stages", googleMapsObjects);
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
        stageAdapter = new ListeStageAdapter(this, listeStages);
        stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        stageAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(stageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

        stageAdapter.setOnItemClickListener(new ListeStageAdapter.OnItemClickListener() {
            @Override
            public void OnDrapeauClick(int position, ImageView DrapeauView) {
                changerPrioriteStage(position, DrapeauView);
                stageAdapter.notifyItemChanged(position);
            }

            @Override
            public void OnItemViewClick(View view, int position) {
                lancerActiviteAjoutStage(view, listeStages.get(position));
            }
        });
    }

    /**
     * Methode qui modifie la prioritee d'un stage
     *
     * @param positionStage la position du stage dans le recycler view
     * @param drapeauView   l'image view du drapeau
     */
    private void changerPrioriteStage(int positionStage, ImageView drapeauView) {
        Stage stage = listeStages.get(positionStage);
        int prioriteActuel = stage.getPriorite().ordinal();
        int prochainePriorite = prioriteActuel + 1;
        Priorite[] priorites = Priorite.values();
        prochainePriorite %= priorites.length;
        stage.setPriorite(priorites[prochainePriorite]);
        int couleur = Utils.renvoyerCouleur(stage.getPriorite());
        drapeauView.setColorFilter(ContextCompat.getColor(this.getApplicationContext(), couleur));
        stageAdapter.notifyItemChanged(positionStage);
        dbHelper.changerPrioriteStage(stage);
    }

    /**
     * Lance l'activitee d'ajout de stage
     *
     * @param view la vue qui demande de la creer
     */
    public void lancerActiviteAjoutStage(View view) {
        Intent intent = new Intent(this, InfoStageActivity.class);
        envoyerInfoStageActivity.launch(intent);
    }

    /**
     * Methode qui verifie si le stage est un stage existant et l'ajoute ou le modifie dans la liste de stages
     *
     * @param stage le stage qui a ete ajoute
     */
    private void mettreDansRV(Stage stage) {
        for (int i = 0; i < listeStages.size(); i++) {
            Stage lStage = listeStages.get(i);
            if (lStage.getId().equals(stage.getId())) {
                listeStages.set(i, stage);
                stageAdapter.filtrerListeStages(selectionPriorites);
                stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
                stageAdapter.notifyDataSetChanged();
                return;
            }
        }
        listeStages.add(stage);
        stageAdapter.filtrerListeStages(selectionPriorites);
        stageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        stageAdapter.notifyDataSetChanged();
    }

    /**
     * Lance l'activitee d'ajout de stage
     *
     * @param view  la vue qui demande de lancer l'activitee
     * @param stage le stage a modifier
     */
    public void lancerActiviteAjoutStage(View view, Stage stage) {
        Intent intent = new Intent(this, InfoStageActivity.class);
        intent.putExtra("stage", stage);
        envoyerInfoStageActivity.launch(intent);
    }

}

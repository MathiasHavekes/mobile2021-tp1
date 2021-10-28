package ca.qc.bdeb.c5gm.stageplanif;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
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

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAjouterEleve;
    private Stockage dbHelper;
    private ArrayList<Stage> listeStages;
    private RecyclerView recyclerView;
    private ListeStageAdapter StageAdapter;
    private Toolbar toolbar;
    private SelectionViewModel viewModel;
    private int selectionPriorites;

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
        selectionPriorites = Utils.renvoyerTotalValeursPriorite();

        creationViewModel();
        creationSwipeRefreshLayout();
        creationRecyclerView();
        btnAjouterEleve.setOnClickListener(ajouterEleveOnClickListener);
    }

    private void creationSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StageAdapter.filtrerListeStages(selectionPriorites);
                StageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
                StageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(SelectionViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            this.selectionPriorites = selection;
            StageAdapter.filtrerListeStages(selectionPriorites);
            StageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
            StageAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.passer_sur_carte) {
            startGoogleMapActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startGoogleMapActivity() {
        Intent intent = new Intent(this, GoogleMapsActivity.class);
        ArrayList<GoogleMapsObject> googleMapsObjects = new ArrayList<>();
        for (Stage stage: listeStages) {
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
        StageAdapter = new ListeStageAdapter(this, listeStages);
        StageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        StageAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(StageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

        StageAdapter.setOnItemClickListener(new ListeStageAdapter.OnItemClickListener() {
            @Override
            public void OnDrapeauClick(int position, ImageView DrapeauView) {
                changerPrioriteStage(position, DrapeauView);
                StageAdapter.notifyItemChanged(position);
            }

            @Override
            public void OnItemViewClick(View view, int position) {
                lancerActiviteAjoutStage(view, listeStages.get(position));
            }
        });
    }

    private final View.OnClickListener ajouterEleveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lancerActiviteAjoutStage(view);
        }
    };
  
    private void changerPrioriteStage(int positionStage, ImageView drapeauView) {
        Stage stage = listeStages.get(positionStage);
        int prioriteActuel = stage.getPriorite().ordinal();
        int prochainePriorite = prioriteActuel + 1;
        Priorite[] priorites = Priorite.values();
        prochainePriorite %= priorites.length;
        stage.setPriorite(priorites[prochainePriorite]);
        int couleur = Utils.renvoyerCouleur(stage.getPriorite());
        drapeauView.setColorFilter(ContextCompat.getColor(this.getApplicationContext(), couleur));
        StageAdapter.notifyItemChanged(positionStage);
        dbHelper.changerPrioriteStage(stage);
    }


    public void lancerActiviteAjoutStage(View view) {
        Intent intent = new Intent(this, InfoStageActivity.class);
        envoyerInfoStageActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> envoyerInfoStageActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Stage stage = intent.getParcelableExtra("stage");
                        mettreDansRV(stage);
                    }
                }
            });

    private void mettreDansRV(Stage stage) {
        for (int i = 0; i < listeStages.size(); i++) {
            Stage lStage = listeStages.get(i);
            if (lStage.getId().equals(stage.getId())) {
                listeStages.set(i, stage);
                StageAdapter.filtrerListeStages(selectionPriorites);
                StageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
                StageAdapter.notifyDataSetChanged();
                return;
            }
        }
        listeStages.add(stage);
        StageAdapter.filtrerListeStages(selectionPriorites);
        StageAdapter.trierListeStages(new StagePrioriteComparateur(), new StageNomComparateur(), new StagePrenomComparateur());
        StageAdapter.notifyDataSetChanged();
    }

    public void lancerActiviteAjoutStage(View view, Stage stage) {
        Intent intent = new Intent(this, InfoStageActivity.class);
        intent.putExtra("stage", stage);
        envoyerInfoStageActivity.launch(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int indexEnleve = viewHolder.getAdapterPosition();
                            dbHelper.deleteStage(listeStages.get(indexEnleve));
                            listeStages.remove(indexEnleve);
                            StageAdapter.notifyItemRemoved(indexEnleve);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.annuler_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int indexEnleve = viewHolder.getAdapterPosition();
                            StageAdapter.notifyItemChanged(indexEnleve);
                        }
                    });
            alertDialog.show();
            return;

        }
    };

}

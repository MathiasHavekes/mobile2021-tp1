package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ca.qc.bdeb.c5gm.stageplanif.data.Stage;
import ca.qc.bdeb.c5gm.stageplanif.data.StagePoidsPlume;
import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;
import ca.qc.bdeb.c5gm.stageplanif.data.Visite;

public class CalendrierActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    public static final int DUREE_VISITE_STANDARD = 45;
    public static final int DUREE_PAUSE_STANTARD = 45;
    public static final int HEURE_PREMIERE_VISITE = 480;
    private WeekView mWeekView;
    private Toolbar toolbar;
    private ArrayList<Visite> visites = new ArrayList<>();;
    private ArrayList<Stage> stages = new ArrayList<>();
    private ArrayList<WeekViewEvent> events = new ArrayList<>();
    private Stockage dbHelper;
    private int tempsSelectionne = DUREE_VISITE_STANDARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);
        dbHelper = Stockage.getInstance(this);
        stages = dbHelper.getStages(ConnectUtils.authId);

        ArrayList<Visite> donneesRecuperees = getIntent().getParcelableArrayListExtra("liste_visites");

        if (!(donneesRecuperees == null)) {
            visites = donneesRecuperees;
        }

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        initToolBar();
    }

    private void initToolBar() {
        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog = inflater.inflate(R.layout.dialog_description_visite, null);
        Visite visite = visites.get((int) event.getId());
        TextView textViewEtudiant = dialog.findViewById(R.id.text_etudiant_visite);
        TextView textViewEntreprise = dialog.findViewById(R.id.text_entreprise_visite);
        TextView textViewAdresse = dialog.findViewById(R.id.text_adresse_visite);
        TextView textViewCommentaire = dialog.findViewById(R.id.text_commentaire_visite);

        textViewEtudiant.setText(visite.getStage().getPrenomEtudiant() + " " + visite.getStage().getNomEtudiant());
        textViewEntreprise.setText(visite.getStage().getEntreprise().getNom());
        textViewAdresse.setText(visite.getStage().getEntreprise().getAdresse());
        textViewCommentaire.setText(visite.getStage().getCommentaire());

        builder.setView(dialog)
                .setTitle("Description visite")
                .setPositiveButton(R.string.btn_revenir, null)

                .show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog = inflater.inflate(R.layout.dialog_modifier_visite, null);
        Spinner spinnerEtudiant = dialog.findViewById(R.id.spinner_selection_etudiant);
        Spinner spinnerJournee = dialog.findViewById(R.id.spinner_selection_jour);
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_temps);
        TimePicker timePicker = dialog.findViewById(R.id.time_picker);

        Visite visiteSelectionnee = visites.get((int) event.getId());

        List<String> stagesSpinner = new ArrayList<>();
        stagesSpinner.add(visiteSelectionnee.getStage().getNomCompletEtudiant());
        setSpinner(spinnerEtudiant, stagesSpinner);
        spinnerEtudiant.setEnabled(false);

        List<String> journees = Utils.creeListeAvecValeursHashMap(Utils.JOURS_DE_LA_SEMAINE);
        setSpinner(spinnerJournee, journees);
        spinnerJournee.setSelection(journees.indexOf(Utils.JOURS_DE_LA_SEMAINE.get(event.getStartTime().getTime().getDay() + 1)));

        radioGroup.setOnCheckedChangeListener(radioGroupClique);
        setChampsDureeVisite(visiteSelectionnee, radioGroup);

        timePicker.setHour(event.getStartTime().getTime().getHours());
        timePicker.setMinute(event.getStartTime().getTime().getMinutes());
        builder.setView(dialog)
                .setTitle("Modifier visite")
                .setNegativeButton(R.string.btn_annuler, null)
                .setPositiveButton(R.string.btn_terminer, (dialog1, which) -> {
                    String jourSelectionne = (String) spinnerJournee.getSelectedItem();
                    int cleJourSelectionne = Utils.trouverCleAvecValeurHashMap(Utils.JOURS_DE_LA_SEMAINE, jourSelectionne);
                    visiteSelectionnee.setJournee(cleJourSelectionne);
                    visiteSelectionnee.setDuree(tempsSelectionne);
                    visiteSelectionnee.setHeureDeDebut(timePicker.getHour() * 60 + timePicker.getMinute());
                    mWeekView.notifyDatasetChanged();
                })
                .show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog = inflater.inflate(R.layout.dialog_modifier_visite, null);
        Spinner spinnerEtudiant = dialog.findViewById(R.id.spinner_selection_etudiant);
        Spinner spinnerJournee = dialog.findViewById(R.id.spinner_selection_jour);
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_temps);
        RadioButton radioButtonParDefaut = dialog.findViewById(R.id.duree_45_minutes);
        TimePicker timePicker = dialog.findViewById(R.id.time_picker);

        ArrayList<String> stageSpinner = new ArrayList<>();

        for (Stage s : stages) {
            stageSpinner.add(s.getEtudiant().getPrenom() + " " + s.getEtudiant().getNom());
        }

        setSpinner(spinnerEtudiant, stageSpinner);

        List<String> journees = Utils.creeListeAvecValeursHashMap(Utils.JOURS_DE_LA_SEMAINE);
        setSpinner(spinnerJournee, journees);
        spinnerJournee.setSelection(journees.indexOf(Utils.JOURS_DE_LA_SEMAINE.get(time.getTime().getDay() + 1)));

        radioGroup.setOnCheckedChangeListener(radioGroupClique);
        radioButtonParDefaut.setChecked(true);

        timePicker.setHour(time.getTime().getHours());
        timePicker.setMinute(time.getTime().getMinutes());
        builder.setView(dialog)
                .setTitle("Ajouter une visite")
                .setNegativeButton(R.string.btn_annuler, null)
                .setPositiveButton(R.string.btn_ajouter, (dialog1, which) -> {
                    String itemSelectionne = (String) spinnerEtudiant.getSelectedItem();
                    for (int i = 0; i < stageSpinner.size(); i++) {
                        if (stageSpinner.get(i) == itemSelectionne) {
                            String jourSelectionne = (String) spinnerJournee.getSelectedItem();
                            int cleJourSelectionne = Utils.trouverCleAvecValeurHashMap(Utils.JOURS_DE_LA_SEMAINE, jourSelectionne);
                            Stage stageSelectionne = stages.get(i);
                            Visite nouvelleVisite = new Visite(
                                    UUID.randomUUID().toString(),
                                    stageSelectionne.getStagePoidsPlume(),
                                    timePicker.getHour() * 60 + timePicker.getMinute(),
                                    tempsSelectionne,
                                    cleJourSelectionne
                                    );
                            visites.add(nouvelleVisite);
                            mWeekView.notifyDatasetChanged();
                        }
                    }
                })
                .show();
    }

    private final RadioGroup.OnCheckedChangeListener radioGroupClique = (radioGroup, i) -> {
        switch (i) {
            case R.id.duree_30_minutes:
                tempsSelectionne = 30;
                break;
            case R.id.duree_45_minutes:
                tempsSelectionne = 45;
                break;
            case R.id.duree_60_minutes:
                tempsSelectionne = 60;
                break;
        }
    };

    private void setChampsDureeVisite(Visite visite, RadioGroup radioGroup) {
        int dureeVisite = visite.getDuree();
        if (dureeVisite != 0) {
            switch (dureeVisite) {
                case 30:
                    radioGroup.check(R.id.duree_30_minutes);
                    break;
                case 45:
                    radioGroup.check(R.id.duree_45_minutes);
                    break;
                case 60:
                    radioGroup.check(R.id.duree_60_minutes);
                    break;
                default:
                    Log.d("Data", "setChampsDureeVisite() called with: stage = [" + visite + "] duree de visite different de 30, 45 ou 60");
            }
        }
    }

    private void setSpinner(Spinner spinner, List<String> listSpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listSpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public ArrayList<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        events = new ArrayList<>();

        if (!visites.isEmpty()) {
            for (Visite v : visites) {
                if (v.getDuree() <= 0) {
                    v.setDuree(DUREE_VISITE_STANDARD);
                }

                if (v.getHeureDeDebut() <= 0) {
                    v.setHeureDeDebut(HEURE_PREMIERE_VISITE);
                }

                WeekViewEvent event = getWeekViewEventFromVisite(v, newMonth, newYear);
                events.add(event);
            }
        }

        return events;
    }

    private WeekViewEvent getWeekViewEventFromVisite(Visite visite, int month, int year) {
        Calendar startTime = Calendar.getInstance();
        int[] heureDeDebut = separerHeuresEtMinutes(visite.getHeureDeDebut());

        startTime.set(Calendar.MINUTE, heureDeDebut[0]);
        startTime.set(Calendar.HOUR_OF_DAY, heureDeDebut[1]);
        startTime.set(Calendar.DAY_OF_WEEK, visite.getJournee());
        startTime.set(Calendar.MONTH, month - 1);
        startTime.set(Calendar.YEAR, year);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, visite.getDuree());
        endTime.set(Calendar.MONTH, month - 1);

        int indexVisite = visites.indexOf(visite);

        WeekViewEvent event = new WeekViewEvent(indexVisite, visite.getStage().getPrenomEtudiant() + " " + visite.getStage().getNomEtudiant(), startTime, endTime);
        event.setColor(ContextCompat.getColor(this, Utils.renvoyerCouleur(visite.getStage().getPriorite())));
        return event;
    }

    private int regrouperHeuresEtMinutes(int[] tableauHeuresEtMinutes) {
        return tableauHeuresEtMinutes[0] + tableauHeuresEtMinutes[1] * 60;
    }

    private int[] separerHeuresEtMinutes(int nombreTotalMinutes) {
        int nombreHeures = nombreTotalMinutes / 60;
        int nombreMinutes = nombreTotalMinutes % 60;
        return new int[] {nombreMinutes, nombreHeures};
    }
}
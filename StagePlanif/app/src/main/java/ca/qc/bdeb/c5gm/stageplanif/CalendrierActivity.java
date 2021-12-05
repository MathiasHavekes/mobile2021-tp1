package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;
import ca.qc.bdeb.c5gm.stageplanif.data.Visite;

public class CalendrierActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private static final int DUREE_VISITE_STANDARD = 45;
    private static final int DUREE_PAUSE_STANDARD = 45;
    private static final int HEURE_PREMIERE_VISITE = 480;
    private WeekView mWeekView;
    private Toolbar toolbar;
    private ArrayList<Visite> visites = new ArrayList<>();;
    private ArrayList<Stage> stages = new ArrayList<>();
    private ArrayList<WeekViewEvent> events = new ArrayList<>();
    private Stockage dbHelper;

    /**
     * Contient le FloatingActionButton d'ajout d'eleve
     */

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
        textViewEtudiant.setText(visite.getStage().getPrenomEtudiant() + " " + visite.getStage().getNomEtudiant());
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
        builder.setView(dialog)
                .setTitle("Modifier visite")
                .setNegativeButton(R.string.btn_annuler, null)
                .setPositiveButton(R.string.btn_terminer, (dialog1, which) -> {

                })
                .show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog = inflater.inflate(R.layout.dialog_modifier_visite, null);
        Spinner spinnerEtudiant = dialog.findViewById(R.id.spinner_selection_etudiant);
        TimePicker timePicker = dialog.findViewById(R.id.time_picker);

        List<String> stageSpinner = new ArrayList<>();

        for (Stage s : stages) {
            stageSpinner.add(s.getEtudiant().getPrenom() + " " + s.getEtudiant().getNom());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, stageSpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEtudiant.setAdapter(adapter);
        timePicker.setHour(time.getTime().getHours());
        timePicker.setMinute(time.getTime().getMinutes());
        builder.setView(dialog)
                .setTitle("Ajouter une visite")
                .setNegativeButton(R.string.btn_annuler, null)
                .setPositiveButton(R.string.btn_ajouter, (dialog1, which) -> {
                    String itemSelectionne = (String) spinnerEtudiant.getSelectedItem();
                    for (int i = 0; i < stageSpinner.size(); i++) {
                        if (stageSpinner.get(i) == itemSelectionne) {
                            Stage stageSelectionne = stages.get(i);
                            Visite nouvelleVisite = new Visite(
                                    UUID.randomUUID().toString(),
                                    stageSelectionne.getStagePoidsPlume(),
                                    timePicker.getHour() * 60 + timePicker.getMinute(),
                                    stageSelectionne.getDureeVisite(),
                                    time.getTime().getDay() + 1);
                            visites.add(nouvelleVisite);
                            mWeekView.notifyDatasetChanged();
                        }
                    }
                })
                .show();
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
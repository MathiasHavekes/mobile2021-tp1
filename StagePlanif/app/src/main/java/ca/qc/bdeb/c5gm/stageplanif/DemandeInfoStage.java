package ca.qc.bdeb.c5gm.stageplanif;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class DemandeInfoStage extends AppCompatActivity {
    private FragmentContainerView demandeInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_info_stage);

        demandeInfoFragment = findViewById(R.id.fragment_demande_info);
        Fragment fragment = DemandeInfoEleve.newInstance(demandeInfoFragment.getId());

        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(demandeInfoFragment.getId(), fragment);
        fragmentTransaction.commit();
    }
}
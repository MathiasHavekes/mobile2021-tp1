package ca.qc.bdeb.c5gm.stageplanif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adapte la liste de compte pour le recycler view
 */
public class ListeCompteAdapter extends RecyclerView.Adapter<ListeCompteAdapter.ListeCompteHolder> {
    /**
     * La liste des comptes à adapter pour le recycler view
     */
    private final ArrayList<Compte> listeCompte;
    /**
     * Un layout inflater
     */
    private final LayoutInflater inflater;
    /**
     * Les listener de cliques
     */
    private OnItemClickListener listener;

    private Context context;

    public ListeCompteAdapter(Context context, ArrayList<Compte> listeCompte) {
        inflater = LayoutInflater.from(context);
        this.listeCompte = listeCompte;
        this.context = context;
    }

    /**
     * Permet de setter les listeners
     *
     * @param listener Les déclarations de l'interface OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListeCompteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.student_list_item, parent, false);
        return new ListeCompteHolder(itemView, this, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListeCompteHolder holder, int position) {
        Compte compte = listeCompte.get(position);
        holder.nomEleveView.setText(compte.getNom());
        holder.prenomEleveView.setText(compte.getPrenom());
        holder.drapeauView.setColorFilter(ContextCompat.getColor(context, renvoyerCouleur(compte.getPriorite())));
    }

    private int renvoyerCouleur(Priorite valeurPriorite) {
        switch (valeurPriorite) {
            case MINIMUM:
                return R.color.green;
            case MOYENNE:
                return  R.color.yellow;
            case MAXIMUM:
                return R.color.red;
            default:
                return R.color.black;
        }
    }

    @Override
    public int getItemCount() {
        return listeCompte.size();
    }

    /**
     * Interface qui demande de creer des methodes pour definir le comportements selon les listeners
     */
    public interface OnItemClickListener {
        /**
         * Comportement lors du clique pour mettre la priorité d'un contact
         *
         * @param position     position dans le recyclerView de l'item
         * @param favoriteView l'item qui a ete clique
         */
        void OnDrapeauClick(int position, ImageView favoriteView);

        /**
         * Comportement lors d'un clique sur l'image de l'élève
         *
         * @param position position dans le recyclerView de l'item
         */
        void OnImageEleveClick(int position);
    }

    /**
     * Classe qui va afficher les views
     */
    public class ListeCompteHolder extends RecyclerView.ViewHolder {
        /**
         * TextView contenant le nom de l'élève
         */
        private final TextView nomEleveView;

        private final TextView prenomEleveView;
        /**
         * ImageView contenant le drapeau
         */
        private final ImageView drapeauView;
        /**
         * ImageView contenant l'image de l'élève
         */
        private final ImageView imageEleveView;
        /**
         * L'adapteur de la liste de compte
         */
        private final ListeCompteAdapter adapter;

        public ListeCompteHolder(@NonNull View itemView, ListeCompteAdapter adapter, final OnItemClickListener listener) {
            super(itemView);
            nomEleveView = itemView.findViewById(R.id.text_nom_eleve);
            prenomEleveView = itemView.findViewById(R.id.text_prenom_eleve);
            drapeauView = itemView.findViewById(R.id.image_drapeau);
            imageEleveView = itemView.findViewById(R.id.image_eleve);
            this.adapter = adapter;
            setListeners(listener);
        }

        private void setListeners(final OnItemClickListener listener) {
            drapeauView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnDrapeauClick(position, drapeauView);
                    }
                }
            });

            imageEleveView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnImageEleveClick(position);
                    }
                }
            });
        }

    }

}

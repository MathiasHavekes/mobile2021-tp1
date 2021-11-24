package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;
import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

public class Utils {
    public static ArrayList<Integer> calculerPrioritesSelectionnees(int selection) {
        ArrayList<Integer> ListePrioritesSelectionnees = new ArrayList<>();

        for (Priorite p : Priorite.values()) {
            if ((selection & p.getValeur()) > 0) {
                ListePrioritesSelectionnees.add(p.getValeur());
            }
        }
        return ListePrioritesSelectionnees;
    }

    public static ArrayList<Stage> filtrerListeStages(ArrayList<Integer> ListePrioritesSelectionnees, ArrayList<Stage> listeStages) {
        ArrayList<Stage> listeStagesMasques = new ArrayList<>();

        for (Stage s : listeStages) {
            if (!ListePrioritesSelectionnees.contains(s.getPriorite().getValeur())) {
                listeStagesMasques.add(s);
            }
        }
        return listeStagesMasques;
    }

    /**
     * Renvoie une couleur en fonction de la priorite passée en paramètre
     * Priorité minimum = vert, Priorité moyenne = jaune, Priorité maximum = Rouge
     * Sinon renvoie du noir
     * @param priorite
     * @return la couleur en format RGB (int)
     */
    public static int renvoyerCouleur(Priorite priorite) {
        switch (priorite) {
            case MINIMUM:
                return R.color.green;
            case MOYENNE:
                return R.color.yellow;
            case MAXIMUM:
                return R.color.red;
            default:
                return R.color.black;
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap getImageAjustee(byte[] photo, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        BitmapFactory.decodeByteArray(photo, 0, photo.length, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length, bmOptions);
        return bitmap;
    }

    /**
     * Methode qui determine l'annee scolaire en cours
     * @return l'annee scolaire en cours
     */
    public static String getAnneeScolaire() {
        Calendar calendrier = Calendar.getInstance();
        int anneeActuelle = calendrier.get(Calendar.YEAR);
        int moisActuel = calendrier.get(Calendar.MONTH);
        if (moisActuel < 7) {
            return String.format("%d-%d", anneeActuelle - 1, anneeActuelle);
        }
        return String.format("%d-%d", anneeActuelle, anneeActuelle + 1);
    }

}

package ca.qc.bdeb.c5gm.stageplanif;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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

    public static ArrayList<GoogleMapsObject> filtrerListeGoogleMapsObject(ArrayList<Integer> ListePrioritesSelectionnees, ArrayList<GoogleMapsObject> listeStages) {
        ArrayList<GoogleMapsObject> listeStagesMasques = new ArrayList<>();

        for (GoogleMapsObject s : listeStages) {
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

    /**
     * Renvoie une couleur en fonction de la priorite passée en paramètre
     * Priorité minimum = vert, Priorité moyenne = jaune, Priorité maximum = Rouge
     * Sinon renvoie du bleu
     * @param priorite
     * @return la couleur en format HSV (float)
     */
    public static float renvoyerCouleurHSV(Priorite priorite) {
        switch (priorite) {
            case MINIMUM:
                return BitmapDescriptorFactory.HUE_GREEN;
            case MOYENNE:
                return BitmapDescriptorFactory.HUE_YELLOW;
            case MAXIMUM:
                return BitmapDescriptorFactory.HUE_RED;
            default:
                return BitmapDescriptorFactory.HUE_BLUE;
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

}

package ca.qc.bdeb.c5gm.stageplanif;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel qui fait le lien entre la classe MainActivity et la classe SelectionPrioriteFragment
 */
public class SelectionViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedItem = new MutableLiveData<Integer>();

    public void selectItem(Integer selection) {
        selectedItem.setValue(selection);
    }

    public LiveData<Integer> getSelectedItem() {
        return selectedItem;
    }
}

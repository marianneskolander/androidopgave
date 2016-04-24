package org.projects.shoppinglist;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Here we create a new dialogbuilder;
        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setTitle("Bekræft sletning");
        alert.setMessage("Slet hele listen?");
        alert.setPositiveButton("Ja", pListener);
        alert.setNegativeButton("Nej", nListener);

        return alert.create();
    }

//--------------- yes button-----------------------------------------------------------
    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            positiveClick();
        }
    };

//-----------------------no button------------------------------------------------------
    DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            negativeClick();
        }
    };

    //These two methods are empty, because they will be overridden

    protected void positiveClick()
    {

    }
    protected void negativeClick()
    {

    }
}

package com.example.jdarcy.ordermydrinks;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jdarcy on 21/02/2016.
 * Class to handle processin dialogs from application
 * Displays Process dialog when tasks are processing
 */
public class ProgressDialogManager {

    ProgressDialog progessDialog;
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * */

    public void showProcessDialog(Context context, String title, String message)
    {

        progessDialog = new ProgressDialog(context);
        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        progessDialog.setTitle(title);

        // Setting Dialog Message
        progessDialog.setMessage(message);

        progessDialog.setIndeterminate(false);
        progessDialog.setCancelable(false);

        // Showing Progess essagera
        progessDialog.show();
    }

    public void dismissProcessDialog()
    {

        progessDialog.dismiss();
    }
}

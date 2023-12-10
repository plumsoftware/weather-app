package ru.plumsoftware.weatherapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import ru.plumsoftware.weatherapp.R;

public class ProgressDialog {
    private Dialog dialog;
    private final Context context;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void show() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}

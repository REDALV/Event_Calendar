package com.unovikau.eventcalendar.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.unovikau.eventcalendar.R;

public class TypePickerDialog extends DialogFragment {

    RadioGroup radioGroup;
    RadioButton sportButton;
    RadioButton cultButton;
    RadioButton educButton;

    int selectedType;

    private Dialog.OnClickListener listener;

    public void setListener(Dialog.OnClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedType(int type) {
        this.selectedType = type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.type_picker_dialog, null);
        radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
        sportButton = (RadioButton) dialog.findViewById(R.id.type_sport);
        cultButton = (RadioButton) dialog.findViewById(R.id.type_cult);
        educButton = (RadioButton) dialog.findViewById(R.id.type_educ);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        selectedType = x + 1;
                    }
                }
            }
        });


        builder.setView(dialog)
            // Add action buttons
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    listener.onClick(dialog, selectedType);
                }
            })
            .setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    selectedType = 0;
                    listener.onClick(dialog, selectedType);
                }
            });
        return builder.create();
    }
}

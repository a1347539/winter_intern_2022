package ivanWinterIntern.photoEditingPage;

import static com.thermalmore.intrinity.common.GlobalVariable.context;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.thermalmore.R;

import java.util.ArrayList;

import ivanWinterIntern.clientSelectorPage.Client;

public class ClientSelectionDialog extends DialogFragment {

    PhotoEditingActivity c;
    ArrayList<Client> clientArrayList;
    String selectedClient = "";

    private RadioGroup radioGroup;
    private Button saveButton;
    private Button cancelButton;

    public ClientSelectionDialog(PhotoEditingActivity c, ArrayList<Client> clientArrayList) {
        this.c = c;
        this.clientArrayList = clientArrayList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.client_selection_dialog, null);
        radioGroup = v.findViewById(R.id.radiogroup);
        saveButton = v.findViewById(R.id.save_button);
        cancelButton = v.findViewById(R.id.cancel_button);
        builder.setView(v);

        for (Client c : clientArrayList) {
            addRadioButton(c.getName());
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedClient == "") {
                    Toast.makeText(context, "Invalid Option", Toast.LENGTH_SHORT).show();
                } else {
                    c.saveImage(selectedClient);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void addRadioButton(String clientName) {
        RadioButton radioButton = new RadioButton(c);
        RadioGroup.LayoutParams childParam1 = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        childParam1.setMargins(0, 0, 0 , 30);
        radioButton.setLayoutParams(childParam1);
        radioButton.setText(clientName);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClient = clientName;
            }
        });

        radioGroup.addView(radioButton);
    }
}

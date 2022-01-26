package ivanWinterIntern.clientSelectorPage;

import static com.thermalmore.intrinity.common.GlobalVariable.context;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.thermalmore.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddClientDialog extends DialogFragment {

    private ClientSelectorActivity c;
    private EditText name_editText;
    private EditText phone_editText;
    private EditText addr_editText;
    private Button addButton;
    private Button cancelButton;

    public AddClientDialog(ClientSelectorActivity c) {
        this.c = c;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_client_dialog, null);
        name_editText = v.findViewById(R.id.name);
        phone_editText = v.findViewById(R.id.phone);
        addr_editText = v.findViewById(R.id.addr);
        addButton = v.findViewById(R.id.add_button);
        cancelButton = v.findViewById(R.id.cancel_button);
        builder.setView(v);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(name_editText) || isEmpty(name_editText) || isEmpty(name_editText)) {
                    Toast.makeText(context, "Empty Field Detected", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> client = new HashMap<>();
                    client.put("name", name_editText.getText().toString());
                    client.put("phone", phone_editText.getText().toString());
                    client.put("address", addr_editText.getText().toString());
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add("");
                    client.put("images", temp);
                    c.addItem(name_editText.getText().toString(), client);

                    Toast.makeText(context, "Client Added", Toast.LENGTH_SHORT).show();
                    dismiss();
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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}

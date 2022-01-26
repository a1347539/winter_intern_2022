package ivanWinterIntern.clientSelectorPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thermalmore.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class ClientSelectorActivity extends AppCompatActivity {

    Clients data;
    Map<String, Map<String, Object>> clientsInfo;
    ArrayList<Client> clientArrayList;
    String jsonFilePath;
    String jsonString;
    File clientInfoFile;
    RecyclerView cardContainer;
    ClientCardAdapter clientCardAdapter;
    TextView debugText1;
    TextView debugText2;
    TextView debugText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_selector);
        Bundle extras = getIntent().getExtras();
        jsonFilePath = extras.getString("jsonPath");
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateJsonMap();
        getSetViewElements();

        clientCardAdapter = new ClientCardAdapter(this, clientArrayList);
        cardContainer.setAdapter(clientCardAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateJsonMap();
        clientCardAdapter.updateList(clientArrayList);
    }

    public void addItem(String id, Map<String, Object> clientData) {
        clientsInfo.put(id, clientData);
        Clients data = new Clients(clientInfoFile, clientsInfo);
        data.saveToFile();
        updateJsonMap();
        clientCardAdapter.updateList(clientArrayList);
    }

    public void removeItem(int position) {
        String name = clientArrayList.get(position).getName();
        clientsInfo.remove(name);
        Clients data = new Clients(clientInfoFile, clientsInfo);
        data.saveToFile();
        updateJsonMap();
        clientCardAdapter.updateList(clientArrayList);
    }

    private void updateJsonMap() {
        clientInfoFile = new File(jsonFilePath);
        jsonString = Clients.loadFromFile(jsonFilePath);
        Gson gson = new Gson();
        data = gson.fromJson(jsonString, Clients.class);
        clientsInfo = data.getMap();
        clientArrayList = data.getClientsArray();
    }

    private void getSetViewElements() {
        debugText1 = (TextView) findViewById(R.id.debugText1);
        debugText2 = (TextView) findViewById(R.id.debugText2);
        debugText3 = (TextView) findViewById(R.id.debugText3);
        debugText1.setVisibility(View.GONE);
        debugText2.setVisibility(View.GONE);
        debugText3.setVisibility(View.GONE);
        cardContainer = findViewById(R.id.cardContainer);
        cardContainer.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_selector_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_client:
                AddClientDialog addClientDialog = new AddClientDialog(this);
                addClientDialog.show(getSupportFragmentManager(), "AddClientDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
package ivanWinterIntern.clientGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thermalmore.R;
import com.thermalmore.intrinity.common.GlobalVariable;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import ivanWinterIntern.clientSelectorPage.Client;
import ivanWinterIntern.clientSelectorPage.Clients;

public class ClientGalleryActivity extends AppCompatActivity {

    TextView debugText1;
    TextView debugText2;
    TextView debugText3;

    ClientGalleryAdapter clientGalleryAdapter;
    RecyclerView imageContainerRecyclerView;
    RecyclerView.LayoutManager imageContainerLayoutManager;
    ImageView backButton;
    ImageView optionButton;
    TextView clientNameText;
    int imagePerRow = 3;

    File clientInfoFile;
    Map<String, Map<String, Object>> clientsInfo;
    private Client client;
    int clientIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_gallery);
        Bundle extras = getIntent().getExtras();
        clientIndex = extras.getInt("index");
        client = (Client) getIntent().getSerializableExtra("client");
        getSetViewElements();
        clientNameText.setText(client.getName());
        ArrayList<String> imgPaths = new ArrayList<>(client.getImgPaths());
        imgPaths.remove(0);

        clientInfoFile = new File(GlobalVariable.clientInfoFilePath);
        Gson gson = new Gson();
        Clients data = gson.fromJson(Clients.loadFromFile(GlobalVariable.clientInfoFilePath), Clients.class);
        clientsInfo = data.getMap();

        debugText2.setText(client.getImgPaths().toString());
        debugText1.setText(imgPaths.toString());

        clientGalleryAdapter =
                new ClientGalleryAdapter(this, imgPaths, imagePerRow);
        imageContainerRecyclerView.setAdapter(clientGalleryAdapter);
    }

    private void getSetViewElements() {
        debugText1 = (TextView) findViewById(R.id.debugText1);
        debugText2 = (TextView) findViewById(R.id.debugText2);
        debugText3 = (TextView) findViewById(R.id.debugText3);

        debugText1.setVisibility(View.GONE);
        debugText2.setVisibility(View.GONE);
        debugText3.setVisibility(View.GONE);

        imageContainerRecyclerView = (RecyclerView) findViewById(R.id.imageContainer);
        imageContainerLayoutManager = new GridLayoutManager(this, imagePerRow);
        imageContainerRecyclerView.setLayoutManager(imageContainerLayoutManager);
        backButton = (ImageView) findViewById(R.id.backButton);
        optionButton = (ImageView) findViewById(R.id.optionButton);
        clientNameText = (TextView) findViewById(R.id.clientNameText);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void removeImage(int position) {
        (( ArrayList<String> )clientsInfo.get(client.getName()).get("images")).remove(position+1);
        Clients data = new Clients(clientInfoFile, clientsInfo);
        data.saveToFile();
        updateJsonMap();
    }

    private void updateJsonMap() {
        clientInfoFile = new File(GlobalVariable.clientInfoFilePath);
        String jsonString = Clients.loadFromFile(GlobalVariable.clientInfoFilePath);
        Gson gson = new Gson();
        Clients data = gson.fromJson(jsonString, Clients.class);
        clientsInfo = data.getMap();
        ArrayList<Client> clientArrayList = data.getClientsArray();
        client = clientArrayList.get(clientIndex);
        ArrayList<String> imgPaths = new ArrayList<>(client.getImgPaths());
        imgPaths.remove(0);
        clientGalleryAdapter.updateList(imgPaths);

        debugText2.setText(client.getImgPaths().toString());

        debugText1.setText(imgPaths.toString());
    }
}
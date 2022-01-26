package ivanWinterIntern.clientSelectorPage;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;

public class Clients {
    private Map<String, Map<String, Object>> map;
    private File file;

    public Clients(File file) {
        this.file = file;
    }

    public Clients(File file, Map<String, Map<String, Object>> map) {
        this.file = file;
        this.map = map;
    }

    public Map<String, Map<String, Object>> getMap() {
        return map;
    }

    public ArrayList<Client> getClientsArray() {
        ArrayList<Client> clients = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> client : map.entrySet()) {
            if (!(client.getValue().get("address").toString().trim().length() == 0)) {
                Client c = new Client();
                c.setName(client.getValue().get("name").toString());
                c.setPhone(client.getValue().get("phone").toString());
                c.setAddr(client.getValue().get("address").toString());
                ArrayList<String> temp = (ArrayList<String>) client.getValue().get("images");
                c.setImgPaths(temp);
                clients.add(c);
            }
        }
        return clients;
    }

    public void setMap(Map<String, Map<String, Object>> map) {
        this.map = map;
    }

    public void saveToFile() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(json);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        Gson gson = new Gson();
//        data = gson.fromJson(json, Clients.class);
    }
}

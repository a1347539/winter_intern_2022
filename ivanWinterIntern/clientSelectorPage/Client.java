package ivanWinterIntern.clientSelectorPage;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {

    private String name;
    private String phone;
    private String addr;
    private int imgCount;
    private ArrayList<String> imgPaths;

    public Client() {
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddr() { return phone; }
    public int getImgCount() { return imgCount; }
    public ArrayList<String> getImgPaths() { return imgPaths; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddr(String addr) { this.addr = addr; }
    public void setImgPaths(ArrayList<String> imgPaths) {
        this.imgCount = imgPaths.size();
        this.imgPaths = imgPaths;
    }

}

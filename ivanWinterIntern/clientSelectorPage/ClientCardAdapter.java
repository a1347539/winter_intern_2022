package ivanWinterIntern.clientSelectorPage;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thermalmore.R;

import java.util.ArrayList;

import ivanWinterIntern.clientGallery.ClientGalleryActivity;

public class ClientCardAdapter extends RecyclerView.Adapter<CardContainerViewHolder> {

    ClientSelectorActivity c;
    ArrayList<Client> clients;

    public ClientCardAdapter(ClientSelectorActivity c, ArrayList<Client> clients) {
        this.c = c;
        this.clients = clients;
    }

    @NonNull
    @Override
    public CardContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_card, null);
        return new CardContainerViewHolder(view, c, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerViewHolder holder, int position) {
        holder.name.setText(clients.get(position).getName());
        holder.phone.setText(clients.get(position).getPhone());
        Integer tempImgCount = clients.get(position).getImgCount()-1;
        if (tempImgCount > 0) {
            holder.imgCount.setText("Photos: " + tempImgCount.toString());
        }
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public void updateList(ArrayList<Client> newlist) {
        clients = newlist;
        this.notifyDataSetChanged();
    }

    public void toClientGallery(int position) {
        Intent intent = new Intent(c, ClientGalleryActivity.class);
        intent.putExtra("client", clients.get(position));
        intent.putExtra("index", position);
        c.startActivity(intent);
    }

    public void removeItem(int position) {
        c.removeItem(position);
    }
}

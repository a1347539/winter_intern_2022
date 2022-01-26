package ivanWinterIntern.clientSelectorPage;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thermalmore.R;

public class CardContainerViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    private Context c;
    ClientCardAdapter cca;
    TextView name;
    TextView phone;
    TextView imgCount;

    public CardContainerViewHolder(@NonNull View itemView, Context c, ClientCardAdapter cca) {
        super(itemView);
        this.c = c;
        this.cca = cca;
        this.name = itemView.findViewById(R.id.clientName);
        this.phone = itemView.findViewById(R.id.clientPhone);
        this.imgCount = itemView.findViewById(R.id.imgCount);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cca.toClientGallery(getAdapterPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    setCardActionMenu(v);
                }
                return false;
            }
        });
    }

    public void setCardActionMenu(View v) {
        PopupMenu pm = new PopupMenu(c, v);
        pm.setOnMenuItemClickListener(this);
        pm.inflate(R.menu.client_card_action_menu);
        pm.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_card:
                cca.removeItem(getAdapterPosition());
                break;
            default:
                break;
        }
        return false;
    }
}

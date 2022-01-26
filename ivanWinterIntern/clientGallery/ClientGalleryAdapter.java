package ivanWinterIntern.clientGallery;

import static com.thermalmore.intrinity.common.GlobalVariable.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thermalmore.R;

import java.io.File;
import java.util.ArrayList;

import ivanWinterIntern.clientSelectorPage.Client;

public class ClientGalleryAdapter extends RecyclerView.Adapter<ClientGalleryAdapter.ImageViewHolder> {

    ClientGalleryActivity c;
    ArrayList<String> imgPaths;
    String fileName;
    int imgPerRow;

    public ClientGalleryAdapter(ClientGalleryActivity c, ArrayList<String> imgPaths, int imgPerRow) {
        this.c = c;
        this.imgPaths = imgPaths;
        this.imgPerRow = imgPerRow;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_image_card, null);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imgPath = imgPaths.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        ImageView imageView = holder.imageView;
        holder.filePath = imgPath;
        holder.imageView.getLayoutParams().height = bitmap.getHeight()/imgPerRow;
        holder.imageView.requestLayout();
        holder.fileName = (new File(imgPath)).getName();

        Glide.with(c).load(bitmap).into(imageView);
    }

    @Override
    public int getItemCount() {
        return imgPaths.size();
    }

    public void updateList(ArrayList<String> imgPaths) {
        this.imgPaths = imgPaths;
        this.notifyDataSetChanged();
    }

    // -------------------------------------------------------------------

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView imageView;
        String filePath;
        String fileName;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(c, ClientImageFullScreenActivity.class);
                        intent.putExtra("imgPaths", imgPaths);
                        intent.putExtra("index", position);
                        c.startActivity(intent);
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
            pm.inflate(R.menu.client_image_card_action_menu);
            pm.show();
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.detail:
                    String[] temp = fileName.split("_");
                    activity.showToast("Date: " + temp[0].substring(0, 4) + "/" +
                            temp[0].substring(4, 6) + "/" + temp[0].substring(6, 8) +
                            ", Time: " + temp[1].substring(0, 2) + ":" + temp[1].substring(2, 4) +
                            ":" + temp[1].substring(4, 6));
                    break;
                case R.id.remove_card:
                    c.removeImage(getAdapterPosition());
                    activity.showToast("Image removed");
                    break;
                default:
                    break;
            }
            return false;
        }
    }

}

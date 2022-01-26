package ivanWinterIntern.clientGallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thermalmore.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class FullScreenImageAdapter extends PagerAdapter {

    Context c;
    ArrayList<String> filePaths;
    LayoutInflater inflater;

    public FullScreenImageAdapter(Context c, ArrayList<String> filePaths) {
        this.c = c;
        this.filePaths = filePaths;
    }

    @Override
    public int getCount() {
        return filePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.client_fullscreen_image, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.client_fullscreen_image);
        Glide.with(c).load(filePaths.get(position)).apply(new RequestOptions().centerInside()).into(imageView);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View v = (View) object;
        viewPager.removeView(v);
    }
}

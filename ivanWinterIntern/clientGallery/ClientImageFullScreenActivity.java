package ivanWinterIntern.clientGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thermalmore.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ClientImageFullScreenActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView optionButton;
    private TextView dateText;
    private TextView timeText;
    private ViewPager imageContainerViewPager;

    ArrayList<String> filePaths;
    int fileIndex;

    Calendar cal;
    SimpleDateFormat monthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_image_full_screen);
        Bundle extras = getIntent().getExtras();
        filePaths = extras.getStringArrayList("imgPaths");
        fileIndex = extras.getInt("index");
        cal = Calendar.getInstance();
        monthDate = new SimpleDateFormat("MMMM");
        getSetViewElement();

        FullScreenImageAdapter fullScreenImageAdapter = new FullScreenImageAdapter(this, filePaths);
        imageContainerViewPager.setAdapter(fullScreenImageAdapter);
        imageContainerViewPager.setCurrentItem(fileIndex, true);
        imageContainerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                String fileName = (new File(filePaths.get(position))).getName();
                String[] temp = fileName.split("_");
                int monthNum = Integer.parseInt(temp[0].substring(4, 6));
                cal.set(Calendar.MONTH,monthNum);
                String month_name = monthDate.format(cal.getTime());
                dateText.setText(month_name + " " + temp[0].substring(6, 8) + ", " + temp[0].substring(0, 4));
                timeText.setText(temp[1].substring(0, 2) + ":" + temp[1].substring(2, 4) +
                        ":" + temp[1].substring(4, 6));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getSetViewElement() {
        backButton = (ImageView) findViewById(R.id.backButton);
        optionButton = (ImageView) findViewById(R.id.optionButton);
        dateText = (TextView) findViewById(R.id.dateText);
        timeText = (TextView) findViewById(R.id.timeText);
        imageContainerViewPager = (ViewPager) findViewById(R.id.imageContainerView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
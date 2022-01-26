package ivanWinterIntern.photoEditingPage;

import static com.thermalmore.intrinity.common.Database.db_load_thermpgraphy;
import static com.thermalmore.intrinity.common.GlobalVariable.activity;
import static com.thermalmore.intrinity.common.GlobalVariable.context;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.thermalmore.R;
import com.thermalmore.intrinity.common.Database;
import com.thermalmore.intrinity.common.GlobalVariable;
import com.thermalmore.intrinity.common.GlobalVariable.editModes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import ivanWinterIntern.clientSelectorPage.AddClientDialog;
import ivanWinterIntern.clientSelectorPage.Client;
import ivanWinterIntern.clientSelectorPage.Clients;

public class PhotoEditingActivity extends AppCompatActivity {
    private final int GRAY_OUT = Color.rgb(148, 148, 148);

    ArrayList<Client> clientArrayList;
    File clientInfoFile;
    Map<String, Map<String, Object>> clientsInfo;

    private String date;
    private String time;
    private myCanvas canvas;
    private Bitmap mainBitmap;
    private Bitmap scaled;

    private File ReceivedImgFile;
    private String imgPath;
    private ArrayList<Double> temperatureArray;

    public int statusBarHeight;

    // topbar
    private ImageView backButton;
    private ImageView saveButton;
    private TextView dateText;
    private TextView timeText;
    // content
    private float imageViewTopAt;
    private boolean isTyping = false;

    // bottombar
    private ImageView drawRectButton;
    private ImageView eraserButton;
    private ImageView drawButton;
    private ImageView writeButton;
    private ArrayList<EditText> userInputTexts = new ArrayList<>();

    private ArrayList<TextView> textForVariables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get extra
        Bundle extras = getIntent().getExtras();
        imgPath = extras.getString("imgPath");
        ReceivedImgFile = new File(imgPath);
        mainBitmap = BitmapFactory.decodeFile(ReceivedImgFile.getAbsolutePath());
        clientInfoFile = new File(GlobalVariable.clientInfoFilePath);
        String jsonString = Clients.loadFromFile(GlobalVariable.clientInfoFilePath);
        Gson gson = new Gson();
        Clients data = gson.fromJson(jsonString, Clients.class);
        clientsInfo = data.getMap();
        clientArrayList = data.getClientsArray();

        Database.RecordData thermalData = getThermalData();
        if (thermalData != null) {
            temperatureArray = new ArrayList<Double>(thermalData.tempData);
//            Log.i("mdb", "onCreate: " + mainBitmap.getWidth() + " " + mainBitmap.getHeight()
//                    + " " + temperatureArray.size());
        }
        // get date and time
        getDemography();
        // get and set layout element
        setContentView(R.layout.activity_photo_editing);

        float scaleByWidth = (float)GlobalVariable.getScreenWidth()/(float)mainBitmap.getWidth();
        scaled = Bitmap.createScaledBitmap(mainBitmap,
                Math.round(mainBitmap.getWidth() * scaleByWidth),
                Math.round(mainBitmap.getHeight() * scaleByWidth),
                true);

        getSetLayout();
        setImageContent();
        setElementListener();
        statusBarHeight = getStatusBarHeight();
    }

    private void getDemography() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        time = sf.format(c);
        date = df.format(c);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void getSetLayout() {
        // debug
        textForVariables.add((TextView) findViewById(R.id.textForVariable1));
        textForVariables.add((TextView) findViewById(R.id.textForVariable2));
        textForVariables.add((TextView) findViewById(R.id.textForVariable3));
        textForVariables.add((TextView) findViewById(R.id.textForVariable4));
        textForVariables.add((TextView) findViewById(R.id.textForVariable5));
        textForVariables.add((TextView) findViewById(R.id.textForVariable6));

        // topbar
        backButton = (ImageView) findViewById(R.id.back_button);
        saveButton = (ImageView) findViewById(R.id.save_button);
        dateText = (TextView) findViewById(R.id.date_text);
        timeText = (TextView) findViewById(R.id.time_text);
        // content
        canvas = new myCanvas(this, scaled, this, temperatureArray, textForVariables);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.topbar);
        params.addRule(RelativeLayout.ABOVE, R.id.bottombar);
        canvas.setLayoutParams(params);
        ((RelativeLayout) findViewById(R.id.root)).addView(canvas);
        // bottombar
        drawRectButton = (ImageView) findViewById(R.id.draw_rect_button);
        eraserButton = (ImageView) findViewById(R.id.eraser_button);
        eraserButton.setColorFilter(Color.WHITE);
        drawButton = (ImageView) findViewById(R.id.draw_button);
        writeButton = (ImageView) findViewById(R.id.write_button);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewTopAt = canvas.getY();
            }
        }, 100);
    }

    private void setImageContent() {
        ReceivedImgFile.delete();
        dateText.setText(date);
        timeText.setText(time);
    }

    private void setElementListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientSelectionDialog clientSelectionDialog = new ClientSelectionDialog(PhotoEditingActivity.this, clientArrayList);
                clientSelectionDialog.show(getSupportFragmentManager(), "ClientSelectionDialog");
            }
        });

        // bottombar

        drawRectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColorTint();
                drawRectButton.setColorFilter(GRAY_OUT);
                GlobalVariable.editMode = editModes.drawRect;
            }
        });

        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColorTint();
                eraserButton.setColorFilter(GRAY_OUT);
                GlobalVariable.editMode = editModes.erase;
                canvas.onEraseEnter();
            }
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColorTint();
                drawButton.setColorFilter(GRAY_OUT);
                GlobalVariable.editMode = editModes.draw;
                canvas.onDrawEnter();
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColorTint();
                writeButton.setColorFilter(GRAY_OUT);
                GlobalVariable.editMode = editModes.write;
            }
        });

        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(GlobalVariable.editMode) {
                    case drawRect:
                        isTyping = false;
                        return canvas.drawRect(event);
                    case erase:
                        isTyping = false;
                        return canvas.draw(event, canvas.eraserPath);
                    case draw:
                        isTyping = false;

                        return canvas.draw(event, canvas.drawPath);
                    case write:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (!isTyping) {
                                    if (GlobalVariable.editMode == editModes.write) {
                                        createTextView((int) event.getRawX(), (int) event.getRawY());
                                    }
                                } else {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    for (int i = 0; i < userInputTexts.size(); i++) {
                                        imm.hideSoftInputFromWindow(userInputTexts.get(i).getWindowToken(), 0);
                                        if (TextUtils.isEmpty(userInputTexts.get(i).getText())) {
                                            userInputTexts.get(i).setVisibility(View.GONE);
                                        } else {
                                            userInputTexts.get(i).setCursorVisible(false);
                                            userInputTexts.get(i).clearFocus();
                                        }
                                    }
                                    isTyping = false;
                                }
                                return false;
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    public void saveImage(String clientName) {
        updateClientInfo(clientName);
        File f = new File(imgPath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            for (EditText editText : userInputTexts) {
                editText.setCursorVisible(false);
                canvas.drawTextEditors(editText, imageViewTopAt);
            }
            // canvas: canvas to be converted to bitmap
            // bitmap: size of bitmap to get from, takes scaled bitmap
            // return a scaled bitmap, e.g. bitmap with screen size
            // 'temp' is the canvas in myCanvas
            Bitmap temp = viewToBitmap(canvas, scaled);
            canvas.writeCanvas.drawBitmap(temp, 0, 0, null);
////////////////////// 'temp' is identical to 'canvas' in myCanvas

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(temp,
                    Math.round(mainBitmap.getWidth()),
                    Math.round(mainBitmap.getHeight()),
                    true);
            temp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        activity.showToast("new photo saved");

        finish();
    }

    private void updateClientInfo(String clientName) {
        (( ArrayList<String> ) clientsInfo.get(clientName).get("images")).add(imgPath);
        Clients data = new Clients(clientInfoFile, clientsInfo);
        data.saveToFile();
    }

    public Bitmap viewToBitmap(View view, Bitmap image) {
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void resetColorTint() {
        switch(GlobalVariable.editMode) {
            case drawRect:
                drawRectButton.setColorFilter(Color.WHITE);
                break;
            case erase:
                eraserButton.setColorFilter(Color.WHITE);
                break;
            case draw:
                drawButton.setColorFilter(Color.WHITE);
                break;
            case write:
                writeButton.setColorFilter(Color.WHITE);
                break;
            default:
                break;
        }
    }

    private Database.RecordData getThermalData() {
        String filenameClean = ReceivedImgFile.getName().replace(".png", "");
        // String filenameClean = "20220119_113307";
        try{
            return db_load_thermpgraphy(filenameClean);
        }
        catch (Exception e) {
            Log.i("mdb", "get database data error: "+e.toString());
            return null;
        }
    }

    private void createTextView(int x, int y) {
        EditText userInputText = new EditText(this);

        userInputText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                float x = e.getRawX();
                float y = e.getRawY();
                if (GlobalVariable.editMode != GlobalVariable.editModes.write) {
                    return false;
                }
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isTyping) {
                            v.setX(e.getRawX() - (v.getWidth() / 2));
                            v.setY(e.getRawY() - (v.getHeight()));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isTyping = true;
                        ((EditText) v).setCursorVisible(true);
                        break;
                }
                return false;
            }
        });

        userInputText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        userInputText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);

        userInputText.setSingleLine(false);
        userInputText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        userInputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        userInputText.setMaxLines(7);
        userInputText.setGravity(Gravity.TOP | Gravity.LEFT);

        params.width = this.getResources().getDisplayMetrics().widthPixels*3/5;
        userInputText.setBackground(ContextCompat.getDrawable(this, R.drawable.frame));
        userInputText.setX(x);
        userInputText.setY(y-statusBarHeight);
        userInputText.setLayoutParams(params);
        userInputTexts.add(userInputText);

        ((RelativeLayout) findViewById(R.id.root)).addView(userInputText);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isTyping) {
                isTyping = false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
package ivanWinterIntern.photoEditingPage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thermalmore.intrinity.common.GlobalVariable;

import java.util.ArrayList;

public class myCanvas extends View {
    private Activity myActivity;
    private ArrayList<TextView> tvs;
    private MyImgProcessor myImgProcessor;

    private Bitmap image;
    float widthHeightRatio;
    private int temperatureSpaceWidth;
    private int temperatureSpaceHeight;
    private float temperatureSpaceToImageRatio;
    ArrayList<double[]> criticalPtsStartAt;
    public float scaleByWidth;
    public Bitmap drawableBitmap;
    public Canvas drawCanvas;
    public Canvas writeCanvas;

    private Paint drawRectPaint;
    private Paint eraserPaint;
    private Paint drawPaint;
    private Paint writePaint;

    public Path eraserPath = new Path();
    public Path drawPath = new Path();
    PointF rect_s_pos = new PointF(), rect_e_pos = new PointF();
    private boolean drawRectangle = false;
    private boolean initRectDrawable = false;

    public myCanvas(Context context, Bitmap bp, Activity myActivity, ArrayList<Double> temperatureArray, ArrayList<TextView> tvs) {
        super(context);
        setDrawRectPaint();
        setEraserPaint();
        setDrawPaint();
        setWritePaint();

        this.tvs = tvs;

        this.image = bp;
        this.myActivity = myActivity;

        if (temperatureArray != null) {
            widthHeightRatio = image.getHeight()/(float)image.getWidth();
            temperatureSpaceWidth = (int) Math.round(Math.sqrt(temperatureArray.size()/widthHeightRatio));
            temperatureSpaceHeight = Math.round(temperatureSpaceWidth * widthHeightRatio);
            temperatureSpaceToImageRatio = image.getWidth()/(float)temperatureSpaceWidth;
            myImgProcessor = new MyImgProcessor(temperatureArray, temperatureSpaceWidth, temperatureSpaceHeight, temperatureSpaceToImageRatio);
            if (myImgProcessor.max[1] - myImgProcessor.min[1] > 2) {
                double[][] colorDifferenceMatrix = myImgProcessor.getColorDifference(5);
                criticalPtsStartAt = myImgProcessor.getCriticalAreaStartAt(colorDifferenceMatrix);
                initRectDrawable = true;
            }
        }
    }

    private void setDrawRectPaint() {
        drawRectPaint = new Paint();
        drawRectPaint.setStyle(Paint.Style.STROKE);
        drawRectPaint.setColor(Color.RED);
        drawRectPaint.setStrokeWidth(5f);
    }

    private void setEraserPaint() {
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(true);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeWidth(100f);
    }

    private void setDrawPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.RED);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeWidth(5f);
    }

    private void setWritePaint() {
        writePaint = new Paint();
        writePaint.setTextSize(30);;
        writePaint.setColor(Color.WHITE);
    }

    public void onEraseEnter() {
        eraserPath = new Path();
    }

    public void onDrawEnter() {
        drawPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawableBitmap == null) {
            scaleByWidth = (float)this.getWidth()/(float)image.getWidth();
            Bitmap temp = Bitmap.createScaledBitmap(image,
                    Math.round(image.getWidth() * scaleByWidth),
                    Math.round(image.getHeight() * scaleByWidth),
                    true);
            image = temp;
            drawableBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

            drawCanvas = new Canvas(drawableBitmap);
            writeCanvas = new Canvas(drawableBitmap);
            if (initRectDrawable) {
                drawInitialRect();
            }
        }

        // draw image on canvas
        canvas.drawBitmap(image, 0, 0, null);

        // draw user inputs on canvas

        // draw rects
        if (drawRectangle) {
            canvas.drawRect(rect_s_pos.x, rect_s_pos.y, rect_e_pos.x, rect_e_pos.y, drawRectPaint);
        }

        // draw
        if (GlobalVariable.editMode == GlobalVariable.editModes.erase) {
            drawCanvas.drawPath(eraserPath, eraserPaint);
        } else if (GlobalVariable.editMode == GlobalVariable.editModes.draw) {
            drawCanvas.drawPath(drawPath, drawPaint);
        }


        canvas.drawBitmap(drawableBitmap, 0, 0, null);

        super.onDraw(canvas);
    }

    private void drawInitialRect() {
        for (int i = 0; i < criticalPtsStartAt.size(); i++) {
            // PointF t = myImgProcessor.getCriticalAreaCenter((int)Math.round(criticalPtsStartAt.get(i)[1]), (int)Math.round(criticalPtsStartAt.get(i)[0]));
            PointF temp = myImgProcessor.matrixSpaceToImageSpace((int)Math.round(criticalPtsStartAt.get(i)[1]), (int)Math.round(criticalPtsStartAt.get(i)[0]));
            drawCanvas.drawCircle(temp.x, temp.y, 50, drawRectPaint);
        }

        invalidate();
    }

    public boolean drawRect(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawRectangle = true;
                rect_s_pos.x = event.getX();
                rect_s_pos.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                rect_e_pos.x = event.getX();
                rect_e_pos.y = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawRectangle = false;
                drawCanvas.drawRect(rect_s_pos.x, rect_s_pos.y, rect_e_pos.x, rect_e_pos.y, drawRectPaint);
                invalidate();
                break;
        }
        return true;
    }

    public boolean draw(MotionEvent event, Path path) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                return true;
            default:
                super.onTouchEvent(event);
                return false;
        }
    }

    public void drawTextEditors(EditText editText, float height_offset) {
        String t = editText.getText().toString();
        float start_x = editText.getX();
        float start_y = editText.getY()+editText.getBaseline()-height_offset;
        for (String line : t.split("\n")) {
            writeCanvas.drawText(line, start_x, start_y, writePaint);
            start_y += writePaint.descent() - writePaint.ascent();
        }
    }
}

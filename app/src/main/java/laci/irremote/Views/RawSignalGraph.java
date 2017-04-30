package laci.irremote.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by laci on 28.4.2017.
 */

public class RawSignalGraph extends View {


    ArrayList<Short> Data;
    int width = 0, height = 0;
    int heightMargin = 15; //height margin in %
    double l;

    public RawSignalGraph(Context context, ArrayList<Short> data) {
        super(context);
        Data = data;
        l = data.size();
    }

    public void UpdateSignal(ArrayList<Short> data){
        Data = data;
        l = data.size();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(Data == null) return;

        width = canvas.getWidth();
        height = canvas.getHeight();
        float topThreshold = (float) (height - (heightMargin/100.0*height));
        float bottomThreshold = (float) (heightMargin/100.0*height);
        double current_x = 0;

        Paint paint = new Paint();
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);

        double x_step = width/(l/1);
        path.moveTo((float) current_x, height/2);
        for (int i = 0; i < l; i+=1) {
            current_x += x_step;
            double y = ((topThreshold -((topThreshold+bottomThreshold)/2))/Short.MAX_VALUE)*Data.get(i) + ((topThreshold+bottomThreshold)/2);
            path.lineTo((float) current_x, (float) y);

        }

        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }
}

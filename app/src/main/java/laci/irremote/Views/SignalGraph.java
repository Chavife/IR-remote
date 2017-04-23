package laci.irremote.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

/**
 * Created by laci on 10.4.2017.
 */

public class SignalGraph extends View {

    Integer[] Signal;
    int signalLength = 0;
    int width = 0, height = 0;
    int heightMargin = 15; //height margin in %


    public SignalGraph(Context context, Integer[] signal) {
        super(context);
        Signal = signal;
        for(int l : signal){
            signalLength += Math.abs(l); //100%
        }
    }

    public void UpdateSignal(Integer[] signal){
        Signal = signal;
        signalLength = 0;
        for(int l : signal){
            signalLength += Math.abs(l); //100%
        }
        //draw(Canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();
        float topThreshold = (float) (height - (heightMargin/100.0*height));
        float bottomThreshold = (float) (heightMargin/100.0*height);
        int current_x = 0;

        Paint paint = new Paint();
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);

        path.moveTo(current_x, bottomThreshold);
        for (int i = 0; i < Signal.length; i++) {
            int x_step = (int) Math.round((Math.abs(Signal[i])/(double)signalLength) * width);
            if(Signal[i] < 0){
                path.lineTo(current_x, topThreshold);
                current_x += x_step;
                path.lineTo(current_x, topThreshold);

            }else{
                path.lineTo(current_x, bottomThreshold);
                current_x += x_step;
                path.lineTo(current_x, bottomThreshold);
            }
        }

        paint.setStrokeWidth(2);
        paint.setPathEffect(null);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }
}

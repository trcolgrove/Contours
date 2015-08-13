package com.trcolgrove.contours.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import com.trcolgrove.contours.R;

/**
 * Created by Thomas on 6/27/15.
 */
public class DrawingUtils {

    public static int[] keyColors = {R.color.purple, R.color.blue, R.color.turquoise, R.color.lime, R.color.yellow,
            R.color.orange, R.color.red};

    /**
     * Get a darker version of a color
     * @param color the color to be darkened
     * @param factor the factor by which to darken the color
     * @return an integer representation of the darkened color
     */
    private static int darker (int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    /**
     * Get a lighter version of a color
     * @param color the color to be lightened
     * @param factor the factor by which to lighten the color
     * @return an integer representation of the lightened color
     */
    public static int lighter(int color, double factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }


    /**
     * Helper method to draw a generic triangle onto the canvas
     * @param canvas the canvas being drawn to
     * @param a point 1
     * @param b point 2
     * @param c point 3
     */
    public static void drawTriangle(Canvas canvas, Point a, Point b, Point c, Paint paint) {

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawArrow(Canvas canvas, int x, int y, int height, int width, Paint paint) {
        //TODO: make dis work

        int triOffset = width/2;
        Point a = new Point(x, y);
        Point b = new Point(x + triOffset, y - triOffset);
        Point c = new Point(x - triOffset, y - triOffset);

        drawTriangle(canvas, a, b ,c, paint);

        Path arrowPath = new Path();
        final RectF arrowOval = new RectF();
        arrowOval.set((x - (width/4)),
                y-height,
                (x + (width/4)),
                y-height);

        //add the oval to path
        arrowPath.addArc(arrowOval, 180, -180);
        arrowPath.close();
        canvas.drawPath(arrowPath, paint);


    }
    /**
     * convert hardcoded density independent pixel value into a raw pixel value
     * @param dps an int specifying number of dps
     * @param context the context in which to calculate the correct number of pixels
     * @return number of pixels for the specified dps value
     */
    public static int dpToPixels(int dps, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}

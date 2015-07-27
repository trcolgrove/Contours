package com.trcolgrove.colorfulPiano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.trcolgrove.contours.events.NoteEvent;
import com.trcolgrove.contours.R;

import org.puredata.core.PdBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Abstract class representing a basic piano key.
 *
 * <p> Must be extended to include layout and
 * drawing functions.
 * </p>
 *
 * <p>
 * Inheriting classes include WhitePianoKey and
 * BlackPianoKey
 * </p>
 *
 * Created by Thomas on 5/19/15.
 */
public abstract class PianoKey {


    protected static final Map<Integer, Integer> key_positions;
    static {
        key_positions = new HashMap<>();
        key_positions.put(0, 0);
        key_positions.put(1, 0);
        key_positions.put(2, 1);
        key_positions.put(3, 1);
        key_positions.put(4, 2);
        key_positions.put(5, 3);
        key_positions.put(6, 3);
        key_positions.put(7, 4);
        key_positions.put(8, 4);
        key_positions.put(9, 5);
        key_positions.put(10, 5);
        key_positions.put(11, 6);
    }

    private static int[] keyColors = {R.color.purple, R.color.blue, R.color.turquoise, R.color.lime,
            R.color.yellow,
            R.color.orange, R.color.red};

    protected Piano piano;
    protected static int outline_width = 14;
    private ArrayList<Touch> touches = new ArrayList<>();
    protected boolean midiKeyPressed;

    protected int noteValue;
    protected int color;
    protected int darkColor;
    // The area this key occupies.
    protected Rect colorRect;
    protected Rect whiteRect;

    // Objects for subclasses to use for painting, just so they don't have to reallocate every time.
    protected Paint fillPaint;
    protected Paint strokePaint;
    protected Paint borderPaint;

    public PianoKey(Context context, Piano piano, int noteValue) {
        this.piano = piano;
        this.noteValue = noteValue;

        midiKeyPressed = false;
        // Set up some default objects for the key to draw itself with.
        color = context.getResources().getColor(keyColors[key_positions.get(noteValue%12)]);
        darkColor = darker(color, 0.7);
        colorRect = new Rect();
        whiteRect = new Rect();

        fillPaint = new Paint();
        borderPaint = new Paint();
        strokePaint = new Paint();

        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);
        strokePaint.setColor(Color.BLACK);
    }

    private static int darker (int color, double factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    abstract public void layout(Rect drawingRect, int keyCount);

    abstract public void draw(Canvas canvas);

    protected static int getBlackKeyWidth(Rect drawingRect, int keyCount) {
        return (getWhiteKeyWidth(drawingRect, keyCount) * 4) / 7;
    }

    protected static int getBlackKeyHeight(Rect drawingRect) {
        return getWhiteKeyHeight(drawingRect) / 2;
    }

    protected static int getWhiteKeyWidth(Rect drawingRect, int keyCount) {
        return drawingRect.width() / ((keyCount/12)*7 + key_positions.get(keyCount%12) + 1);
    }

    protected static int getWhiteKeyHeight(Rect drawingRect) {
        return drawingRect.height();
    }

    public boolean getTouches() {
        return (touches.size() > 0 || midiKeyPressed);
    }

    public Rect getBounds(){
        return colorRect;
    }

    public void press(Touch touch, int midiVal) {
        this.touches.add(touch);
        this.piano.invalidate();
        _press(midiVal);
    }

    public void unpress(Touch touch, int midiVal) {
        this.touches.remove(touch);
        if (!getTouches()) {
            this.piano.invalidate();
        }
        _unpress(midiVal);
    }

    public void press(int midiVal) {
        midiKeyPressed = true;
        this.piano.postInvalidate();
        _press(midiVal);
    }

    public void unpress() {
        midiKeyPressed = false;
        this.piano.postInvalidate();
    }

    private void _press(int midiVal) {
        PdBase.sendList("note", midiVal, 127);
        EventBus.getDefault().post(new NoteEvent(midiVal));
    }

    private void _unpress(int midiVal) {
        PdBase.sendList("note", midiVal, 0);
    }

    public int getNoteValue() {
        return this.noteValue;
    }
}


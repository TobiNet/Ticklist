package org.tobinet.tick.colorchooser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tobinet.tick.R;

public class AmbilWarnaDialog {
    final AlertDialog dialog;
    final OnAmbilWarnaListener listener;
    final View viewHue;
    final AmbilWarnaKotak viewSatVal;
    final ImageView viewCursor;
    final View viewOldColor;
    final View viewNewColor;
    final ImageView viewTarget;
    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    /**
     * create an AmbilWarnaDialog. call this only from OnCreateDialog() or from
     * a background thread.
     *
     * @param context  current context
     * @param color    current color
     * @param listener an OnAmbilWarnaListener, allowing you to get back error or
     */
    public AmbilWarnaDialog(final Context context, final int color,
                            final OnAmbilWarnaListener listener) {
        this.listener = listener;
        Color.colorToHSV(color, this.currentColorHsv);
        final View view = LayoutInflater.from(context).inflate(
                R.layout.ambilwarna_dialog, null);
        this.viewHue = view.findViewById(R.id.ambilwarna_viewHue);
        this.viewSatVal = (AmbilWarnaKotak) view
                .findViewById(R.id.ambilwarna_viewSatBri);
        this.viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
        this.viewOldColor = view.findViewById(R.id.ambilwarna_warnaLama);
        this.viewNewColor = view.findViewById(R.id.ambilwarna_warnaBaru);
        this.viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
        this.viewContainer = (ViewGroup) view
                .findViewById(R.id.ambilwarna_viewContainer);
        this.viewSatVal.setHue(this.getHue());
        this.viewOldColor.setBackgroundColor(color);
        this.viewNewColor.setBackgroundColor(color);
        this.viewHue.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {
                    float y = event.getY();
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > AmbilWarnaDialog.this.viewHue.getMeasuredHeight()) {
                        y = AmbilWarnaDialog.this.viewHue.getMeasuredHeight() - 0.001f;
                    }
                    float hue = 360.f - 360.f
                            / AmbilWarnaDialog.this.viewHue.getMeasuredHeight()
                            * y;
                    if (hue == 360.f) {
                        hue = 0.f;
                    }
                    AmbilWarnaDialog.this.setHue(hue);
                    // update view
                    AmbilWarnaDialog.this.viewSatVal
                            .setHue(AmbilWarnaDialog.this.getHue());
                    AmbilWarnaDialog.this.moveCursor();
                    AmbilWarnaDialog.this.viewNewColor
                            .setBackgroundColor(AmbilWarnaDialog.this
                                    .getColor());
                    return true;
                }
                return false;
            }
        });
        this.viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();
                    if (x < 0.f) {
                        x = 0.f;
                    }
                    if (x > AmbilWarnaDialog.this.viewSatVal.getMeasuredWidth()) {
                        x = AmbilWarnaDialog.this.viewSatVal.getMeasuredWidth();
                    }
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > AmbilWarnaDialog.this.viewSatVal
                            .getMeasuredHeight()) {
                        y = AmbilWarnaDialog.this.viewSatVal
                                .getMeasuredHeight();
                    }
                    AmbilWarnaDialog.this.setSat(1.f
                            / AmbilWarnaDialog.this.viewSatVal
                            .getMeasuredWidth() * x);
                    AmbilWarnaDialog.this
                            .setVal(1.f - (1.f / AmbilWarnaDialog.this.viewSatVal
                                    .getMeasuredHeight() * y));
                    // update view
                    AmbilWarnaDialog.this.moveTarget();
                    AmbilWarnaDialog.this.viewNewColor
                            .setBackgroundColor(AmbilWarnaDialog.this
                                    .getColor());
                    return true;
                }
                return false;
            }
        });
        this.dialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                                if (AmbilWarnaDialog.this.listener != null) {
                                    AmbilWarnaDialog.this.listener.onOk(
                                            AmbilWarnaDialog.this,
                                            AmbilWarnaDialog.this.getColor());
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                                if (AmbilWarnaDialog.this.listener != null) {
                                    AmbilWarnaDialog.this.listener
                                            .onCancel(AmbilWarnaDialog.this);
                                }
                            }
                        }).setOnCancelListener(new OnCancelListener() {
                    // if back button is used, call back our listener.
                    @Override
                    public void onCancel(
                            final DialogInterface paramDialogInterface) {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener
                                    .onCancel(AmbilWarnaDialog.this);
                        }
                    }
                }).create();
        // kill all padding from the dialog window
        this.dialog.setView(view, 0, 0, 0, 0);
        // move cursor & target on first draw
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AmbilWarnaDialog.this.moveCursor();
                AmbilWarnaDialog.this.moveTarget();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void moveCursor() {
        float y = this.viewHue.getMeasuredHeight()
                - (this.getHue() * this.viewHue.getMeasuredHeight() / 360.f);
        if (y == this.viewHue.getMeasuredHeight()) {
            y = 0.f;
        }
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewCursor
                .getLayoutParams();
        layoutParams.leftMargin = (int) (this.viewHue.getLeft()
                - Math.floor(this.viewCursor.getMeasuredWidth() / 2) - this.viewContainer
                .getPaddingLeft());

        layoutParams.topMargin = (int) (this.viewHue.getTop() + y
                - Math.floor(this.viewCursor.getMeasuredHeight() / 2) - this.viewContainer
                .getPaddingTop());

        this.viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        final float x = this.getSat() * this.viewSatVal.getMeasuredWidth();
        final float y = (1.f - this.getVal())
                * this.viewSatVal.getMeasuredHeight();
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewTarget
                .getLayoutParams();
        layoutParams.leftMargin = (int) (this.viewSatVal.getLeft() + x
                - Math.floor(this.viewTarget.getMeasuredWidth() / 2) - this.viewContainer
                .getPaddingLeft());
        layoutParams.topMargin = (int) (this.viewSatVal.getTop() + y
                - Math.floor(this.viewTarget.getMeasuredHeight() / 2) - this.viewContainer
                .getPaddingTop());
        this.viewTarget.setLayoutParams(layoutParams);
    }

    private int getColor() {
        return Color.HSVToColor(this.currentColorHsv);
    }

    private float getHue() {
        return this.currentColorHsv[0];
    }

    private void setHue(final float hue) {
        this.currentColorHsv[0] = hue;
    }

    private float getSat() {
        return this.currentColorHsv[1];
    }

    private void setSat(final float sat) {
        this.currentColorHsv[1] = sat;
    }

    private float getVal() {
        return this.currentColorHsv[2];
    }

    private void setVal(final float val) {
        this.currentColorHsv[2] = val;
    }

    public void show() {
        this.dialog.show();
    }

    public AlertDialog getDialog() {
        return this.dialog;
    }

    public interface OnAmbilWarnaListener {
        void onCancel(AmbilWarnaDialog dialog);

        void onOk(AmbilWarnaDialog dialog, int color);
    }
}
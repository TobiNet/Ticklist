package org.tobinet.tick.ColorChooser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class AmbilWarnaKotak extends View {
	Paint paint;
	Shader luar;
	final float[] color = { 1.f, 1.f, 1.f };

	public AmbilWarnaKotak(final Context context) {
		this(context, null);
	}

	public AmbilWarnaKotak(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AmbilWarnaKotak(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		if (this.paint == null) {
			this.paint = new Paint();
			this.luar = new LinearGradient(0.f, 0.f, 0.f,
					this.getMeasuredHeight(), 0xffffffff, 0xff000000,
					TileMode.CLAMP);
		}
		final int rgb = Color.HSVToColor(this.color);
		final Shader dalam = new LinearGradient(0.f, 0.f,
				this.getMeasuredWidth(), 0.f, 0xffffffff, rgb, TileMode.CLAMP);
		final ComposeShader shader = new ComposeShader(this.luar, dalam,
				PorterDuff.Mode.MULTIPLY);
		this.paint.setShader(shader);
		canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(),
				this.getMeasuredHeight(), this.paint);
	}

	void setHue(final float hue) {
		this.color[0] = hue;
		invalidate();
	}
}
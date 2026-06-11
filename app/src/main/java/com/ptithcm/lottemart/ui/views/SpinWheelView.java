package com.ptithcm.lottemart.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;
import com.ptithcm.lottemart.data.api.ProductApiService.SpinEvent.Reward;

public class SpinWheelView extends View {
    private List<Reward> rewards;
    private Paint paint;
    private Paint textPaint;
    private RectF rectF;
    private int[] colors = {Color.parseColor("#FF5252"), Color.parseColor("#FFC107"), Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"), Color.parseColor("#9C27B0")};

    public SpinWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        rectF = new RectF();
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (rewards == null || rewards.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;

        rectF.set(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);

        int count = rewards.size();
        float sweepAngle = 360f / count;

        for (int i = 0; i < count; i++) {
            paint.setColor(colors[i % colors.length]);
            float startAngle = i * sweepAngle;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Draw text
            String text = rewards.get(i).getRewardName();
            Path path = new Path();
            path.addArc(rectF, startAngle, sweepAngle);
            // Draw text along the arc
            float textRadius = radius * 0.75f;
            float cx = width / 2f;
            float cy = height / 2f;
            float angle = startAngle + sweepAngle / 2f;
            float x = cx + (float) (textRadius * Math.cos(Math.toRadians(angle)));
            float y = cy + (float) (textRadius * Math.sin(Math.toRadians(angle)));
            
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(angle + 90);
            canvas.drawText(text, 0, 0, textPaint);
            canvas.restore();
        }
        
        // Draw center circle
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2f, height / 2f, radius * 0.1f, paint);
    }
}

package com.teamtreehouse.customviewsbase;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;
import java.util.List;

/**
 * Created by LuongHH on 5/24/2017.
 */

public class ChartView extends View {

    List<StockData> data;
    List<StockData> subset;
    float width, height, maxPrice, minPrice, textHeight;
    Paint paint = new Paint();
    Paint strokePaint = new Paint();
    Paint textPaint = new Paint();

    public ChartView(Context context) {
        super(context);
        init(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();
        float chartWidth = width - textPaint.measureText("1000");
        float rectWidth = chartWidth / subset.size();
        strokePaint.setStrokeWidth(rectWidth / 8);
        float left = 0;
        float bottom, top;

        for (int i = subset.size() - 1; i >= 0; i--) {
            StockData stockData = subset.get(i);
            if (stockData.close >= stockData.open) {
                paint.setColor(Color.GREEN);
                top = stockData.close;
                bottom = stockData.open;
            } else {
                paint.setColor(Color.RED);
                top = stockData.open;
                bottom = stockData.close;
            }
            canvas.drawLine(left + rectWidth/2, getYPosition(stockData.high), left + rectWidth / 2, getYPosition(stockData.low), strokePaint);
            canvas.drawRect(left, getYPosition(top), left + rectWidth, getYPosition(bottom), paint);
            left += rectWidth;
        }
        strokePaint.setStrokeWidth(1);
        for (int j = (int)minPrice; j < maxPrice; j++) {
            if (j % 20 == 0) {
                canvas.drawLine(0, getYPosition(j), chartWidth, getYPosition(j), strokePaint);
                canvas.drawText(j + "", width, getYPosition(j) + textHeight / 2, textPaint);
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartView, 0, 0);
        int resId = typedArray.getResourceId(R.styleable.ChartView_data, 0);
        InputStream inputStream = getResources().openRawResource(resId);
        data = CSVParser.read(inputStream);
        showLast();
        strokePaint.setColor(Color.WHITE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        Rect textBounds = new Rect();
        textPaint.getTextBounds("0", 0, 1, textBounds);
        textHeight = textBounds.height();
    }

    public void showLast(int n) {
        subset = data.subList(0, n);
        updateMaxAndMin();
        invalidate();
    }

    public void showLast() {
        showLast(data.size());
    }

    private void updateMaxAndMin() {
        minPrice = 99999f;
        maxPrice = -1f;
        for (StockData stockData : subset) {
            if (stockData.high > maxPrice) {
                maxPrice = stockData.high;
            }
            if (stockData.low < minPrice) {
                minPrice = stockData.low;
            }
        }
    }

    private float getYPosition(float price) {
        float scaleFactoryY = (price - minPrice) / (maxPrice - minPrice);
        return height - height * scaleFactoryY;
    }
}

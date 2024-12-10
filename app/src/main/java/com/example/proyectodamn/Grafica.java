package com.example.proyectodamn;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class Grafica extends View {
    private Paint p, gridPaint, textPaint;
    private Path path;
    private float[] datos;
    private int maxValor;
    private int numPuntos;
    private int delay; // Tiempo entre cada punto
    private int indiceGeneral; // Punto actual que se está dibujando
    private Handler handler;

    public Grafica(Context c) {
        super(c);
        init();
    }

    public Grafica(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(5);

        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24);
        textPaint.setAntiAlias(true);

        path = new Path();
        handler = new Handler();
    }

    public void setDatos(float[] datos, int maxValor) {
        this.datos = datos;
        this.maxValor = maxValor;
        this.numPuntos = datos.length;
        this.indiceGeneral = 0;
        invalidate();
    }

    public void iniciarDibujo(int duracionMinutos) {
        // Calcula el delay entre puntos para que la duración sea exacta
        this.delay = (duracionMinutos * 60 * 1000) / numPuntos;
        this.indiceGeneral = 0;
        handler.post(drawRunnable);
    }

    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            if (indiceGeneral < datos.length) {
                indiceGeneral++;
                invalidate(); // Redibuja el gráfico
                handler.postDelayed(this, delay); // Llama al siguiente punto
            }
        }
    };

    public void reset() {
        indiceGeneral = 0; // Reinicia el índice de dibujo
        path.reset(); // Limpia el Path
        invalidate(); // Redibuja la vista
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int gridLines = 10;

        // Dibujar cuadrícula
        for (int i = 0; i <= gridLines; i++) {
            float y = padding + i * (height - 2 * padding) / gridLines;
            canvas.drawLine(padding, y, width - padding, y, gridPaint);
            float valueY = maxValor - (i * maxValor / gridLines);
            canvas.drawText(String.valueOf((int) valueY), padding - 40, y + 8, textPaint);

            float x = padding + i * (width - 2 * padding) / gridLines;
            canvas.drawLine(x, padding, x, height - padding, gridPaint);
            float valueX = i * (numPuntos - 1) / gridLines;
            canvas.drawText(String.valueOf((int) valueX), x - 10, height - padding + 30, textPaint);
        }

        // Dibujar ejes
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);
        canvas.drawLine(padding, height - padding, width - padding, height - padding, p);
        canvas.drawLine(padding, padding, padding, height - padding, p);

        // Dibujar gráfica
        p.setColor(Color.BLUE);
        path.reset();
        if (indiceGeneral > 0) {
            path.moveTo(padding, height - padding - (datos[0] * (height - 2 * padding) / maxValor));
            for (int i = 1; i < indiceGeneral; i++) {
                float x = padding + i * (width - 2 * padding) / (numPuntos - 1);
                float y = height - padding - (datos[i] * (height - 2 * padding) / maxValor);
                path.lineTo(x, y);
            }
            canvas.drawPath(path, p);
        }
    }
}

package com.example.proyectodamn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ControlActivity extends AppCompatActivity {

    private int volumen = 50;
    private int pitch = 50;
    private boolean ruidoBlanco = true;
    private String programaSeleccionado;

    private TextView tvVolumen, tvPitch;
    private LinearLayout contenedorGraficas;

    private Grafica graficaRuidoBlanco, graficaEstimuloOcular, graficaAmplitudOcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // Inicialización de vistas
        tvVolumen = findViewById(R.id.tvVolumen);
        tvPitch = findViewById(R.id.tvPitch);
        Switch switchRuidoBlanco = findViewById(R.id.switchRuidoBlanco);
        Spinner spinnerProgramas = findViewById(R.id.spinnerProgramas);
        Button btnVolumenMas = findViewById(R.id.btnVolumenMas);
        Button btnVolumenMenos = findViewById(R.id.btnVolumenMenos);
        Button btnPitchSubir = findViewById(R.id.btnPitchSubir);
        Button btnPitchBajar = findViewById(R.id.btnPitchBajar);
        Button btnPausarEjecutar = findViewById(R.id.btnPausarEjecutar);
        contenedorGraficas = findViewById(R.id.contenedorGraficas);

        // Configuración inicial
        configurarSpinner(spinnerProgramas);

        // Listeners
        btnVolumenMas.setOnClickListener(v -> ajustarVolumen(1));
        btnVolumenMenos.setOnClickListener(v -> ajustarVolumen(-1));
        btnPitchSubir.setOnClickListener(v -> ajustarPitch(1));
        btnPitchBajar.setOnClickListener(v -> ajustarPitch(-1));

        switchRuidoBlanco.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ruidoBlanco = isChecked;
            Toast.makeText(this, "Ruido Blanco: " + (isChecked ? "Activado" : "Desactivado"), Toast.LENGTH_SHORT).show();
        });

        btnPausarEjecutar.setOnClickListener(v -> {
            if ("A6".equals(programaSeleccionado)) {
                mostrarGraficas();
            } else {
                Toast.makeText(this, "Seleccione el programa A6 para mostrar las gráficas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSpinner(Spinner spinner) {
        ArrayList<String> programas = new ArrayList<>(Arrays.asList("A0", "A1", "A2", "A3", "A4", "A5", "A6"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, programas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                programaSeleccionado = programas.get(position);
                Toast.makeText(ControlActivity.this, "Programa seleccionado: " + programaSeleccionado, Toast.LENGTH_SHORT).show();

                // Limpiar gráficas si no es A6
                if (!"A6".equals(programaSeleccionado)) {
                    ocultarGraficas();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void ajustarVolumen(int delta) {
        volumen = Math.max(0, Math.min(100, volumen + delta));
        tvVolumen.setText(String.valueOf(volumen));
    }

    private void ajustarPitch(int delta) {
        pitch = Math.max(0, Math.min(100, pitch + delta));
        tvPitch.setText(String.valueOf(pitch));
    }

    private void mostrarGraficas() {
        // Limpiar el contenedor antes de agregar gráficas
        contenedorGraficas.removeAllViews();

        // Crear instancias de las gráficas
        graficaRuidoBlanco = new Grafica(this);
        graficaEstimuloOcular = new Grafica(this);
        graficaAmplitudOcular = new Grafica(this);

        // Configurar las gráficas con datos
        graficaRuidoBlanco.setDatos(generarDatosEscalonados(16), 16);
        graficaEstimuloOcular.setDatos(generarDatosEscalonados(16), 16);
        graficaAmplitudOcular.setDatos(generarDatosLineales(), 100);

        // Agregar gráficas al contenedor
        contenedorGraficas.addView(graficaRuidoBlanco, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400));
        contenedorGraficas.addView(graficaEstimuloOcular, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400));
        contenedorGraficas.addView(graficaAmplitudOcular, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400));

        graficaRuidoBlanco.iniciarDibujo(45);
        graficaEstimuloOcular.iniciarDibujo(45);
        graficaAmplitudOcular.iniciarDibujo(45);

        // Asegurarse de que el contenedor se actualice
        contenedorGraficas.invalidate();
    }


    private void ocultarGraficas() {
        contenedorGraficas.removeAllViews();
    }

    private float[] generarDatosEscalonados(float inicio) {
        int puntos = 45;
        float[] datos = new float[puntos];
        float nivel = inicio;
        int intervalos = 5;
        float decremento = inicio / (puntos / intervalos);

        for (int i = 0; i < puntos; i++) {
            datos[i] = nivel;
            if ((i + 1) % intervalos == 0 && nivel > 0) {
                nivel -= decremento;
                if (nivel < 0) nivel = 0;
            }
        }
        return datos;
    }

    private float[] generarDatosLineales() {
        int puntos = 45;
        float[] datos = new float[puntos];
        for (int i = 0; i < puntos; i++) {
            datos[i] = 100 - (i * (100f / (puntos - 1)));
        }
        return datos;
    }
}

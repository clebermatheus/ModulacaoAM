package com.github.clebermatheus.modulacaoam.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.github.clebermatheus.modulacaoam.R;
import com.github.clebermatheus.modulacaoam.models.Modulacao;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "ModulacaoAM";
    public LineChart grafico;
    private double tempoMaximo = 5, nivelDC = 4, tensaoPortadora = 6, tensaoModulado = 2;
    private double freqPortadora = Modulacao.frequenciaAngular(750),
            freqModulado = Modulacao.frequenciaAngular(40);
    private SharedPreferences preferences;
    private GraficoTipo tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grafico = findViewById(R.id.grafico);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        Description descricao = new Description();
        XAxis x = grafico.getXAxis();
        YAxis left = grafico.getAxisLeft();
        YAxis right = grafico.getAxisRight();
        Legend legend = grafico.getLegend();

        descricao.setEnabled(false);
        grafico.setDescription(descricao);
        x.setTextColor(WHITE);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setAxisMinimum(0);
        left.setTextColor(WHITE);
        right.setEnabled(false);
        legend.setTextColor(WHITE);
        if(actionBar != null){actionBar.setTitle(R.string.app_name);}
        loadGraficoInformacao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        grafico.invalidate();
        tempoMaximo = Double.parseDouble(preferences.getString("plus_tempo_maximo", String.valueOf
                (tempoMaximo)));
        nivelDC = Double.parseDouble(preferences.getString("plus_nivel_dc", String.valueOf(nivelDC)));
        freqModulado = Modulacao.frequenciaAngular(Double.parseDouble(
                preferences.getString("info_frequencia", String.valueOf(40))));
        freqPortadora = Modulacao.frequenciaAngular(Double.parseDouble(
                preferences.getString("portadora_frequencia", String.valueOf(750))));
        tensaoModulado = Double.parseDouble(preferences.getString("info_amplitude", String.valueOf(tensaoModulado)));
        tensaoPortadora = Double.parseDouble(preferences.getString("portadora_amplitude", String.valueOf(tensaoPortadora)));
        switch(tipo){
            case INFORMACAO: loadGraficoInformacao(); break;
            case PORTADORA: loadGraficoPortadora(); break;
            case MODULADO: loadGraficoModulado(); break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.navigation_informacao).setOnMenuItemClickListener(view -> loadGraficoInformacao());
        menu.findItem(R.id.navigation_portadora).setOnMenuItemClickListener(view -> loadGraficoPortadora());
        menu.findItem(R.id.navigation_modulado).setOnMenuItemClickListener(view -> loadGraficoModulado());
        menu.findItem(R.id.navigation_config).setOnMenuItemClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ConfigActivity.class));
            return true;
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean loadGraficoInformacao(){
        tipo = GraficoTipo.INFORMACAO;
        grafico.invalidate();
        ArrayList<Entry> dadosInformacao = new ArrayList<>();
        for(float i=0; i<tempoMaximo; i+=0.001){
            dadosInformacao.add(new Entry(i, (float) Modulacao.tensaoInstantanea(tensaoModulado,
                    freqModulado, i)));
        }
        LineDataSet ldInformacao = new LineDataSet(dadosInformacao, "Informação");
        ldInformacao.setHighlightEnabled(false);
        ldInformacao.setValueTextColor(WHITE);
        ldInformacao.setDrawCircles(false);
        ldInformacao.setAxisDependency(YAxis.AxisDependency.LEFT);
        LineData ld = new LineData(ldInformacao);
        grafico.setData(new LineData(ldInformacao));
        return true;
    }

    private boolean loadGraficoPortadora(){
        tipo = GraficoTipo.PORTADORA;
        grafico.invalidate();
        ArrayList<Entry> dadosPortadora = new ArrayList<>();
        for(float i=0; i<tempoMaximo; i+=0.001){
            dadosPortadora.add(new Entry(i, (float) Modulacao.modulacaoAMDSB(tensaoPortadora,
                    freqPortadora, freqModulado, nivelDC, i)));
        }
        LineDataSet ldPortadora = new LineDataSet(dadosPortadora, "Portadora");
        ldPortadora.setHighlightEnabled(false);
        ldPortadora.setValueTextColor(WHITE);
        ldPortadora.setDrawCircles(false);
        ldPortadora.setAxisDependency(YAxis.AxisDependency.LEFT);
        grafico.setData(new LineData(ldPortadora));
        return true;
    }

    private boolean loadGraficoModulado(){
        tipo = GraficoTipo.MODULADO;
        grafico.invalidate();
        ArrayList<Entry> dadosModulado = new ArrayList<>();
        for(float i=0; i<tempoMaximo; i+=0.001){
            double k = Modulacao.constanteProporcionalidade(tensaoPortadora, tensaoModulado, nivelDC);
            dadosModulado.add(new Entry(i, (float) Modulacao.sinalModulado(tensaoPortadora, k,
                    Modulacao.tensaoInstantanea(tensaoModulado, freqModulado, i), freqPortadora, i)));
        }
        LineDataSet ldModulado = new LineDataSet(dadosModulado, "Modulado");
        ldModulado.setHighlightEnabled(false);
        ldModulado.setValueTextColor(WHITE);
        ldModulado.setDrawCircles(false);
        ldModulado.setAxisDependency(YAxis.AxisDependency.LEFT);
        grafico.setData(new LineData(ldModulado));
        return true;
    }


    private enum GraficoTipo {
        INFORMACAO(0),
        PORTADORA(1),
        MODULADO(2);

        private final int value;
        GraficoTipo(int value){this.value = value;}
        public int getValue() {return value;}
    }
}

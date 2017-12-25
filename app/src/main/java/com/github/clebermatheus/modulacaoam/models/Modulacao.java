package com.github.clebermatheus.modulacaoam.models;

/**
 * Classe que faz o cálculo de modulação
 *
 * Created by clebermatheus on 24/10/17.
 */

public class Modulacao {
    private static final int c = 3*10^8; // c = 3*10^8 m/s

    public static double constanteProporcionalidade(double tensaoPicoPortadora, double
            tensaoPicoModulador, double m){return (m*tensaoPicoPortadora)/tensaoPicoModulador;}

    public static double frequenciaAngular(double frequencia){return Math.toRadians(2*Math.PI*frequencia);}

    public static double modulacaoAMDSB(double tensaoPico, double freqPortadora, double
            freqModulado, double modulado, double tempo){
        return tensaoInstantanea(tensaoPico, freqPortadora, tempo) +
                (((modulado*tensaoPico)/2) * Math.cos(freqPortadora + freqModulado) + Math.cos
                        (freqPortadora - freqModulado));
    }

    public static double sinalModulado(double tensaoPicoPortadora, double constanteProporcionalidade,
                                double tensaoInstantaneaModulado, double frequenciaAngular, double
                                    tempo)
    {return (tensaoPicoPortadora+constanteProporcionalidade*tensaoInstantaneaModulado)*Math.cos(frequenciaAngular*tempo);}

    public static double tensaoInstantanea(double tensaoPico, double frequenciaAngular, double
            tempo){
        return tensaoPico*Math.cos(frequenciaAngular*tempo);
    }
}

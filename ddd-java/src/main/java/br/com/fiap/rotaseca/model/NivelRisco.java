package br.com.fiap.rotaseca.model;

/**
 * Nivel de risco de alagamento. A classificacao pela chuva acumulada em 1 hora
 * segue a escala de intensidade usada pelo CGE de Sao Paulo
 * (fraca, moderada, forte e muito forte).
 */
public enum NivelRisco {
    BAIXO,
    MODERADO,
    ALTO,
    MUITO_ALTO;

    public static NivelRisco porPrecipitacao(double milimetrosPorHora) {
        if (milimetrosPorHora >= 50) {
            return MUITO_ALTO;
        }
        if (milimetrosPorHora >= 25) {
            return ALTO;
        }
        if (milimetrosPorHora >= 10) {
            return MODERADO;
        }
        return BAIXO;
    }

    /** Somente chuvas a partir de MODERADO geram alerta para os moradores. */
    public boolean geraAlerta() {
        return this != BAIXO;
    }
}

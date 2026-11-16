package br.com.fiap.rotaseca.model;

/**
 * Resposta do registro de uma leitura de chuva: a leitura salva, o nível de risco
 * calculado e o alerta gerado automaticamente (nulo quando a chuva é fraca).
 */
public record RegistroLeitura(LeituraChuva leitura, NivelRisco nivelRisco, Alerta alertaGerado) {
}

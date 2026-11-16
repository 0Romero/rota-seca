package br.com.fiap.rotaseca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RotaSecaApplication {

    public static void main(String[] args) {
        // O servidor do Render roda em UTC; os horários de chuva e alerta são de São Paulo
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(RotaSecaApplication.class, args);
    }
}

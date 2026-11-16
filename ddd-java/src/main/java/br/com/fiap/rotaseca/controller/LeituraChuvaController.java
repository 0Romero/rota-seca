package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.LeituraChuvaBO;
import br.com.fiap.rotaseca.model.LeituraChuva;
import br.com.fiap.rotaseca.model.RegistroLeitura;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/leituras")
@Tag(name = "Leituras de chuva", description = "Chuva acumulada em 1 hora por bairro")
public class LeituraChuvaController {

    private final LeituraChuvaBO bo;

    public LeituraChuvaController(LeituraChuvaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista as leituras, da mais recente para a mais antiga (filtro opcional por bairro)")
    public List<LeituraChuva> listar(@RequestParam(required = false) Long bairroId) {
        return bo.listar(bairroId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma leitura pelo id")
    public LeituraChuva buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Registra uma leitura; se a chuva for moderada ou mais forte, gera um alerta automaticamente")
    public ResponseEntity<RegistroLeitura> registrar(@Valid @RequestBody LeituraChuva leitura) {
        RegistroLeitura registro = bo.registrar(leitura);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registro.leitura().getId()).toUri();
        return ResponseEntity.created(uri).body(registro);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Corrige uma leitura (não gera novo alerta)")
    public LeituraChuva atualizar(@PathVariable Long id, @Valid @RequestBody LeituraChuva leitura) {
        return bo.atualizar(id, leitura);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma leitura")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

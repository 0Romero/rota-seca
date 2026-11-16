package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.AlertaBO;
import br.com.fiap.rotaseca.model.Alerta;
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
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Avisos de risco de alagamento por bairro")
public class AlertaController {

    private final AlertaBO bo;

    public AlertaController(AlertaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista todos os alertas, do mais recente para o mais antigo")
    public List<Alerta> listar() {
        return bo.listar();
    }

    @GetMapping("/ativos")
    @Operation(summary = "Lista os alertas em vigor agora (filtro opcional por bairro)")
    public List<Alerta> listarAtivos(@RequestParam(required = false) Long bairroId) {
        return bo.listarAtivos(bairroId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um alerta pelo id")
    public Alerta buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Emite um alerta manual (Defesa Civil)")
    public ResponseEntity<Alerta> cadastrar(@Valid @RequestBody Alerta alerta) {
        Alerta criado = bo.cadastrar(alerta);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um alerta (ex.: prorrogar a validade)")
    public Alerta atualizar(@PathVariable Long id, @Valid @RequestBody Alerta alerta) {
        return bo.atualizar(id, alerta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um alerta")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

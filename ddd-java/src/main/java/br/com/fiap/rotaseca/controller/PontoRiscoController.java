package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.PontoRiscoBO;
import br.com.fiap.rotaseca.model.PontoRisco;
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
@RequestMapping("/api/pontos-risco")
@Tag(name = "Pontos de risco", description = "Trechos de rua que costumam alagar")
public class PontoRiscoController {

    private final PontoRiscoBO bo;

    public PontoRiscoController(PontoRiscoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista os pontos de risco (filtro opcional por bairro)")
    public List<PontoRisco> listar(@RequestParam(required = false) Long bairroId) {
        return bo.listar(bairroId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um ponto de risco pelo id")
    public PontoRisco buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um ponto de risco")
    public ResponseEntity<PontoRisco> cadastrar(@Valid @RequestBody PontoRisco ponto) {
        PontoRisco criado = bo.cadastrar(ponto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um ponto de risco")
    public PontoRisco atualizar(@PathVariable Long id, @Valid @RequestBody PontoRisco ponto) {
        return bo.atualizar(id, ponto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um ponto de risco")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

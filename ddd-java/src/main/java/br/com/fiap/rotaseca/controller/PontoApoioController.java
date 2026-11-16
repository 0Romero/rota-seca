package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.PontoApoioBO;
import br.com.fiap.rotaseca.model.PontoApoio;
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
@RequestMapping("/api/pontos-apoio")
@Tag(name = "Pontos de apoio", description = "Abrigos, escolas e UBS que recebem moradores")
public class PontoApoioController {

    private final PontoApoioBO bo;

    public PontoApoioController(PontoApoioBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista os pontos de apoio; com bairroId, traz só os ativos do bairro, do maior para o menor")
    public List<PontoApoio> listar(@RequestParam(required = false) Long bairroId) {
        return bairroId == null ? bo.listar() : bo.listarAtivosPorBairro(bairroId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um ponto de apoio pelo id")
    public PontoApoio buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um ponto de apoio")
    public ResponseEntity<PontoApoio> cadastrar(@Valid @RequestBody PontoApoio ponto) {
        PontoApoio criado = bo.cadastrar(ponto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um ponto de apoio")
    public PontoApoio atualizar(@PathVariable Long id, @Valid @RequestBody PontoApoio ponto) {
        return bo.atualizar(id, ponto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um ponto de apoio")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

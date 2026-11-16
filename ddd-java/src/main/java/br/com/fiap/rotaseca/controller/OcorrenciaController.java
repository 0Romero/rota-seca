package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.OcorrenciaBO;
import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
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
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrências", description = "Alagamentos reportados por moradores ou agentes")
public class OcorrenciaController {

    private final OcorrenciaBO bo;

    public OcorrenciaController(OcorrenciaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista as ocorrências (filtro opcional por status)")
    public List<Ocorrencia> listar(@RequestParam(required = false) StatusOcorrencia status) {
        return bo.listar(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ocorrência pelo id")
    public Ocorrencia buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Reporta um alagamento em um ponto de risco")
    public ResponseEntity<Ocorrencia> registrar(@Valid @RequestBody Ocorrencia ocorrencia) {
        Ocorrencia criada = bo.registrar(ocorrencia);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma ocorrência (status segue ABERTA -> EM_ATENDIMENTO -> ENCERRADA)")
    public Ocorrencia atualizar(@PathVariable Long id, @Valid @RequestBody Ocorrencia ocorrencia) {
        return bo.atualizar(id, ocorrencia);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma ocorrência")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

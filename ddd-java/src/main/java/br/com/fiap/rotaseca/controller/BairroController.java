package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.BairroBO;
import br.com.fiap.rotaseca.model.Bairro;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bairros")
@Tag(name = "Bairros", description = "Bairros atendidos pelo Rota Seca")
public class BairroController {

    private final BairroBO bo;

    public BairroController(BairroBO bo) {
        this.bo = bo;
    }

    @GetMapping
    @Operation(summary = "Lista todos os bairros")
    public List<Bairro> listar() {
        return bo.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um bairro pelo id")
    public Bairro buscar(@PathVariable Long id) {
        return bo.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um bairro")
    public ResponseEntity<Bairro> cadastrar(@Valid @RequestBody Bairro bairro) {
        Bairro criado = bo.cadastrar(bairro);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um bairro")
    public Bairro atualizar(@PathVariable Long id, @Valid @RequestBody Bairro bairro) {
        return bo.atualizar(id, bairro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um bairro (só se não houver dados ligados a ele)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

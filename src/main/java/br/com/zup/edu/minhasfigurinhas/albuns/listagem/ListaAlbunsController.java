package br.com.zup.edu.minhasfigurinhas.albuns.listagem;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.edu.minhasfigurinhas.albuns.AlbumRepository;

@RestController
public class ListaAlbunsController {

    private final AlbumRepository repository;

    public ListaAlbunsController(AlbumRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/albuns")
    public ResponseEntity<?> lista() {
        List<AlbumResponse> albuns = repository.findAll(Sort.by(ASC, "titulo"))
                                               .stream()
                                               .map(album -> {
                                                   return new AlbumResponse(album);
                                               })
                                               .collect(toList());

        return ResponseEntity.ok(albuns);
    }

}

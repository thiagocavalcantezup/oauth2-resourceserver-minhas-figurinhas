package br.com.zup.edu.minhasfigurinhas.albuns;

import java.net.URI;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class NovoAlbumController {

    private final AlbumRepository repository;

    public NovoAlbumController(AlbumRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @PostMapping("/api/albuns")
    public ResponseEntity<?> cadastra(@RequestBody @Valid NovoAlbumRequest request,
                                      UriComponentsBuilder uriBuilder,
                                      @AuthenticationPrincipal Jwt principal) {

        String username = principal.getClaim("preferred_username");
        if (username == null) {
            username = "anonymous";
        }

        Album album = request.toModel(username);
        repository.save(album);

        URI location = uriBuilder.path("/api/albuns/{id}").buildAndExpand(album.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

}

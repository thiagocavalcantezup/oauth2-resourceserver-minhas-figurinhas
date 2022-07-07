package br.com.zup.edu.minhasfigurinhas.albuns.detalhamento;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import base.SpringBootIntegrationTest;
import br.com.zup.edu.minhasfigurinhas.albuns.Album;
import br.com.zup.edu.minhasfigurinhas.albuns.AlbumRepository;
import br.com.zup.edu.minhasfigurinhas.albuns.Figurinha;

class DetalhaAlbumControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private AlbumRepository repository;

    @BeforeEach
    private void setUp() {
        repository.deleteAll();
    }

    @Test
    public void deveDetalharAlbumComSuasFigurinhas() throws Exception {
        // cenario
        Album album = new Album("DBZ", "Album do DBZ", "rafael.ponte");
        album.adiciona(
            new Figurinha("goku", "http://animes.com/dbz/goku.jpg"),
            new Figurinha("picollo", "http://animes.com/dbz/picollo.jpg")
        );
        repository.save(album);

        // ação e validação
        mockMvc.perform(
            GET("/api/albuns/{id}", album.getId()).with(
                jwt().authorities(new SimpleGrantedAuthority("SCOPE_albuns:read"))
            )
        )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(album.getId()))
               .andExpect(jsonPath("$.titulo").value(album.getTitulo()))
               .andExpect(jsonPath("$.descricao").value(album.getDescricao()))
               .andExpect(jsonPath("$.dono").value(album.getDono()))
               .andExpect(jsonPath("$.figurinhas", hasSize(2)))
               .andExpect(
                   jsonPath("$.figurinhas[0].id").value(album.getFigurinhas().get(0).getId())
               )
               .andExpect(
                   jsonPath("$.figurinhas[0].descricao").value(
                       album.getFigurinhas().get(0).getDescricao()
                   )
               )
               .andExpect(
                   jsonPath("$.figurinhas[0].enderecoDaImagem").value(
                       album.getFigurinhas().get(0).getEnderecoDaImagem()
                   )
               )
               .andExpect(
                   jsonPath("$.figurinhas[1].id").value(album.getFigurinhas().get(1).getId())
               )
               .andExpect(
                   jsonPath("$.figurinhas[1].descricao").value(
                       album.getFigurinhas().get(1).getDescricao()
                   )
               )
               .andExpect(
                   jsonPath("$.figurinhas[1].enderecoDaImagem").value(
                       album.getFigurinhas().get(1).getEnderecoDaImagem()
                   )
               )

        ;
    }

    @Test
    public void naoDeveDetalharAlbumComSuasFigurinhas_quandoAlbumNaoEncontrado() throws Exception {
        // cenario
        Album album = new Album("CDZ", "Album do CDZ", "jordi.silva");
        album.adiciona(new Figurinha("Shiryu de Dragrão", "http://animes.com/dbz/shiryu.jpg"));
        repository.save(album);

        // ação e validação
        mockMvc.perform(
            GET("/api/albuns/{id}", -9999).with(
                jwt().authorities(new SimpleGrantedAuthority("SCOPE_albuns:read"))
            )
        )
               .andExpect(status().isNotFound())
               .andExpect(status().reason("album não encontrado"))
               .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void naoDeveDetalharAlbumComSuasFigurinhas_quandoTokenNaoEnviado() throws Exception {
        // cenario
        Album album = new Album("DBZ", "Album do DBZ", "rafael.ponte");
        album.adiciona(
            new Figurinha("goku", "http://animes.com/dbz/goku.jpg"),
            new Figurinha("picollo", "http://animes.com/dbz/picollo.jpg")
        );
        repository.save(album);

        // ação e validação
        mockMvc.perform(GET("/api/albuns/{id}", album.getId()))
               .andExpect(status().isUnauthorized());
    }

    @Test
    public void naoDeveDetalharAlbumComSuasFigurinhas_quandoTokenNaoPossuiEscopoApropriado() throws Exception {
        // cenario
        Album album = new Album("DBZ", "Album do DBZ", "rafael.ponte");
        album.adiciona(
            new Figurinha("goku", "http://animes.com/dbz/goku.jpg"),
            new Figurinha("picollo", "http://animes.com/dbz/picollo.jpg")
        );
        repository.save(album);

        // ação e validação
        mockMvc.perform(GET("/api/albuns/{id}", album.getId()).with(jwt()))
               .andExpect(status().isForbidden());
    }

}

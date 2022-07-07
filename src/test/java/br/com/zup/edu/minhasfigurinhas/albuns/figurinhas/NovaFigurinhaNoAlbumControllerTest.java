package br.com.zup.edu.minhasfigurinhas.albuns.figurinhas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import base.SpringBootIntegrationTest;
import br.com.zup.edu.minhasfigurinhas.albuns.Album;
import br.com.zup.edu.minhasfigurinhas.albuns.AlbumRepository;
import br.com.zup.edu.minhasfigurinhas.albuns.Figurinha;

class NovaFigurinhaNoAlbumControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private AlbumRepository repository;

    private static Album ALBUM;

    @BeforeEach
    private void setUp() {
        repository.deleteAll();
        ALBUM = new Album("DBZ", "Album do DBZ", "rponte");
        ALBUM.adiciona(new Figurinha("picollo", "http://animes.com/dbz/picollo.jpg"));
        repository.save(ALBUM);
    }

    @Test
    public void deveAdicionarNovaFigurinhaNoAlbum() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest novaFigurinha = new NovaFigurinhaNoAlbumRequest(
            "gohan", "http://animes.com/dbz/gohan.jpg"
        );

        // ação
        mockMvc.perform(
            POST(uri(ALBUM.getId()), novaFigurinha).with(
                jwt().jwt(jwt -> jwt.claim("preferred_username", "rponte"))
                     .authorities(new SimpleGrantedAuthority("SCOPE_albuns:write"))
            )
        )
               .andExpect(status().isCreated())
               .andExpect(redirectedUrlPattern("**/api/albuns/*/figurinhas/**"));

        // validação
        Album encontrado = repository.findByIdWithFigurinhas(ALBUM.getId());
        assertThat(encontrado.getFigurinhas()).hasSize(2)
                                              .extracting("descricao")
                                              .containsExactly("picollo", "gohan");
    }

    @Test
    public void naoDeveAdicionarNovaFigurinhaNoAlbum_quandoAlbumNaoEncontrado() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest figurinhaInvalida = new NovaFigurinhaNoAlbumRequest(
            "gohan", "http://animes.com/dbz/gohan.jpg"
        );

        // ação
        mockMvc.perform(
            POST(uri(-2022L), figurinhaInvalida).with(
                jwt().authorities(new SimpleGrantedAuthority("SCOPE_albuns:write"))
            )
        ).andExpect(status().isNotFound()).andExpect(status().reason("album não encontrado"));
    }

    @Test
    public void naoDeveAdicionarNovaFigurinhaNoAlbum_quandoForDeOutroUsuario() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest figurinhaInvalida = new NovaFigurinhaNoAlbumRequest(
            "gohan", "http://animes.com/dbz/gohan.jpg"
        );

        // ação
        mockMvc.perform(
            POST(uri(ALBUM.getId()), figurinhaInvalida).with(
                jwt().jwt(jwt -> jwt.claim("preferred_username", "outro"))
                     .authorities(new SimpleGrantedAuthority("SCOPE_albuns:write"))
            )
        )
               .andExpect(status().isBadRequest())
               .andExpect(status().reason("um album só pode ser alterado pelo seu dono"));
    }

    @Test
    public void naoDeveAdicionarNovaFigurinhaNoAlbum_quandoParametrosInvalidos() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest figurinhaInvalida = new NovaFigurinhaNoAlbumRequest("", "");

        // ação
        mockMvc.perform(
            POST(uri(ALBUM.getId()), figurinhaInvalida).with(
                jwt().authorities(new SimpleGrantedAuthority("SCOPE_albuns:write"))
            )
        ).andExpect(status().isBadRequest());

        // validação
        Album encontrado = repository.findByIdWithFigurinhas(ALBUM.getId());
        assertThat(encontrado.getFigurinhas()).hasSize(1)
                                              .extracting("descricao")
                                              .doesNotContain("gohan");
    }

    @Test
    public void naoDeveAdicionarNovaFigurinhaNoAlbum_quandoTokenNaoEnviado() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest novaFigurinha = new NovaFigurinhaNoAlbumRequest(
            "gohan", "http://animes.com/dbz/gohan.jpg"
        );

        // ação
        mockMvc.perform(POST(uri(ALBUM.getId()), novaFigurinha))
               .andExpect(status().isUnauthorized());
    }

    @Test
    public void naoDeveAdicionarNovaFigurinhaNoAlbum_quandoTokenNaoPossuiEscopoApropriado() throws Exception {
        // cenario
        NovaFigurinhaNoAlbumRequest novaFigurinha = new NovaFigurinhaNoAlbumRequest(
            "gohan", "http://animes.com/dbz/gohan.jpg"
        );

        // ação
        mockMvc.perform(
            POST(uri(ALBUM.getId()), novaFigurinha).with(
                jwt().jwt(jwt -> jwt.claim("preferred_username", "rponte"))
            )
        ).andExpect(status().isForbidden());
    }

    private String uri(Long albumId) {
        String uri = "/api/albuns/{albumId}/figurinhas".replace("{albumId}", albumId.toString());
        return uri;
    }

}

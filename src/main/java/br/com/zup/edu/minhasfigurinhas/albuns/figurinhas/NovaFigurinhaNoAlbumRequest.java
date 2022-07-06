package br.com.zup.edu.minhasfigurinhas.albuns.figurinhas;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import br.com.zup.edu.minhasfigurinhas.albuns.Figurinha;

public class NovaFigurinhaNoAlbumRequest {

    @NotBlank
    @Size(max = 2000)
    private String descricao;

    @URL
    @NotNull
    private String enderecoDaImagem;

    public NovaFigurinhaNoAlbumRequest() {}

    public NovaFigurinhaNoAlbumRequest(String descricao, String enderecoDaImagem) {
        this.descricao = descricao;
        this.enderecoDaImagem = enderecoDaImagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getEnderecoDaImagem() {
        return enderecoDaImagem;
    }

    public Figurinha toModel() {
        return new Figurinha(descricao, enderecoDaImagem);
    }

}

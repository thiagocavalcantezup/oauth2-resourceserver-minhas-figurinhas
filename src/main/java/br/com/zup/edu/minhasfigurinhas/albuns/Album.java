package br.com.zup.edu.minhasfigurinhas.albuns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

@Entity
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String dono;

    @OneToMany(mappedBy = "album", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Figurinha> figurinhas = new ArrayList<>();

    @Deprecated
    public Album() {}

    public Album(String titulo, String descricao, String dono) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dono = dono;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDono() {
        return dono;
    }

    public List<Figurinha> getFigurinhas() {
        return figurinhas;
    }

    /**
     * Adiciona nova figurinha ao album
     */
    public void adiciona(Figurinha figurinha) {
        figurinha.setAlbum(this);
        this.figurinhas.add(figurinha);
    }

    /**
     * Adiciona novas figurinhas ao album
     */
    public void adiciona(Figurinha... figurinhas) {
        Arrays.stream(figurinhas).forEach(figurinha -> {
            adiciona(figurinha);
        });
    }

}

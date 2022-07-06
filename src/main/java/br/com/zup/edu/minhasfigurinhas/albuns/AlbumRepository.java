package br.com.zup.edu.minhasfigurinhas.albuns;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Query("SELECT a FROM Album a JOIN FETCH a.figurinhas WHERE a.id = :id")
    public Album findByIdWithFigurinhas(Long id);

}

package com.sabado.filme.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sabado.filme.model.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

}

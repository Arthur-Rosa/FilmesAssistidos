package com.sabado.filme.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Filme {

	@Id
	@GeneratedValue
	private Long id;
	private String nome;
	private Date data;
	private String imagem;
	
}

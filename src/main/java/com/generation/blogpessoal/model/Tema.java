package com.generation.blogpessoal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "td_tema") // CREATE TABLE tb_tema;
public class Tema {
	
	@Id //Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
	private Long id; 
	
	
	@Column(length = 1000)
	@NotBlank(message = "O atributo 'descrição' é obrigatório")
	@Pattern(regexp = "^[^0-9].*", message = "A descrição não pode ser apenas numérico")
	@Size(min = 10, max = 1000, message = "O atributo 'título' deve ter entre 5 e 100 caracteres")
	private String descricao;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void getDescricao(String descricao) {
		this.descricao = descricao;
	}

}


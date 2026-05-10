package com.pji.triagem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sintoma")
public class Sintoma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao_curta", length = 255)
    private String descricaoCurta;

    @Column(name = "icone_ref", length = 40)
    private String iconeRef;

    @Column(name = "cor_hex", length = 7)
    private String corHex;

    @Column(nullable = false)
    private Integer ordem;

    protected Sintoma() {
    }

    public Sintoma(String codigo, String nome, String descricaoCurta, String iconeRef, String corHex, Integer ordem) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricaoCurta = descricaoCurta;
        this.iconeRef = iconeRef;
        this.corHex = corHex;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public String getIconeRef() {
        return iconeRef;
    }

    public String getCorHex() {
        return corHex;
    }

    public Integer getOrdem() {
        return ordem;
    }
}

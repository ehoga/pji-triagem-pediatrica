package com.pji.triagem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "opcao_pergunta",
        uniqueConstraints = @UniqueConstraint(name = "uq_opcao_codigo_pergunta", columnNames = {"pergunta_id", "codigo"})
)
public class OpcaoPergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    @Column(nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "red_flag", nullable = false)
    private Boolean redFlag = false;

    @Column(nullable = false)
    private Integer ordem;

    protected OpcaoPergunta() {
    }

    public OpcaoPergunta(String codigo, String texto, Integer score, Boolean redFlag, Integer ordem) {
        this.codigo = codigo;
        this.texto = texto;
        this.score = score;
        this.redFlag = redFlag;
        this.ordem = ordem;
    }

    void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }

    public Long getId() {
        return id;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getRedFlag() {
        return redFlag;
    }

    public Integer getOrdem() {
        return ordem;
    }
}

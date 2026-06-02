package com.pji.triagem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pergunta",
        uniqueConstraints = @UniqueConstraint(name = "uq_pergunta_codigo_sintoma", columnNames = {"sintoma_id", "codigo"})
)
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sintoma_id", nullable = false)
    private Sintoma sintoma;

    @Column(nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(length = 255)
    private String sub;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPergunta tipo;

    @Column(nullable = false)
    private Integer ordem;

    @OneToMany(mappedBy = "pergunta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcaoPergunta> opcoes = new ArrayList<>();

    @OneToOne(mappedBy = "pergunta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PesoYesNo pesoYesNo;

    protected Pergunta() {
    }

    public Pergunta(Sintoma sintoma, String codigo, String texto, String sub, TipoPergunta tipo, Integer ordem) {
        this.sintoma = sintoma;
        this.codigo = codigo;
        this.texto = texto;
        this.sub = sub;
        this.tipo = tipo;
        this.ordem = ordem;
    }

    public void adicionarOpcao(OpcaoPergunta opcao) {
        opcao.setPergunta(this);
        opcoes.add(opcao);
    }

    public void definirPesoYesNo(PesoYesNo pesoYesNo) {
        pesoYesNo.setPergunta(this);
        this.pesoYesNo = pesoYesNo;
    }

    public Long getId() {
        return id;
    }

    public Sintoma getSintoma() {
        return sintoma;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }

    public String getSub() {
        return sub;
    }

    public TipoPergunta getTipo() {
        return tipo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public List<OpcaoPergunta> getOpcoes() {
        return opcoes;
    }

    public PesoYesNo getPesoYesNo() {
        return pesoYesNo;
    }
}

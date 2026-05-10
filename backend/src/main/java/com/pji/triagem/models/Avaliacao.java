package com.pji.triagem.models;

import com.pji.triagem.enums.Classificacao;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crianca_id", nullable = false)
    private Long criancaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sintoma_id", nullable = false)
    private Sintoma sintoma;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "classificacao")
    private Classificacao classificacao;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "red_flag_detected", nullable = false)
    private Boolean redFlagDetected;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<RespostaAvaliacao> respostas;

    @Column(name = "protocolo_versao", nullable = false, length = 20)
    private String protocoloVersao = "1.0.0";

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected Avaliacao() {
    }

    public Avaliacao(
            Long criancaId,
            Sintoma sintoma,
            Classificacao classificacao,
            Integer score,
            Boolean redFlagDetected,
            List<RespostaAvaliacao> respostas
    ) {
        this.criancaId = criancaId;
        this.sintoma = sintoma;
        this.classificacao = classificacao;
        this.score = score;
        this.redFlagDetected = redFlagDetected;
        this.respostas = respostas;
    }

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getCriancaId() {
        return criancaId;
    }

    public Sintoma getSintoma() {
        return sintoma;
    }

    public Classificacao getClassificacao() {
        return classificacao;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getRedFlagDetected() {
        return redFlagDetected;
    }

    public List<RespostaAvaliacao> getRespostas() {
        return respostas;
    }

    public String getProtocoloVersao() {
        return protocoloVersao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}

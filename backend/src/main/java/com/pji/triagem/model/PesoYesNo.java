package com.pji.triagem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "peso_yesno")
public class PesoYesNo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pergunta_id", nullable = false, unique = true)
    private Pergunta pergunta;

    @Column(name = "score_yes", nullable = false)
    private Integer scoreYes;

    @Column(name = "score_no", nullable = false)
    private Integer scoreNo;

    @Column(name = "score_dunno", nullable = false)
    private Integer scoreDunno;

    @Column(name = "red_flag_on_yes", nullable = false)
    private Boolean redFlagOnYes = false;

    @Column(name = "red_flag_on_no", nullable = false)
    private Boolean redFlagOnNo = false;

    protected PesoYesNo() {
    }

    public PesoYesNo(Integer scoreYes, Integer scoreNo, Integer scoreDunno, Boolean redFlagOnYes, Boolean redFlagOnNo) {
        this.scoreYes = scoreYes;
        this.scoreNo = scoreNo;
        this.scoreDunno = scoreDunno;
        this.redFlagOnYes = redFlagOnYes;
        this.redFlagOnNo = redFlagOnNo;
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

    public Integer getScoreYes() {
        return scoreYes;
    }

    public Integer getScoreNo() {
        return scoreNo;
    }

    public Integer getScoreDunno() {
        return scoreDunno;
    }

    public Boolean getRedFlagOnYes() {
        return redFlagOnYes;
    }

    public Boolean getRedFlagOnNo() {
        return redFlagOnNo;
    }
}

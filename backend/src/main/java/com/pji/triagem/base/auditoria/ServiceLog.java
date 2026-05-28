package com.pji.triagem.base.auditoria;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.util.UUID;

//@Entity
//@Table(name = "revinfo", schema = "posicard")
//@SequenceGenerator(name = "revinfo_seq", sequenceName = "revinfo_seq", allocationSize = 1)
//@Getter
//@Setter
//@ToString
//@QueryExclude
//@NoArgsConstructor
//@RevisionEntity(CustomRevisionListener.class)
public class ServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
    @RevisionNumber
    @Column(name = "id")
    private Long rev;

    @RevisionTimestamp
    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "id_usuario")
    private UUID userId;

    @Column(name = "ip_address")
    private String ipAddress;

}
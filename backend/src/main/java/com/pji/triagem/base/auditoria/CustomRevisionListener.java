package com.pji.triagem.base.auditoria;

import org.hibernate.envers.RevisionListener;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object o) {
        ServiceLog revision = (ServiceLog) o;
    }
}
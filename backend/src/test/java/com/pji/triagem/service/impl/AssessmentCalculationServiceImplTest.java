package com.pji.triagem.service.impl;

import com.pji.triagem.model.Classification;
import com.pji.triagem.model.YesNoAnswer;
import com.pji.triagem.model.YesNoWeight;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentCalculationServiceImplTest {

    private final AssessmentCalculationServiceImpl service = new AssessmentCalculationServiceImpl();

    @Test
    void classifySymptomUsesRedFlagAsHighPriority() {
        assertEquals(Classification.HIGH, service.classifySymptom(0, true));
    }

    @Test
    void classifySymptomByScoreRanges() {
        assertEquals(Classification.LOW, service.classifySymptom(4, false));
        assertEquals(Classification.MOD, service.classifySymptom(9, false));
        assertEquals(Classification.HIGH, service.classifySymptom(10, false));
    }

    @Test
    void classifyFinalUsesHighestSeverity() {
        assertEquals(Classification.HIGH, service.classifyFinal(List.of(Classification.LOW, Classification.HIGH)));
        assertEquals(Classification.MOD, service.classifyFinal(List.of(Classification.LOW, Classification.MOD)));
        assertEquals(Classification.LOW, service.classifyFinal(List.of(Classification.LOW)));
    }

    @Test
    void scoreAndRedFlagYesNoUseConfiguredWeights() {
        YesNoWeight weight = new YesNoWeight();
        weight.setYesScore(8);
        weight.setNoScore(0);
        weight.setUnknownScore(2);
        weight.setRedFlagOnYes(true);
        weight.setRedFlagOnNo(false);

        assertEquals(8, service.scoreYesNo(YesNoAnswer.YES, weight));
        assertEquals(0, service.scoreYesNo(YesNoAnswer.NO, weight));
        assertEquals(2, service.scoreYesNo(YesNoAnswer.DUNNO, weight));
        assertTrue(service.redFlagYesNo(YesNoAnswer.YES, weight));
        assertFalse(service.redFlagYesNo(YesNoAnswer.NO, weight));
        assertFalse(service.redFlagYesNo(YesNoAnswer.DUNNO, weight));
    }
}

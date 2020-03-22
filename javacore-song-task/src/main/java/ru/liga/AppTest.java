package ru.liga;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.analizismidi.*;
import ru.liga.songtask.domain.Content;
import ru.liga.songtask.domain.SimpleMidiFile;

public class AppTest {
    SimpleMidiFile simpleMidiFile;
    AnalysisRange analysisRange;
    AnalysisHeight analysisHeight;
    AnalysisDuration analysisDuration;

    @Before
    public void setup() {
        simpleMidiFile = new SimpleMidiFile(Content.ZOMBIE);
        analysisRange = new AnalysisRange(simpleMidiFile);
        analysisHeight = new AnalysisHeight(simpleMidiFile);
        analysisDuration = new AnalysisDuration(simpleMidiFile);
    }

    @Test
    public void Right_AnalysisRange() {
        Assertions.assertThat(analysisRange.GetAnalysis().get(0)).isEqualTo("A5");
        Assertions.assertThat(analysisRange.GetAnalysis().get(1)).isEqualTo("E4");
        Assertions.assertThat(analysisRange.GetAnalysis().get(2)).isEqualTo("17");
    }

    @Test
    public void Right_AnalysisHeight() {
        Assertions.assertThat(analysisHeight.GetAnalysis().get("E5")).isEqualTo(57);
        Assertions.assertThat(analysisHeight.GetAnalysis().get("C5")).isEqualTo(12);
    }

    @Test
    public void Right_AnalysisDuration() {
        Assertions.assertThat(analysisDuration.GetAnalysis().get(731)).isEqualTo(92);
    }
}

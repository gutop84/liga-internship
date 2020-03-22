package ru.liga;

import com.leff.midi.MidiFile;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.analizismidi.AnalysisRange;
import ru.liga.songtask.analizismidi.AnalysisDuration;
import ru.liga.songtask.analizismidi.AnalysisHeight;
import ru.liga.songtask.domain.Content;
import ru.liga.songtask.domain.SimpleMidiFile;
import ru.liga.songtask.util.ChangeClass;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AppTestNew {
    SimpleMidiFile simpleMidiFileZ;
    SimpleMidiFile simpleMidiFileUYC;
    SimpleMidiFile simpleMidiFileWB;
    SimpleMidiFile simpleMidiFileB;
    AnalysisRange analysisRange;
    AnalysisHeight analysisHeight;
    AnalysisDuration analysisDuration;

    @Before
    public void setup() throws IOException {
        simpleMidiFileZ = new SimpleMidiFile(Content.ZOMBIE);
        simpleMidiFileUYC = new SimpleMidiFile(Content.UNDERNEATH_YOUR_CLOTHES);
        simpleMidiFileWB = new SimpleMidiFile(Content.WRECKING_BALL);
        simpleMidiFileB = new SimpleMidiFile(Content.BELLE);
    }

    @Test
    public void OpeningWrongFile() {
        Assertions.assertThatThrownBy(()->simpleMidiFileZ = new SimpleMidiFile("FILE.NAME"));
    }

    @Test
    public void TestingUNDERNEATH_YOUR_CLOTHES() {
        analysisRange = new AnalysisRange( simpleMidiFileUYC);
        analysisDuration = new AnalysisDuration( simpleMidiFileUYC);
        analysisHeight = new AnalysisHeight( simpleMidiFileUYC);
        List<String> analysisDiapozUYC= analysisRange.GetAnalysis();
        Map<Integer,Integer> analysisDurationUYC= analysisDuration.GetAnalysis();
        Map<String,Integer> analysisHeightUYC=analysisHeight.GetAnalysis();
        Assertions.assertThat(analysisDiapozUYC.get(0)).isEqualTo("C5");
        Assertions.assertThat(analysisDiapozUYC.get(1)).isEqualTo("G#3");
        Assertions.assertThat(analysisDiapozUYC.get(2)).isEqualTo("16");
        Assertions.assertThat(analysisDurationUYC.get(1077)).isEqualTo(1);
        Assertions.assertThat(analysisDurationUYC.get(708)).isEqualTo(17);
        Assertions.assertThat(analysisHeightUYC.get("C5")).isEqualTo(28);
        Assertions.assertThat(analysisHeightUYC.get("H3")).isEqualTo(20);
    }

    @Test
    public void TestingRightTrackDefining() {
        Assertions.assertThat(simpleMidiFileUYC.getVoiceTrackIndex()).isEqualTo(2);
        Assertions.assertThat(simpleMidiFileWB.getVoiceTrackIndex()).isEqualTo(9);
        Assertions.assertThat(simpleMidiFileWB.getVoiceTrackIndex()).isEqualTo(9);
    }

    @Test
    public void TestingRightBPMDefining() {
        Assertions.assertThat(simpleMidiFileUYC.getBpm()).isEqualTo(83.99997f);
        Assertions.assertThat(simpleMidiFileWB.getBpm()).isEqualTo(120.0f);
        Assertions.assertThat(simpleMidiFileB.getBpm()).isEqualTo(83.99997f);
    }

    @Test
    public void NewNoteOutOfBoundsException() {
        Assertions.assertThatThrownBy(()->ChangeClass.transposeMidi(simpleMidiFileWB.getMidiFile(),100));
        Assertions.assertThatThrownBy(()->ChangeClass.transposeMidi(simpleMidiFileWB.getMidiFile(),-50));
        Assertions.assertThat(ChangeClass.transposeMidi(simpleMidiFileWB.getMidiFile(),-2)).isInstanceOf(MidiFile.class);
    }
}
package ru.liga.songtask.analizismidi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.util.ArrayList;
import java.util.List;

import static ru.liga.App.eventsToNotes;

public class AnalysisRange {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public SimpleMidiFile midiFile;
    ArrayList<String> analysisResult;

    public AnalysisRange(SimpleMidiFile mid_file) {
        this.midiFile = mid_file;
    }

    public void doAnalysis() {
        analysisResult = new ArrayList<String>();
        List<Note> notes = eventsToNotes(midiFile.getMidiFile().getTracks().get(midiFile.getVoiceTrackIndex()).getEvents());
        Note range_bottom, range_top;
        range_bottom = range_top = notes.get(0);
        for (Note n : notes) {
            if (n.sign().getMidi() < range_bottom.sign().getMidi())
                range_bottom = n;
            if (n.sign().getMidi() > range_top.sign().getMidi())
                range_top = n;
        }
        analysisResult.add(range_top.sign().fullName());
        analysisResult.add(range_bottom.sign().fullName());
        analysisResult.add(Integer.toString((range_top.sign().getMidi() - range_bottom.sign().getMidi())));
    }

    public ArrayList<String> getAnalysis() {
        return analysisResult;
    }

    public void showAnalysisResult() {
        logger.info("Range:");
        logger.info("top: " + analysisResult.get(0));
        logger.info("bottom: " + analysisResult.get(1));
        logger.info("range: " + analysisResult.get(2));
    }
}

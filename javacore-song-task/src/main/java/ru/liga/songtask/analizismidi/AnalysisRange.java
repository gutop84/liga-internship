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

    public AnalysisRange(SimpleMidiFile mid_file) {
        this.midiFile = mid_file;
    }

    public List<String> GetAnalysis() {
        ArrayList<String> analysisResult = new ArrayList<String>();
        List<Note> notes = eventsToNotes(midiFile.getMidiFile().getTracks().get(midiFile.getVoiceTrackIndex()).getEvents());
        Note range_bottom, range_top;
        range_bottom = range_top = notes.get(0);
        for (Note n : notes) {
            if (n.sign().getMidi() < range_bottom.sign().getMidi())
                range_bottom = n;
            if (n.sign().getMidi() > range_top.sign().getMidi())
                range_top = n;
        }
        logger.info("Range:");
        logger.info("top: " + range_top.sign().fullName());
        logger.info("bottom: " + range_bottom.sign().fullName());
        logger.info("range: " + (range_top.sign().getMidi() - range_bottom.sign().getMidi()));
        analysisResult.add(range_top.sign().fullName());
        analysisResult.add(range_bottom.sign().fullName());
        analysisResult.add(Integer.toString((range_top.sign().getMidi() - range_bottom.sign().getMidi())));
        return analysisResult;
    }
}

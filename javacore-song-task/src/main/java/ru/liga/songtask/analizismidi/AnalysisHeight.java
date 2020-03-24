package ru.liga.songtask.analizismidi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.domain.SimpleMidiFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.liga.App.eventsToNotes;

public class AnalysisHeight {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    SimpleMidiFile midiFile;
    Map<NoteSign, Integer> analysisResult;

    public AnalysisHeight(SimpleMidiFile mid_file) {
        this.midiFile = mid_file;
    }

    public void doAnalysis() {
        analysisResult = new HashMap<NoteSign, Integer>();
        List<Note> notes = eventsToNotes(midiFile.getMidiFile().getTracks().get(midiFile.getVoiceTrackIndex()).getEvents());
        for (Note n : notes) {
            if (analysisResult.containsKey(n.sign())) {
                int i = analysisResult.get(n.sign());
                analysisResult.put(n.sign(), i + 1);
            } else
                analysisResult.put(n.sign(), 1);
        }
    }

    public Map<NoteSign, Integer> getAnalysis() {
        return analysisResult;
    }

    public void showAnalysisResult() {
        logger.info("List of notes:");
        analysisResult.
                entrySet().
                stream().
                sorted((a1, a2) -> a2.getKey().getMidi() - a1.getKey().getMidi()).
                forEach((k) -> logger.info(k.getKey().fullName() + ": " + k.getValue()));
    }
}

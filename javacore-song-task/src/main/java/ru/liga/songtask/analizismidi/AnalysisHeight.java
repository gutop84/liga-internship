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

    public AnalysisHeight(SimpleMidiFile mid_file) {
        this.midiFile = mid_file;
    }

    public Map<String, Integer> GetAnalysis() {
        Map<String, Integer> analysisResult = new HashMap<String, Integer>();
        Map<NoteSign, Integer> analysisResultBuf = new HashMap<NoteSign, Integer>();
        List<Note> notes = eventsToNotes(midiFile.getMidiFile().getTracks().get(midiFile.getVoiceTrackIndex()).getEvents());
        for (Note n : notes) {
            if (analysisResultBuf.containsKey(n.sign())) {
                int i = analysisResultBuf.get(n.sign());
                analysisResultBuf.put(n.sign(), i + 1);
            } else
                analysisResultBuf.put(n.sign(), 1);
        }
        for (Map.Entry<NoteSign, Integer> entry : analysisResultBuf.entrySet())
            analysisResult.put(entry.getKey().fullName(), entry.getValue());
        logger.info("List of notes:");
        analysisResultBuf.
                entrySet().
                stream().
                sorted((a1, a2) -> a2.getKey().getMidi() - a1.getKey().getMidi()).
                forEach((k) -> logger.info(k.getKey().fullName() + ": " + k.getValue()));
        return analysisResult;
    }
}

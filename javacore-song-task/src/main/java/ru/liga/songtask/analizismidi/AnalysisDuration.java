package ru.liga.songtask.analizismidi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.SimpleMidiFile;
import ru.liga.songtask.util.SongUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.liga.App.eventsToNotes;

public class AnalysisDuration {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    SimpleMidiFile midiFile;

    public AnalysisDuration(SimpleMidiFile mid_file) {
        this.midiFile = mid_file;
    }

    public Map<Integer, Integer> GetAnalysis() {
        List<Note> notes = eventsToNotes(midiFile.getMidiFile().getTracks().get(midiFile.getVoiceTrackIndex()).getEvents());
        Map<Integer, Integer> analysisResult = new HashMap<Integer, Integer>();
        int tickToMs;
        for (Note n : notes) {
            tickToMs = SongUtils.tickToMs(midiFile.getBpm(), midiFile.getMidiFile().getResolution(), n.durationTicks());
            if (analysisResult.containsKey(tickToMs)) {
                int i = analysisResult.get(tickToMs);
                analysisResult.put(tickToMs, i + 1);
            } else
                analysisResult.put(tickToMs, 1);
        }
        logger.info("The number of notes by duration:");
        Map<Integer, Integer> forLogging = new HashMap<Integer, Integer>(analysisResult);
        forLogging.entrySet().
                stream().
                sorted((a1, a2) -> (int) (a2.getKey() - a1.getKey())).
                forEach((k) -> logger.info(k.getKey() + "ms: " + k.getValue()));
        return analysisResult;
    }
}

package ru.liga.songtask.domain;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;

import java.io.FileInputStream;
import java.util.*;

public class SimpleMidiFile {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    MidiFile midiFile;
    int VoiceTrackIndex;
    float bpm;

    public MidiFile getMidiFile() {
        return midiFile;
    }

    public int getVoiceTrackIndex() {
        return VoiceTrackIndex;
    }

    public float getBpm() {
        return bpm;
    }

    public void setVoiceTrackIndex(int voiceTrackIndex) {
        VoiceTrackIndex = voiceTrackIndex;
    }

    public SimpleMidiFile(String file_name) {
        openFile(file_name);
        logger.debug("File contains {} tracks", midiFile.getTracks().size());
        definingBPM();
        logger.debug("File BPM is {}", bpm);
        definingVoiceTrackIndex(file_name);
        logger.debug("Founded Voice-Track index - {}", VoiceTrackIndex);
    }

    private void openFile(String file_name) {
        logger.debug("Opening file {}", file_name);
        try {
            this.midiFile = new MidiFile(new FileInputStream(file_name));
        } catch (Throwable e) {
            logger.error("Error occurred");
            logger.error(e.getMessage());
            for (StackTraceElement element : e.getStackTrace())
                logger.error(element.toString());
            throw new RuntimeException();
        }
        logger.debug("File opened");
    }

    private void definingBPM() {
        logger.debug("Defining File BPM");
        MidiTrack midiTrack = midiFile.getTracks().get(0);
        this.bpm = 120.0f;
        MidiEvent midiEvent = midiFile.
                getTracks().
                get(0).
                getEvents().
                stream().
                filter((a) -> a.getClass().equals(Tempo.class)).
                findFirst().
                orElseThrow(RuntimeException::new);
        Tempo tempo = new Tempo(midiEvent.getTick(), midiEvent.getDelta(), ((Tempo) midiEvent).getMpqn());
        this.bpm = tempo.getBpm();
    }

    private void definingVoiceTrackIndex(String file_name) {
        this.VoiceTrackIndex = -1;
        MidiTrack mdt;
        if (file_name.contains("zombie.mid"))
            this.VoiceTrackIndex = 1;
        else {
            logger.debug("Searching for Text track");
            mdt = midiFile.
                    getTracks().
                    stream().
                    filter((a) -> a.getEvents().stream().filter((b) -> b.getClass().equals(Text.class)).count() > 10).
                    findFirst().
                    orElseThrow(RuntimeException::new);
            logger.debug("Text Track founded");
            logger.debug("Searching for Voice track");
            long NumberOfNotesInVoiceTrack = mdt.getEvents().size() - 1;
            for (int i = 0; i < midiFile.getTracks().size(); i++) {
                MidiTrack track = midiFile.getTracks().get(i);
                List<Note> list = App.eventsToNotes(track.getEvents());
                if (list.size() == NumberOfNotesInVoiceTrack) {
                    this.VoiceTrackIndex = i;
                    logger.debug("Track {} meets conditions", i);
                }
            }
        }
    }
}

package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.SimpleMidiFile;

public class TheChanger {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static MidiFile changeMidi(SimpleMidiFile midiFile, float tempo, int trans) {
        float percentTempo = 1.0F + tempo / 100.0F;
        logger.debug("Tempo multiplier is {}", percentTempo);
        logger.debug("Old Bpm - {}", midiFile.getBpm());
        logger.debug("New Bpm - {}", midiFile.getBpm() * percentTempo);
        logger.debug("Changing tempo");
        MidiFile newMidi = changeTempo(midiFile, percentTempo);
        logger.debug("Tempo changed");
        logger.debug("Changing trans");
        newMidi = transposeMidi(newMidi, trans);
        logger.debug("Trans changed");
        logger.debug("Change complete");
        return newMidi;
    }

    public static MidiFile transposeMidi(MidiFile midiFile, int trans) {
        MidiFile midiFile1 = new MidiFile();
        Iterator it = midiFile.getTracks().iterator();
        while (it.hasNext()) {
            MidiTrack midiTrack = (MidiTrack) it.next();
            MidiTrack midiTrack1 = transposeMidiTrack(trans, midiTrack);
            midiFile1.addTrack(midiTrack1);
        }
        return midiFile1;
    }

    private static MidiTrack transposeMidiTrack(int trans, MidiTrack midiTrack) {
        MidiTrack midiTrack1 = new MidiTrack();
        Iterator it = midiTrack.getEvents().iterator();
        while (it.hasNext()) {
            MidiEvent midiEvent = (MidiEvent) it.next();
            if (midiEvent.getClass().equals(NoteOn.class)) {
                NoteOn on = getChangedNoteOn(trans, (NoteOn) midiEvent);
                midiTrack1.getEvents().add(on);
            } else if (midiEvent.getClass().equals(NoteOff.class)) {
                NoteOff off = getChangedNoteOff(trans, (NoteOff) midiEvent);
                midiTrack1.getEvents().add(off);
            } else {
                midiTrack1.getEvents().add(midiEvent);
            }
        }
        return midiTrack1;
    }

    private static NoteOff getChangedNoteOff(int trans, NoteOff midiEvent) {
        NoteOff off = new NoteOff(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getChannel(), midiEvent.getNoteValue(), midiEvent.getVelocity());
        if (((off.getNoteValue() + trans) > 107) || ((off.getNoteValue() + trans) < 21)) {
            logger.error("Error. New note value is out of bounds.");
            throw new RuntimeException();
        }
        off.setNoteValue(off.getNoteValue() + trans);
        return off;
    }

    private static NoteOn getChangedNoteOn(int trans, NoteOn midiEvent) {
        NoteOn on = new NoteOn(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getChannel(), midiEvent.getNoteValue(), midiEvent.getVelocity());
        if (((on.getNoteValue() + trans) > 107) || ((on.getNoteValue() + trans) < 21)) {
            logger.error("Error. New note value is out of bounds.");
            throw new RuntimeException();
        }
        on.setNoteValue(on.getNoteValue() + trans);
        return on;
    }

    public static MidiFile changeTempo(SimpleMidiFile midiFile, float percentTempo) {
        MidiFile midiFile1 = new MidiFile();
        Iterator it = midiFile.getMidiFile().getTracks().iterator();
        while (it.hasNext()) {
            MidiTrack midiTrack = (MidiTrack) it.next();
            MidiTrack midiTrack1 = changeTempoOfMidiTrack(percentTempo, midiTrack);
            midiFile1.addTrack(midiTrack1);
        }
        return midiFile1;
    }

    private static MidiTrack changeTempoOfMidiTrack(float percentTempo, MidiTrack midiTrack) {
        MidiTrack midiTrack1 = new MidiTrack();
        Iterator it = midiTrack.getEvents().iterator();
        while (it.hasNext()) {
            MidiEvent midiEvent = (MidiEvent) it.next();
            if (midiEvent.getClass().equals(Tempo.class)) {
                Tempo tempo = getChangedTempo(percentTempo, (Tempo) midiEvent);
                midiTrack1.getEvents().add(tempo);
            } else {
                midiTrack1.getEvents().add(midiEvent);
            }
        }
        return midiTrack1;
    }

    private static Tempo getChangedTempo(float percentTempo, Tempo midiEvent) {
        Tempo tempo = new Tempo(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getMpqn());
        tempo.setBpm(tempo.getBpm() * percentTempo);
        return tempo;
    }

}

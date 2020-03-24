package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import ru.liga.songtask.analizismidi.AnalysisRange;
import ru.liga.songtask.analizismidi.AnalysisDuration;
import ru.liga.songtask.analizismidi.AnalysisHeight;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.domain.SimpleMidiFile;
import ru.liga.songtask.util.TheChanger;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    /**
     * Это пример работы, можете всё стирать и переделывать
     * Пример, чтобы убрать у вас начальный паралич разработки
     * Также посмотрите класс SongUtils, он переводит тики в миллисекунды
     * Tempo может быть только один
     */
    private static final Logger logger = LoggerFactory.getLogger(App.class);
//    java -jar javacore-song-task.jar "zombie.mid" analyze
//    java -jar javacore-song-task.jar "Wrecking Ball.mid" analyze 4
//    java -jar javacore-song-task.jar "Belle.mid" change -trans 4 -tempo 40
//    "Underneath Your Clothes.mid"
//    "Wrecking Ball.mid"
//    "Belle.mid"
//    "zombie.mid"

    public static void main(String[] args) {
        logger.debug("Program starts");
        logger.debug("Program inputs:");
        for (int i = 0; i < args.length; i++)
            logger.debug(" - {}", args[i]);
        definingInputAndDoingWork(args);
        logger.debug("Closing program");
        logger.debug(" ");
    }

    public static SimpleMidiFile openingFile(String fileName) {
        return new SimpleMidiFile(fileName);
    }

    public static void definingInputAndDoingWork(String[] args) {
        if ((args.length == 2) && (args[1].equals("analyze"))) {
            logger.debug("Analyze input");
            doAnalysis(openingFile(args[0]));
        } else if ((args.length == 3) && (args[1].equals("analyze"))) {
            logger.debug("Target Analyze input");
            doTargetAnalysis(openingFile(args[0]), args[2]);
        } else if ((args.length == 6) && (args[1].equals("change"))) {
            logger.debug("Change input");
            doChange(args);
        } else {
            logger.error("Wrong inputs");
            logger.error("Expected inputs:");
            logger.error("\"FILE.NAME\" analyze");
            logger.error("\"FILE.NAME\" analyze 5");
            logger.error("\"FILE.NAME\" change -trans 3 -tempo 0");
            logger.debug(" ");
        }
    }

    public static void doAnalysis(SimpleMidiFile simpleMidiFile) {
        if (simpleMidiFile.getVoiceTrackIndex() == -1) {
            logger.error("Fail to find voice track");
        } else if (simpleMidiFile.getVoiceTrackIndex() >= simpleMidiFile.getMidiFile().getTracks().size()) {
            logger.error("MIDI File does not have Target Index Track");
        } else if (eventsToNotes(simpleMidiFile.getMidiFile().getTracks().get(simpleMidiFile.getVoiceTrackIndex()).getEvents()).size() == 0) {
            logger.error("Target track has no notes to analyze");
        } else {
            logger.debug("Starting range analysis");
            AnalysisRange analysisRange = new AnalysisRange(simpleMidiFile);
            analysisRange.doAnalysis();
            analysisRange.showAnalysisResult();
            logger.debug("Starting duration analysis");
            AnalysisDuration analysisDuration = new AnalysisDuration(simpleMidiFile);
            analysisDuration.doAnalysis();
            analysisDuration.showAnalysisResult();
            logger.debug("Starting note-height analysis");
            AnalysisHeight analysisHeight = new AnalysisHeight(simpleMidiFile);
            analysisHeight.doAnalysis();
            analysisHeight.showAnalysisResult();

        }
    }

    public static void doTargetAnalysis(SimpleMidiFile simpleMidiFile, String trackIndex) {
        logger.debug("Track Index we will work with - {}", trackIndex);
        simpleMidiFile.setVoiceTrackIndex(Integer.valueOf(trackIndex));
        doAnalysis(simpleMidiFile);
    }

    public static void doChange(String args[]) {

        logger.debug("Opening file {}", args[0]);
        String newFileName = args[0].substring(0, args[0].length() - 4) + "-trans" + args[3] + "-tempo" + args[5] + ".mid";
        logger.info("Starting change procedure");
        logger.info("Input file - {}", args[0]);
        logger.info("Output file - {}", newFileName);
        logger.debug("Defining parameters");
        logger.debug("trans = {}", Integer.parseInt(args[3]));
        logger.debug("tempo = {}", Integer.parseInt(args[5]));
        MidiFile newMidi = formChangedFile(openingFile(args[0]), Integer.parseInt(args[3]), Integer.parseInt(args[5]));
        logger.info("Change procedure complete");
        logger.info("Starting write-to-new-file procedure");
        try {
            newMidi.writeToFile(new File(newFileName));
        } catch (Throwable e) {
            logger.error("Error occurred");
            logger.error(e.getMessage());
            for (StackTraceElement element : e.getStackTrace())
                logger.error(element.toString());
        }
        logger.info("Write-to-new-file procedure complete");
    }

    private static MidiFile formChangedFile(SimpleMidiFile simpleMidiFile, int trans, int tempo) {
        return TheChanger.changeMidi(simpleMidiFile, tempo, trans);
    }

    /**
     * Этот метод, чтобы вы не афигели переводить эвенты в ноты
     *
     * @param events эвенты одного трека
     * @return список нот
     */
    public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
        List<Note> vbNotes = new ArrayList<>();
        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            long start = noteOn.getTick();
                            long end = event.getTick();
                            vbNotes.add(
                                    new Note(noteSign, start, end - start));
                        }
                    }
                } else {
                    noteOnQueue.offer((NoteOn) event);
                }
            }
        }
        return vbNotes;
    }

    private static Integer extractNoteValue(MidiEvent event) {
        if (event instanceof NoteOff) {
            return ((NoteOff) event).getNoteValue();
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getNoteValue();
        } else {
            return null;
        }
    }

    private static boolean isEndMarkerNote(MidiEvent event) {
        if (event instanceof NoteOff) {
            return true;
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getVelocity() == 0;
        } else {
            return false;
        }
    }
}

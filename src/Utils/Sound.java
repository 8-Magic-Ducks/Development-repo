package Utils;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    private static final String SOUNDS_PATH = "src//Assets//Sounds//";


    private static Clip clickClip;
    private static Clip badMoveClip;
    private static Clip winClip;
    private static Clip failClip;
    private static Clip menuMusicClip;


    static {
        clickClip = loadClip("click.wav");
        badMoveClip = loadClip("bad_move.wav");
        winClip = loadClip("winner.wav");
        failClip = loadClip("fail.wav");
        menuMusicClip = loadClip("game_sound.wav");
    }


    private static Clip loadClip(String filename) {
        try {
            File soundFile = new File(SOUNDS_PATH + filename);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                return clip;
            }
        } catch (Exception e) {
            System.out.println("Error loading sound: " + filename);
        }
        return null;
    }

    private static void playClip(Clip clip) {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }
    public static void playClick() {
        playClip(clickClip);
    }

    public static void playBadMove() {
        playClip(badMoveClip);
    }

    public static void playWin() {
        stopMenuMusic();
        playClip(winClip);
    }

    public static void playFail() {
        stopMenuMusic();
        playClip(failClip);
    }

    public static void playMenuMusic() {
        if (menuMusicClip != null) {
            menuMusicClip.setFramePosition(0);
            menuMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            menuMusicClip.start();
        }
    }

    public static void stopMenuMusic() {
        if (menuMusicClip != null && menuMusicClip.isRunning()) {
            menuMusicClip.stop();
        }
    }
}
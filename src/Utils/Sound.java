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

    private static float volume = 0.5f; // Default volume 50%

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
                setClipVolume(clip, volume); // Set initial volume
                return clip;
            }
        } catch (Exception e) {
            System.out.println("Error loading sound: " + filename);
        }
        return null;
    }

    private static void setClipVolume(Clip clip, float volume) {
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // Convert volume (0.0 to 1.0) to decibels
                float dB;
                if (volume <= 0.0f) {
                    dB = -80.0f; // Mute
                } else {
                    dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                }
                // Clamp the dB value to the control's range
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                dB = Math.max(min, Math.min(max, dB));
                gainControl.setValue(dB);
            } catch (Exception e) {
                System.out.println("Error setting volume for clip: " + e.getMessage());
            }
        }
    }

    // Set volume (0.0 to 1.0)
    public static void setVolume(float newVolume) {
        volume = Math.max(0.0f, Math.min(1.0f, newVolume)); // Clamp between 0 and 1

        // Update volume for all clips
        setClipVolume(clickClip, volume);
        setClipVolume(badMoveClip, volume);
        setClipVolume(winClip, volume);
        setClipVolume(failClip, volume);
        setClipVolume(menuMusicClip, volume);
    }

    public static float getVolume() {
        return volume;
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
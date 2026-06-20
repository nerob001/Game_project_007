import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Sound {
    private Clip clip;

    public Sound(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            } else {
                System.err.println("Sound file not found at: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound: " + filePath);
            e.printStackTrace();
        }
    }


    public void play() {
        if (clip != null) {
            if (clip.isRunning() ) { clip.stop(); }
            clip.setFramePosition(0);
            clip.start();
        }
    }
}

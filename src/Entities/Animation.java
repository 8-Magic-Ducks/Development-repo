package Entities;

import Texture.TextureReader;
import javax.media.opengl.GL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Animation {
    public enum AnimationType {
        IDLE,
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        SELECTED
    }
    private AnimationType currentAnimation;
    private Map<String, TextureReader.Texture> frames;
    private String[] currentFrameSequence;
    private int currentFrameIndex;
    private float frameTimer;
    private float frameDuration;
    private boolean isLooping;
    private boolean isPlaying;
    private boolean isComplete;
    private float startX, startY;
    private float targetX, targetY;
    private float currentX, currentY;
    private float movementProgress;
    private boolean isMoving;
    private static final float DEFAULT_FRAME_DURATION = 0.15f; // 150ms per frame
    private static final float MOVEMENT_DURATION = 0.3f; // 300ms for smooth movement

    public Animation() {
        this.frames = new HashMap<>();
        this.currentAnimation = AnimationType.IDLE;
        this.currentFrameIndex = 0;
        this.frameTimer = 0;
        this.frameDuration = DEFAULT_FRAME_DURATION;
        this.isLooping = true;
        this.isPlaying = false;
        this.isComplete = false;
        this.isMoving = false;
        this.movementProgress = 0;
    }

    public void loadDuckAnimations(String duckFolder) {
        try {
            frames.put("f1", TextureReader.readTexture(duckFolder + "//f1.png", true));
            frames.put("f2", TextureReader.readTexture(duckFolder + "//f2.png", true));


            frames.put("b1", TextureReader.readTexture(duckFolder + "//b1.png", true));
            frames.put("b2", TextureReader.readTexture(duckFolder + "//b2.png", true));


            frames.put("l1", TextureReader.readTexture(duckFolder + "//l1.png", true));
            frames.put("l2", TextureReader.readTexture(duckFolder + "//l2.png", true));


            frames.put("r1", TextureReader.readTexture(duckFolder + "//r1.png", true));
            frames.put("r2", TextureReader.readTexture(duckFolder + "//r2.png", true));

            currentFrameSequence = new String[]{"f1"};

        } catch (IOException e) {
            System.err.println("Error loading duck animations from: " + duckFolder);
            e.printStackTrace();
        }
    }

    public void loadFrame(String key, String filepath) {
        try {
            frames.put(key, TextureReader.readTexture(filepath, true));
        } catch (IOException e) {
            System.err.println("Error loading frame: " + filepath);
            e.printStackTrace();
        }
    }

    public void setAnimationType(String type) {
        AnimationType newType;
        try {
            newType = AnimationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            newType = AnimationType.IDLE;
        }
        setAnimationType(newType);
    }

    public void setAnimationType(AnimationType type) {
        if (this.currentAnimation == type && isPlaying) {
            return;
        }
        this.currentAnimation = type;
        this.currentFrameIndex = 0;
        this.frameTimer = 0;
        this.isComplete = false;
        switch (type) {
            case FORWARD:
                currentFrameSequence = new String[]{"f1", "f2"};
                isLooping = false;
                isPlaying = true;
                break;

            case BACKWARD:
                currentFrameSequence = new String[]{"b1", "b2"};
                isLooping = false;
                isPlaying = true;
                break;

            case LEFT:
                currentFrameSequence = new String[]{"l1", "l2"};
                isLooping = false;
                isPlaying = true;
                break;

            case RIGHT:
                currentFrameSequence = new String[]{"r1", "r2"};
                isLooping = false;
                isPlaying = true;
                break;

            case SELECTED:
                currentFrameSequence = new String[]{"f1", "f2"};
                isLooping = true;
                isPlaying = true;
                frameDuration = 0.25f;
                break;

            case IDLE:
            default:
                currentFrameSequence = new String[]{"f1"};
                isLooping = false;
                isPlaying = false;
                break;
        }
    }

    public void update() {
        update(0.016f);
    }
    public void update(float deltaTime) {
        if (!isPlaying || currentFrameSequence == null || currentFrameSequence.length == 0) {
            return;
        }
        frameTimer += deltaTime;
        if (frameTimer >= frameDuration) {
            frameTimer = 0;
            currentFrameIndex++;
            if (currentFrameIndex >= currentFrameSequence.length) {
                if (isLooping) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = currentFrameSequence.length - 1;
                    isComplete = true;
                    isPlaying = false;
                }
            }
        }
        if (isMoving) {
            movementProgress += deltaTime / MOVEMENT_DURATION;

            if (movementProgress >= 1.0f) {
                movementProgress = 1.0f;
                currentX = targetX;
                currentY = targetY;
                isMoving = false;
            } else {

                float t = smoothStep(movementProgress);
                currentX = lerp(startX, targetX, t);
                currentY = lerp(startY, targetY, t);
            }
        }
    }
    public void moveTo(float targetX, float targetY) {
        this.startX = this.currentX;
        this.startY = this.currentY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.movementProgress = 0;
        this.isMoving = true;

        float dx = targetX - startX;
        float dy = targetY - startY;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                setAnimationType(AnimationType.RIGHT);
            } else {
                setAnimationType(AnimationType.LEFT);
            }
        } else {
            if (dy > 0) {
                setAnimationType(AnimationType.FORWARD);
            } else {
                setAnimationType(AnimationType.BACKWARD);
            }
        }
    }

    public void setPosition(float x, float y) {
        this.currentX = x;
        this.currentY = y;
        this.startX = x;
        this.startY = y;
        this.targetX = x;
        this.targetY = y;
        this.isMoving = false;
    }


    public TextureReader.Texture getCurrentFrame() {
        if (currentFrameSequence == null || currentFrameSequence.length == 0) {
            return null;
        }

        String frameKey = currentFrameSequence[currentFrameIndex];
        return frames.get(frameKey);
    }


    public void bindCurrentFrame(GL gl) {
        TextureReader.Texture texture = getCurrentFrame();
        if (texture != null) {

        }
    }

    private float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }


    private float smoothStep(float t) {
        return t * t * (3.0f - 2.0f * t);
    }


    public boolean isComplete() {
        return isComplete;
    }


    public boolean isMoving() {
        return isMoving;
    }


    public float getProgress() {
        return movementProgress;
    }


    public void stop() {
        isPlaying = false;
        isMoving = false;
    }


    public void reset() {
        currentFrameIndex = 0;
        frameTimer = 0;
        isComplete = false;
        movementProgress = 0;
    }

    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }

    public float getTargetX() { return targetX; }
    public float getTargetY() { return targetY; }

    public void setFrameDuration(float duration) {
        this.frameDuration = duration;
    }

    public void setLooping(boolean looping) {
        this.isLooping = looping;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public AnimationType getCurrentAnimationType() {
        return currentAnimation;
    }

    public Map<String, TextureReader.Texture> getAllFrames() {
        return frames;
    }
}
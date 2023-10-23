package fr.nicolas.wispy.game.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Assets {

    private static final HashMap<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage get(String path) {
        return cache.computeIfAbsent(path, (key) -> {
            try {
                return ImageIO.read(Objects.requireNonNull(Assets.class.getResource("/assets/" + key + ".png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

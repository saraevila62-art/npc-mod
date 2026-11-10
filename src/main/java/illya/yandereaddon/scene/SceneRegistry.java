package illya.yandereaddon.scene;

import com.google.gson.Gson;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SceneRegistry extends SimplePreparableReloadListener<Map<String, SceneFile>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneRegistry.class);
    private static final Gson GSON = new Gson();
    private static final String SCENE_PATH = "lovsplayer/motiondata/featured";

    // Общий реестр — доступен из любого места мода
    public static final Map<String, SceneFile> SCENES = new HashMap<>();

    @Override
    protected Map<String, SceneFile> prepare(ResourceManager rm, ProfilerFiller profiler) {
        Map<String, SceneFile> out = new HashMap<>();
        // Ищем все JSON в папке featured мода lovsplayer
        var resources = rm.listResources(SCENE_PATH, name -> name.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            try (InputStream is = entry.getValue().open()) {
                String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                // Исправляем ключи с пробелами перед двоеточием ("key ": → "key":)
                String fixed = raw.replaceAll("\"\\s+\":", "\":");
                SceneFile sf = GSON.fromJson(fixed, SceneFile.class);
                if (sf != null && sf.name() != null) {
                    out.put(sf.name().trim(), sf);
                }
            } catch (Exception e) {
                LOGGER.warn("[yandereaddon] Не удалось прочитать сцену {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return out;
    }

    @Override
    protected void apply(Map<String, SceneFile> ready, ResourceManager rm, ProfilerFiller profiler) {
        SCENES.clear();
        SCENES.putAll(ready);
        LOGGER.info("[yandereaddon] Загружено сцен: {}", SCENES.size());
        SCENES.keySet().forEach(name -> LOGGER.info("  - \"{}\"", name));
    }
}
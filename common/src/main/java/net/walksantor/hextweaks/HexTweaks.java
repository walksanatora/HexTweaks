package net.walksantor.hextweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static at.petrak.hexcasting.interop.HexInterop.PATCHOULI_ANY_INTEROP_FLAG;

public class HexTweaks {
    public static final String MOD_ID = "hextweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Nullable public static HexTweaksConfig CONFIG = null;

    public static void breakpoint() {
        LOGGER.info("breakpoints sometimes fail. call me instead!");
    }

    public static void init() {
        //we... dont have anything...
        LOGGER.info("performing COMMON setup");
        CommandRegistrationEvent.EVENT.register((it,b,c) -> HexTweaksCommands.register(it));

        Path res = Platform.getConfigFolder().resolve("hextweaks.json");
        Gson gson = new GsonBuilder().setLenient().create();
        if (Files.exists(res)) {
            try {
                String json = Strings.join(Files.readAllLines(res).iterator(),'\n');
                CONFIG = gson.fromJson(json, HexTweaksConfig.class);
            } catch (Exception e) {
                LOGGER.error("Failed to load hextweaks config");
                e.printStackTrace();
                CONFIG = new HexTweaksConfig();
            }
        } else {
            CONFIG = new HexTweaksConfig();
        }

        LifecycleEvent.SERVER_STOPPING.register(it -> {
            try {
                Files.writeString(res,gson.toJson(CONFIG));
            } catch (IOException e) {
                LOGGER.error("failed to write config");
                e.printStackTrace();
            }
        });

        if (Platform.isModLoaded("computercraft")) {
            PatchouliAPI.get().setConfigFlag(PATCHOULI_ANY_INTEROP_FLAG, true);
        }
        if (Platform.isModLoaded("worldedit")) {
            TickEvent.SERVER_POST.register((server) -> {
                MojankResetChunk.step();
            });
        }

    }
}

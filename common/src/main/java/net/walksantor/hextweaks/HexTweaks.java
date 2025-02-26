package net.walksantor.hextweaks;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static at.petrak.hexcasting.interop.HexInterop.PATCHOULI_ANY_INTEROP_FLAG;

public class HexTweaks {
    public static final String MOD_ID = "hextweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Nullable static HexTweaksConfig CONFIG = null;

    @NotNull
    public static HexTweaksConfig getCONFIG() {
        boolean makeFile = false;
        Path res = Platform.getConfigFolder().resolve("hextweaks.json");
        if (CONFIG == null) {
            try {
                Either<HexTweaksConfig,HexTweaksConfig> either = HexTweaksConfig.Companion.getCODEC().parse(
                        JsonOps.INSTANCE,
                        JsonParser.parseReader(
                                new FileReader(
                                        res.toFile()
                                )
                        )
                ).getOrThrow(false, LOGGER::error);
                Optional<HexTweaksConfig> right = either.right();
                if (right.isPresent()) {
                    makeFile = true;
                    LOGGER.warn("Hextweaks config was not setup right, it has been reset to default.");
                    CONFIG = right.get();
                } else {
                    LOGGER.info("Hextweaks config loaded successfully");
                    CONFIG = either.left().get();
                }
            } catch (Exception e) {
                LOGGER.error("Hextweaks config does not exists. defaulting");
                makeFile = true;
                CONFIG = HexTweaksConfig.Companion.getDEFAULT();
            }
        }
        if (makeFile) {
            DataResult<JsonElement> result = HexTweaksConfig.Companion.getCODEC().encodeStart(
                    JsonOps.INSTANCE,
                    Either.left(CONFIG)
            );
            String output = new Gson().toJson(result.result().get());
            try {
                Files.writeString(res,output);
            } catch (IOException e) {
                LOGGER.error("Failed to save hextweaks config to a string");
                e.printStackTrace((PrintStream) LOGGER);
            }
        }
        return CONFIG;
    }

    public static void breakpoint() {
        LOGGER.info("breakpoints sometimes fail. call me instead!");
        new Exception("Breakpoint").printStackTrace();
    }

    public static void init() {
        //we... dont have anything...
        LOGGER.info("performing COMMON setup");
        CommandRegistrationEvent.EVENT.register((it,b,c) -> HexTweaksCommands.register(it));

        if (Platform.isModLoaded("computercraft")) {
            PatchouliAPI.get().setConfigFlag(PATCHOULI_ANY_INTEROP_FLAG, true);
        }
        //load config. somehow a <clinit> could go before this one. bwoomp i dont care. this is just so the config file wont be randomly loaded part way into the game.
        //I should also probally add a reload listener that nullifies the config and re-loads it
        getCONFIG();
    }
}

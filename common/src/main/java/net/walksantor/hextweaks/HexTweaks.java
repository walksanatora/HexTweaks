package net.walksantor.hextweaks;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.api.PatchouliAPI;

import static at.petrak.hexcasting.interop.HexInterop.PATCHOULI_ANY_INTEROP_FLAG;

public class HexTweaks {
    public static final String MOD_ID = "hextweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        //we... dont have anything...
        LOGGER.info("performing COMMON setup");
        CommandRegistrationEvent.EVENT.register((it,b,c) -> HexTweaksCommands.register(it));
        if (Platform.isModLoaded("computercraft")) {
            PatchouliAPI.get().setConfigFlag(PATCHOULI_ANY_INTEROP_FLAG, true);
        }
    }
}

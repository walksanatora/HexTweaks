package net.walksanator.hextweaks;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.items.storage.ItemScroll;
import at.petrak.hexcasting.common.lib.HexItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.walksantor.hextweaks.casting.PatternRegistry;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HexTweaksCommands {
    static void register(CommandDispatcher<CommandSourceStack> it) {
        LiteralArgumentBuilder<CommandSourceStack> main = literal("hextweaks");

        getAnglesig(main);

        it.register(main);
    }

    private static void getAnglesig(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(literal("give-grand").then(argument("anglesig",StringArgumentType.string()).requires(i -> i.hasPermission(3))
                .executes(ctx -> {
                    String sig = ctx.getArgument("anglesig",String.class);
                    HexPattern pat = PatternRegistry.INSTANCE.patternAllowIllegal(HexDir.WEST,sig);
                    HexPattern real = PatternRegistry.INSTANCE.getGrandSpellPattern(
                            Objects.requireNonNull(ctx.getSource().getPlayer()),
                            ctx.getSource().getLevel(),
                            pat
                    );
                    ctx.getSource().sendSystemMessage(Component.literal("%s".formatted(real)));

                    CompoundTag nbt = new CompoundTag();
                    nbt.put(ItemScroll.TAG_PATTERN, real.serializeToNBT());
                    nbt.put(ItemScroll.TAG_OP_ID, StringTag.valueOf("hextweaks:grand_spell"));
                    var stack = new ItemStack(HexItems.SCROLL_LARGE);
                    stack.setTag(nbt);
                    var stackEntity = ctx.getSource().getPlayer().drop(stack, false);
                    if (stackEntity != null) {
                        stackEntity.setNoPickUpDelay();
                        stackEntity.setThrower(ctx.getSource().getPlayer().getUUID());
                    }

                    ctx.getSource().getPlayer();
                    return 1;
                })
        ));
    }


}

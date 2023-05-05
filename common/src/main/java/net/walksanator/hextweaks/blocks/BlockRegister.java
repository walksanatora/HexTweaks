package net.walksanator.hextweaks.blocks;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.walksanator.hextweaks.HexTweaks;

public class BlockRegister {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(HexTweaks.MOD_ID, Registry.BLOCK_REGISTRY);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(HexTweaks.MOD_ID,Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Block> CRYSTALLIZED_SCROLL_BLOCK = BLOCKS.register("crystallizedbookblock",
            () -> new CrystalizedScrollBlock(BlockBehaviour.Properties.of(Material.AMETHYST)));

    public static void register() {
        BLOCKS.register();

        for (RegistrySupplier<Block> block : BLOCKS) {
            ITEMS.register(block.getId(),
                    () -> new BlockItem(block.get(), new Item.Properties().tab(IXplatAbstractions.INSTANCE.getTab())));
        }
        ITEMS.register();
    }

}

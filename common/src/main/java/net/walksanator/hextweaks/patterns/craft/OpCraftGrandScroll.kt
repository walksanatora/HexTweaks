package net.walksanator.hextweaks.patterns.craft

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.spell.mishaps.MishapBadItem
import at.petrak.hexcasting.common.items.ItemScroll
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.walksanator.hextweaks.blocks.BlockRegister

class OpCraftGrandScroll : SpellAction {
    override val argc: Int = 2
    override val isGreat: Boolean = true//require enlightenment
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val item = args.getItemEntity(0)
        if (item.item.item !is ItemScroll) {throw MishapBadItem(item, Component.translatable("item.scroll.great"))}
        if (item.item.tag == null || item.item.tag!!["op_id"] == null) { throw MishapBadItem(item,Component.translatable("item.scroll.great")) }

        val block = args.getBlockPos(1)
        ctx.assertVecInRange(block)
        val blockstate =ctx.world.level.getBlockState(block)
        if (!blockstate.`is`(Blocks.BUDDING_AMETHYST)) {throw MishapBadBlock(block, Component.translatable("block.budding_amethyst"))}


        return Triple(
            Spell(item,block),
            MediaConstants.CRYSTAL_UNIT * 10,
            listOf(
                ParticleSpray.burst(item.position(), 2.0,30),
                ParticleSpray.cloud(
                    Vec3(block.x.toDouble(),block.y.toDouble(),block.z.toDouble()),
                    1.0,
                    20
                )
            )
        )

    }

    private data class Spell(val scroll: ItemEntity, val block: BlockPos) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            //we allready know that target is
            // 1. the scroll is great
            // 2. we know we are targeting a budding amethyst
            scroll.kill()
            val block1 = BlockRegister.CRYSTALLIZED_SCROLL_BLOCK.get()
            ctx.world.level.setBlockAndUpdate(block,block1.defaultBlockState())
        }
    }
}
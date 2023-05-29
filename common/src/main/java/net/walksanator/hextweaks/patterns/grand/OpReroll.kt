package net.walksanator.hextweaks.patterns.grand

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapBadItem
import at.petrak.hexcasting.common.items.ItemScroll
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.walksanator.hextweaks.HexTweaks
import net.walksanator.hextweaks.items.CrystallizedScroll
import net.walksanator.hextweaks.patterns.PatternRegister
import kotlin.math.pow
import kotlin.random.Random


class OpReroll : SpellAction {
    override val argc: Int = 1
    override val isGreat: Boolean = true//require enlightenment
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val item = args.getItemEntity(0)
        if (!(item.item.item is ItemScroll || item.item.item is CrystallizedScroll)) {throw MishapBadItem(item, Component.translatable("item.scroll.great_or_better"))}
        if (item.item.tag == null || item.item.tag!!["op_id"] == null) { throw MishapBadItem(item,Component.translatable("item.scroll.great_or_better")) }

        val uses = if (item.item.tag!!["rerolls"] == null) {
            0
        } else {
            (item.item.tag!!["rerolls"] as IntTag).asInt
        }

        val cost = MediaConstants.DUST_UNIT * 100  * (1.25.pow(uses))

        return Triple(
            Spell(item,uses,ctx.world),
            cost.toInt(),
            listOf(ParticleSpray.cloud(item.position(), 2.0,30))
        )

    }

    private data class Spell(val target: ItemEntity, val uses: Int, val lvl: ServerLevel) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            //we allready know that target is
            // 1. a valid item
            // 2. a scroll that is Great/Grand
            val rand = Random.Default
            val tag = if (target.item.item is CrystallizedScroll) {
                //create the data
                val ctag = CompoundTag()
                val target = HexTweaks.GrandSpells[rand.nextInt(HexTweaks.GrandSpells.size)]
                val entry =
                    PatternRegister.lookupPatternIllegal( //use the illegal lookup method since Grand spells can have Illegal signatures
                        target
                    )
                ctag.put(
                    CrystallizedScroll.TAG_PATTERN,
                    entry.prototype().serializeToNBT()
                )
                ctag.put(CrystallizedScroll.TAG_OP_ID, StringTag.valueOf(target.toString()))
                ctag
            } else {
                val worldLookup = PatternRegistry.getPerWorldPatterns(lvl)

                val keys = worldLookup.keys.stream().toList()
                val sig = keys[rand.nextInt(keys.size)]

                val entry = worldLookup[sig]!!
                val opId = entry.first
                val startDir = entry.second
                val tag = CompoundTag()
                tag.putString(ItemScroll.TAG_OP_ID, opId.toString())
                tag.put(ItemScroll.TAG_PATTERN, HexPattern.fromAngles(sig, startDir).serializeToNBT())
                tag
            }
            tag.put("rerolls",IntTag.valueOf(uses+1))
            target.item.tag = tag
        }
    }
}
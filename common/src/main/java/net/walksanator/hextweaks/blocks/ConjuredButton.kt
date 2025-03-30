package net.walksanator.hextweaks.blocks

import at.petrak.hexcasting.common.blocks.BlockConjured
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConjuredButton(p: Properties) : BlockConjured(p) {
    companion object {
        private const val HELD_TICKS = 20;
        val PRESSED: BooleanProperty = BooleanProperty.create("pressed")
        val DIRECTION: DirectionProperty = DirectionProperty.create("direction")
    }

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        println(blockState.getValue(PRESSED))
        if (blockState.getValue<Boolean>(PRESSED) as Boolean) {
            return InteractionResult.CONSUME
        } else {
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, blockPos)
            return InteractionResult.sidedSuccess(level.isClientSide)
        }
    }

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        blockState.setValue(PRESSED,false)
        super.tick(blockState, serverLevel, blockPos, randomSource)
    }

    override fun hasDynamicShape(): Boolean = true

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val state = this.defaultBlockState().setValue(PRESSED,false)
        return state
    }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(PRESSED,DIRECTION)
    }
    override fun getVisualShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return Shapes.create(0.0,0.0,0.0,1.0,1.0,1.0)
    }
}
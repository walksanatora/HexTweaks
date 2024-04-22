package net.walksantor.hextweaks.entities

import at.petrak.hexcasting.api.utils.getBoolean
import at.petrak.hexcasting.api.utils.getInt
import at.petrak.hexcasting.api.utils.getLong
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.platform.Platform
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.BossEvent
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.rituals.HexRitual
import net.walksantor.hextweaks.casting.rituals.HexRitualRegistry
import ram.talia.hexal.common.entities.BaseWisp
import java.util.*
import kotlin.math.sqrt


//Entity: public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(modId, Registry.ENTITY_TYPE_REGISTRY);
//Attribute Supplier: dev.architectury.registry.level.entity.EntityAttributeRegistry.register()
//Renderer: dev.architectury.registry.client.level.entity.EntityRendererRegistry.register() (Client only)

class SpellBeaconEntity(entityType: EntityType<out LivingEntity>, level: Level) : LivingEntity(entityType, level) {
    val boss_event = ServerBossEvent(this.displayName, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)
        .setDarkenScreen(true) as ServerBossEvent
    var owner: UUID? = null
    var ritual: HexRitual? = null // the ritual being performed
    var media_till_next_stage = 0L
    var ticks_till_next_stage = 0
    var final_countdown = 0

    init {
        noPhysics = true
    }

    override fun move(type: MoverType, pos: Vec3) {
        return //no gravity, no piston, idk what the other movertypes are
    }

    override fun teleportTo(
        level: ServerLevel,
        x: Double,
        y: Double,
        z: Double,
        relativeMovements: MutableSet<RelativeMovement>,
        yRot: Float,
        xRot: Float
    ): Boolean {
        return false //immovable
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("has_ritual",ritual != null)
        val rit = ritual
        if (rit != null) {
            val loc = rit.getLocation()
            compound.putString("ritual_ns",loc.namespace)
            compound.putString("ritual_path",loc.path)
            compound.putInt("ticks_left",ticks_till_next_stage)
            compound.putLong("media_left",media_till_next_stage)
            val tag = CompoundTag()
            rit.saveState(tag)
            compound.putCompound("ritual",tag)
        }
        compound.putInt("countdown",final_countdown)
        compound.putUUID("owner", owner)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        final_countdown = compound.getInt("countdown",0)
        owner = compound.getUUID("owner")
        if (compound.getBoolean("has_ritual",defaultExpected = false)) {
            val resloc = ResourceLocation(
                compound.getString("ritual_ns"),
                compound.getString("ritual_path")
            )
            ritual = HexRitualRegistry.getFactory(resloc).apply(boss_event,compound.getCompound("ritual"))
            ticks_till_next_stage = compound.getInt("ticks_left", ritual!!.stepTime)
            media_till_next_stage = compound.getLong("media_left", ritual!!.stepMedia)
        }
    }

    override fun isNoGravity(): Boolean = true

    override fun push(x: Double, y: Double, z: Double) {
        //no pushy
    }

    override fun knockback(strength: Double, x: Double, z: Double) {
        //no pushy
    }

    //override fun attackable(): Boolean = true

    companion object {
        val BUILDER: EntityType.Builder<SpellBeaconEntity> = EntityType.Builder.of(
            ::SpellBeaconEntity, MobCategory.MISC
        ).fireImmune().canSpawnFarFromPlayer().sized(1f,1f)

        fun createAttributes(): AttributeSupplier.Builder = Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.MOVEMENT_SPEED, 0.0)

        val entity_predicate = run {
            if (Platform.isModLoaded("hexal")) {
                { parent: SpellBeaconEntity ->
                    { it: Entity ->
                        if (it is ItemEntity) {
                            IXplatAbstractions.INSTANCE.findMediaHolder(it.item) != null
                        } else if (it is BaseWisp) {
                            it.owner() == it.uuid || it.owner() == parent.owner //consume if it is unowned. or it is owned by the creator
                        } else {
                            false
                        }
                    }
                }
            } else {
                { _ ->
                    { it: Entity ->
                        if (it is ItemEntity) {
                            IXplatAbstractions.INSTANCE.findMediaHolder(it.item) != null
                        } else {
                            false
                        }
                    }
                }
            }
        }

        val entity_to_media = run {
            if (Platform.isModLoaded("hexal")) {
                { it: Entity ->
                    if (it is ItemEntity) {
                        IXplatAbstractions.INSTANCE.findMediaHolder(it.item)!!.withdrawMedia(-1,false)
                    } else if (it is BaseWisp) {
                        it.media
                    } else { 0 }
                }
            } else {
                { it: Entity ->
                    if (it is ItemEntity) {
                        IXplatAbstractions.INSTANCE.findMediaHolder(it.item)!!.withdrawMedia(-1, false)
                    } else { 0 }
                }
            }
        }
    }

    override fun tick() {
        super.tick()
        //boss_event.progress = this.health / this.maxHealth
        if (level().isClientSide()) {return}
        if (ritual == null) {return}
        if (final_countdown > 0) {
            final_countdown -= 1
            if (final_countdown == 0) {
                ritual!!.ritualFinished(this)
                ritual = null
                this.kill()
            } else {
                boss_event.progress = final_countdown.toFloat() / ritual!!.countdown
            }
            return
        }
        ritual!!.tick(ticks_till_next_stage,media_till_next_stage)
        if (ticks_till_next_stage > 0) {ticks_till_next_stage -= 1}
        val consumables = level().getEntitiesOfClass(Entity::class.java, AABB.ofSize(position(),3.0,3.0,3.0),entity_predicate.invoke(this))
        if (consumables.isNotEmpty()) {
            println(consumables)
            if (media_till_next_stage > 0) {
                val media = consumables.map(entity_to_media).sum()
                if (media >= media_till_next_stage) {media_till_next_stage = 0} else {media_till_next_stage -= media}
                val slevel = level() as ServerLevel
                consumables.onEach {
                    it.kill()
                } //remove it since we consumed it
            }
        }
        if ((ticks_till_next_stage <= 0) && (media_till_next_stage <= 0)) {
            ritual!!.stepFinished()
            if (ritual!!.stepsCompleted < ritual!!.totalSteps) {
                ticks_till_next_stage = ritual!!.stepTime
                media_till_next_stage = ritual!!.stepMedia
            } else {
                boss_event.color = BossEvent.BossBarColor.RED
                final_countdown = ritual!!.countdown
            }
        }
    }

    override fun startSeenByPlayer(serverPlayer: ServerPlayer) {
        super.startSeenByPlayer(serverPlayer)
        boss_event.addPlayer(serverPlayer)
    }

    override fun stopSeenByPlayer(serverPlayer: ServerPlayer) {
        super.stopSeenByPlayer(serverPlayer)
        boss_event.removePlayer(serverPlayer)
    }

    override fun getArmorSlots(): MutableIterable<ItemStack> = mutableListOf()

    override fun setItemSlot(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        //noop
    }

    override fun getItemBySlot(equipmentSlot: EquipmentSlot): ItemStack = ItemStack.EMPTY

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
}

class SpellBeaconEntityRender(context: EntityRendererProvider.Context) : EntityRenderer<SpellBeaconEntity>(context) {
    override fun render(
        entity: SpellBeaconEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val scale = 1/(sqrt(3.0)).toFloat()
        val offset = scale/2
        val blockRender: BlockRenderDispatcher = Minecraft.getInstance().blockRenderer
        poseStack.pushPose()
            poseStack.translate(-0.5,0.0,-0.5)
            blockRender.renderSingleBlock(Blocks.PURPLE_STAINED_GLASS.defaultBlockState(),poseStack,buffer,packedLight,0)
            poseStack.pushPose()
                poseStack.scale(scale,scale,scale)
                poseStack.translate(offset,offset,offset)
                //poseStack.rotateAround()
                blockRender.renderSingleBlock(Blocks.AMETHYST_BLOCK.defaultBlockState(),poseStack,buffer,packedLight,0)
            poseStack.popPose()
        poseStack.popPose()
        LevelRenderer.renderLineBox(poseStack,buffer.getBuffer(RenderType.debugLineStrip(10.0)),AABB.ofSize(entity.position(),3.0,3.0,3.0),0.5f,0.0f,0.5f,1.0f)
    }

    override fun getTextureLocation(entity: SpellBeaconEntity): ResourceLocation = ResourceLocation(HexTweaks.MOD_ID,"sbe_tex")

}
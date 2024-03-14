package net.walksantor.hextweaks.entities

import at.petrak.hexcasting.api.utils.putBoolean
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.BossEvent
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaks
import kotlin.math.floor
import kotlin.math.sqrt


//Entity: public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(modId, Registry.ENTITY_TYPE_REGISTRY);
//Attribute Supplier: dev.architectury.registry.level.entity.EntityAttributeRegistry.register()
//Renderer: dev.architectury.registry.client.level.entity.EntityRendererRegistry.register() (Client only)

class SpellBeaconEntity(entityType: EntityType<out LivingEntity>, level: Level) : LivingEntity(entityType, level) {
    val boss_event = ServerBossEvent(this.displayName, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)
        .setDarkenScreen(true) as ServerBossEvent;

    var targetPos: Vec3? = null

    init {
        noPhysics = true
    }

    override fun move(type: MoverType, pos: Vec3) {
        val pos = position().add(pos)
        val rpos = Vec3(
            floor(pos.x)+0.5,
            floor(pos.y),
            floor(pos.z)+0.5
        )
        setPos(
            rpos
        )
        if (targetPos == null) { targetPos = rpos }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("htp",targetPos != null)
        val tp = targetPos
        if (tp != null) {
            compound.putDouble("ox", tp.x)
            compound.putDouble("oy", tp.y)
            compound.putDouble("oz", tp.z)
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        if (compound.getBoolean("htp")) {
            targetPos = Vec3(compound.getDouble("ox"),
            compound.getDouble("oy"),
            compound.getDouble("oz"))
        } else {
            targetPos = null
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
    }

    override fun tick() {
        super.tick()
        boss_event.progress = this.health / this.maxHealth
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
    var rotate_velocity = null

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
    }

    override fun getTextureLocation(entity: SpellBeaconEntity): ResourceLocation = ResourceLocation(HexTweaks.MOD_ID,"sbe_tex")

}
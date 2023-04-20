package net.walksanator.hextweaks.dammagesources;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceSus extends DamageSource {
    public DamageSourceSus() {
        super("hextweaks.death.sus");
        this.bypassArmor();
        this.bypassMagic();
        this.bypassInvul();
        this.setMagic();
    }
}

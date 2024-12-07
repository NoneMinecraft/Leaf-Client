package net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils

import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.delayControlPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.delayControlPosModeMinDelayValue
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getPlayerPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getVelocityMode
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.abs

fun posX(it: EntityPlayer): Double = getPos(it.posX, it.serverPosX.toDouble())
fun posY(it: EntityPlayer): Double = getPos(it.posY, it.serverPosY.toDouble())
fun posZ(it: EntityPlayer): Double = getPos(it.posZ, it.serverPosZ.toDouble())
fun playerPosX(it: EntityPlayer): Double = getPlayerPos(it.posX, it.serverPosX.toDouble())
fun playerPosY(it: EntityPlayer): Double = getPlayerPos(it.posY, it.serverPosY.toDouble())
fun playerPosZ(it: EntityPlayer): Double = getPlayerPos(it.posZ, it.serverPosZ.toDouble())

fun velocityX(it: EntityPlayer): Double = getVelocity(it.posX, it.prevPosX, it.serverPosX.toDouble()/32, it.lastTickPosX)
fun velocityY(it: EntityPlayer): Double = getVelocity(it.posY, it.prevPosY, it.serverPosY.toDouble()/32, it.lastTickPosY)
fun velocityZ(it: EntityPlayer): Double = getVelocity(it.posZ, it.prevPosZ, it.serverPosZ.toDouble()/32, it.lastTickPosZ)

fun velocityABSX(it: EntityPlayer): Double = getVelocityABS(it.posX, it.prevPosX, it.serverPosX.toDouble()/32, it.lastTickPosX)
fun velocityABSY(it: EntityPlayer): Double = getVelocityABS(it.posY, it.prevPosY, it.serverPosY.toDouble()/32, it.lastTickPosY)
fun velocityABSZ(it: EntityPlayer): Double = getVelocityABS(it.posZ, it.prevPosZ, it.serverPosZ.toDouble()/32, it.lastTickPosZ)

fun getPos(pos: Double, serverPos: Double): Double {
    return if (getPosMode.get() == "Pos" || (delayControlPosMode.get() && ping() > delayControlPosModeMinDelayValue.get())) pos
    else if (getPosMode.get() == "ServerPos") serverPos / 32 else pos + (serverPos / 32 - pos)
}
fun getPlayerPos(pos: Double, serverPos: Double): Double {
    return if (getPlayerPosMode.get() == "Pos" || (delayControlPosMode.get() && ping() > delayControlPosModeMinDelayValue.get())) pos
    else if (getPlayerPosMode.get() == "ServerPos") serverPos  else pos + (serverPos - pos)
}

fun getVelocity(pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double): Double {
    return when (getVelocityMode.get()) {
        "Pos" -> pos - prevPos
        "ServerPos" -> serverPos - lastTickPos
        "MixServerPosAndPrevPos" -> serverPos - prevPos
        "MixPosAndLastTickPos" -> pos - lastTickPos
        "PracticalityMix" -> (serverPos - lastTickPos) - (pos - prevPos) / 1.1
        else -> 0.0
    }
}

fun getVelocityABS(pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double): Double {
    return abs(getVelocity(pos, prevPos, serverPos, lastTickPos))
}


/*头部算法(deobf)

if (targetPlayer != null) {
    if (this.main.enableBlood() && this.delayBlood.get(targetPlayer.getUniqueId()) == null) {
        targetPlayer.getWorld().playEffect(hitLocation, Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        this.delayBlood.put(targetPlayer.getUniqueId(), this.delayshot);
    }

    double damageMultiplier;
    GunDamageEvent damageEvent;
    // Check if it's a headshot based on the Y-coordinate differences
    if (!targetPlayer.isSneaking() && hitLocation.getY() - targetPlayer.getLocation().getY() > 1.35 && hitLocation.getY() - targetPlayer.getLocation().getY() < 1.9 || hitLocation.getY() - targetPlayer.getLocation().getY() > 1.27 && hitLocation.getY() - targetPlayer.getLocation().getY() < 1.82) {
        // If the player is wearing a leather helmet, the damage multiplier is 0.5, otherwise it's 0.25
        damageMultiplier = targetPlayer.getInventory().getHelmet().getType() == Material.LEATHER_HELMET ? 0.5 : 0.25;

        // Create a GunDamageEvent for headshot damage
        damageEvent = new GunDamageEvent(this.damage + damageMultiplier * this.damage, true, shooter, targetPlayer);
        this.main.getServer().getPluginManager().callEvent(damageEvent);

        // Apply damage and play headshot sounds based on success
        if (this.main.getManager().damage(game, shooter, targetPlayer, this.damage + damageMultiplier * this.damage, this.symbol + "headshot")) {
            shooter.playSound(shooter.getLocation(), "cs.gamesounds.headshotkill", 1.0F, 1.0F);
            ((PlayerStatus) game.getStats().get(shooter.getUniqueId())).addHeadshotKill();
        } else {
            shooter.playSound(shooter.getLocation(), "cs.random.headshot_shooter", 1.0F, 1.0F);
            targetPlayer.playSound(shooter.getLocation(), "cs.random.headshot_victim", 1.0F, 1.0F);
        }
        break;
    }

    // If not a headshot, check for chestplate armor and apply chest hit damage
    damageMultiplier = targetPlayer.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE ? 0.0 : 0.15;

    // Create a GunDamageEvent for chest hit damage
    damageEvent = new GunDamageEvent(this.damage - damageMultiplier * this.damage, false, shooter, targetPlayer);
    this.main.getServer().getPluginManager().callEvent(damageEvent);

    // Apply damage for chest hit
    this.main.getManager().damage(game, shooter, targetPlayer, this.damage - damageMultiplier * this.damage, this.symbol);
    break;
}

 */

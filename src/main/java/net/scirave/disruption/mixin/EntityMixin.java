/*
 * -------------------------------------------------------------------
 * Disruption
 * Copyright (c) 2022 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.disruption.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.scirave.disruption.helpers.EntityVelocityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityVelocityInterface {

	@Shadow
	private Vec3d velocity;

	@Shadow
	public abstract Vec3d getVelocity();

	@Shadow
	public abstract World getWorld();

	@Shadow
	public abstract BlockPos getBlockPos();

	@Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
	public void disruption$fallingGroupVelocity(Vec3d velocity, CallbackInfo ci) {
		//Overridden
	}

	@Override
	public void setVelocityProxy(Vec3d velocity) {
		this.velocity = velocity;
	}

}

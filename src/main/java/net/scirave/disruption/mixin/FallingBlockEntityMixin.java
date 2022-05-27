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

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.scirave.disruption.helpers.EntityVelocityInterface;
import net.scirave.disruption.helpers.FallingGroupInterface;
import net.scirave.disruption.logic.BlockHandler;
import net.scirave.disruption.logic.FallingBlockGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends EntityMixin implements FallingGroupInterface {

	@Nullable public FallingBlockGroup fallingGroup = null;

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 2), cancellable = true)
	public void disruption$preventDiscard1(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 3), cancellable = true)
	public void disruption$preventDiscard2(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 4), cancellable = true)
	public void disruption$preventDiscard3(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V", ordinal = 5), cancellable = true)
	public void disruption$preventDiscard4(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;", ordinal = 2), cancellable = true)
	public void disruption$preventDiscard5(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0), cancellable = true)
	public void disruption$placeFallingGroup(CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();

			World world = getWorld();
			BlockPos pos = getBlockPos();

			BlockState down = world.getBlockState(pos.down());
			if (BlockHandler.isBlockIntangible(down, world, pos.down())) return;

			if (!BlockHandler.updateNeighbors(world, pos)) {
				fallingGroup.place();
			}
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LandingBlock;onLanding(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/FallingBlockEntity;)V"))
	public void disruption$fallingBlockDisruption(CallbackInfo ci) {
		if (fallingGroup == null) {
			BlockHandler.updateNeighbors(getWorld(), getBlockPos());
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/FallingBlockEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 2))
	public void disruption$fallingGroupAcceleration(FallingBlockEntity instance, Vec3d vec3d) {
		((EntityVelocityInterface) instance).setVelocityProxy(vec3d);
	}

	@Override
	public void disruption$fallingGroupVelocity(Vec3d velocity, CallbackInfo ci) {
		if (fallingGroup != null) {
			ci.cancel();
			fallingGroup.setVelocity(velocity, this.getVelocity());
		}
	}

	@Override
	public void setFallingGroup(@Nullable FallingBlockGroup fallingGroup) {
		this.fallingGroup = fallingGroup;
	}

	@Override
	public @Nullable FallingBlockGroup getFallingGroup() {
		return this.fallingGroup;
	}
}

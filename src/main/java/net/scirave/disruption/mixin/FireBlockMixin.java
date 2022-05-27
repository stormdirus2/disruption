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

import net.scirave.disruption.Disruption;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends BlockMixin {

    @Inject(method = "trySpreadingFire", at = @At("RETURN"))
    protected void disruption$fireSpread(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge, CallbackInfo ci) {
		if (world.getBlockState(pos).isAir()) {
			world.emitGameEvent(Disruption.FIRE_SPREAD, pos);
		}
    }
}

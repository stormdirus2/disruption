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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.scirave.disruption.Disruption;
import net.scirave.disruption.logic.BlockHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

	public final HashSet<BlockPos> disruption = new HashSet<>();
	public final HashSet<BlockPos> neighborDisruptions = new HashSet<>();

	@Inject(method = "emitGameEvent", at = @At("RETURN"))
	public void disruption$detectDisruption(Entity entity, GameEvent event, BlockPos pos, CallbackInfo ci) {
		boolean entityExists = entity != null;

		if (entityExists) {
			if (Disruption.hasGameEventTag(Disruption.ENTITY_DISRUPTION, event)) {
				disruption.add(pos);
				return;
			}
		}
		if (Disruption.hasGameEventTag(Disruption.DISRUPTION, event)) {
			disruption.add(pos);
			return;
		}
		if (entityExists) {
			if (Disruption.hasGameEventTag(Disruption.ENTITY_NEIGHBOR_DISRUPTION, event)) {
				neighborDisruptions.add(pos);
				return;
			}
		}
		if (Disruption.hasGameEventTag(Disruption.NEIGHBOR_DISRUPTION, event)) {
			neighborDisruptions.add(pos);
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void disruption$tickDisruptions(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		ServerWorld world = (ServerWorld) (Object) this;
		disruption.forEach((pos) -> BlockHandler.updatePosAndNeighbors(world, pos));
		disruption.clear();
		neighborDisruptions.forEach((pos) -> BlockHandler.updatePosAndNeighbors(world, pos));
		neighborDisruptions.clear();
	}

}

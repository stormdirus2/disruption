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

package net.scirave.disruption.logic;

import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.scirave.disruption.helpers.EntityVelocityInterface;

import java.util.HashSet;

public class FallingBlockGroup {

	public final HashSet<FallingBlockEntity> fallingBlockEntities = new HashSet<>();

	public void add(FallingBlockEntity fallingBlock) {
		fallingBlockEntities.add(fallingBlock);
	}

	public void remove(FallingBlockEntity fallingBlock) {
		fallingBlockEntities.remove(fallingBlock);
	}

	public void place() {
		HashSet<FallingBlockEntity> toRemove = new HashSet<>();

		fallingBlockEntities.forEach((fallingBlock -> {
			if (BlockHandler.placeFallingBlock(fallingBlock)) {
				toRemove.add(fallingBlock);
			}
		}));

		toRemove.forEach(fallingBlock -> fallingBlockEntities.remove(fallingBlock));
	}

	public void setVelocity(Vec3d velocity, Vec3d original) {
		Vec3d newVelocity = (velocity.subtract(original).multiply(1.0F/fallingBlockEntities.size())).add(original);
		fallingBlockEntities.forEach((fallingBlock -> ((EntityVelocityInterface) fallingBlock).setVelocityProxy(newVelocity)));
	}



}

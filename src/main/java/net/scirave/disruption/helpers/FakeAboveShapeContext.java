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

package net.scirave.disruption.helpers;

import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class FakeAboveShapeContext implements ShapeContext {

	public final static FakeAboveShapeContext INSTANCE = new FakeAboveShapeContext();

	@Override
	public boolean isDescending() {
		return false;
	}

	@Override
	public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
		return true;
	}

	@Override
	public boolean isHolding(Item item) {
		return false;
	}

	@Override
	public boolean canWalkOnFluid(FluidState state, FluidState fluidState) {
		return false;
	}
}

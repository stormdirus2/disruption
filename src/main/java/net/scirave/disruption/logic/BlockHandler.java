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

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.scirave.disruption.Disruption;
import net.scirave.disruption.DisruptionConfig;
import net.scirave.disruption.helpers.FakeAboveShapeContext;
import net.scirave.disruption.helpers.FallingGroupInterface;

public class BlockHandler {

	public static float getBlastResistance(Block block) {
		return block.getBlastResistance() * DisruptionConfig.getBlastResistanceScaling();
	}

	public static float getHardness(Block block) {
		return block.getHardness() * DisruptionConfig.getHardnessScaling();
	}

    public static boolean blockNotFall(BlockState above, BlockState below, World world, BlockPos posBelow) {

        Material material = below.getMaterial();

        if (material.isLiquid()) {
            return isBuoyant(above);
        }

		return !isBlockIntangible(below, world, posBelow);
	}

    public static boolean blockViable(BlockState state, World world, BlockPos pos) {
		PistonBehavior behavior = state.getPistonBehavior();
		if (behavior == PistonBehavior.BLOCK || behavior == PistonBehavior.IGNORE) return false;

        if (canFloat(state) || getBlastResistance(state.getBlock()) > 0) {
            return !isBlockIntangible(state, world, pos);
        }

        return false;
    }

    public static boolean upEncased(World world, BlockPos pos) {
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                for (int c = 0; c <= 1; c++) {
					BlockPos pos2 = pos.north(a).east(b).up(c);
                    if (!blockViable(world.getBlockState(pos2), world, pos2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean upCheck(BlockState state, World world, BlockPos pos) {
        if (!blockViable(state, world, pos)) {
            return false;
        }

        if (canFloat(state) || upEncased(world,pos)) {
            return true;
        }

		BlockPos pos2 = pos.up();

        return upCheck(world.getBlockState(pos2), world, pos2);
    }

	public static boolean isBuoyant(BlockState state) {
		Block block = state.getBlock();
		return state.getMaterial().isBurnable() || block.getSlipperiness() > 0.6f || Disruption.hasBlockTag(Disruption.BUOYANT, block);
	}

    public static boolean canHang(BlockState state) {
		Block block = state.getBlock();
        return state.getLuminance() > 0 || (state.getSoundGroup() == BlockSoundGroup.GLASS && block.getSlipperiness() <= 0.6F) || Disruption.hasBlockTag(Disruption.HANGS, block);
    }

	public static boolean isProtected(BlockState state) {
		return state.getSoundGroup() == BlockSoundGroup.GLASS || Disruption.hasBlockTag(Disruption.PROTECTED, state.getBlock());
	}

	public static boolean canFloat(BlockState state) {
		Block block = state.getBlock();
		return getBlastResistance(block) >= 1200 || Disruption.hasBlockTag(Disruption.FLOATS, block);
	}

    public static boolean check(BlockState state, World world, BlockPos pos, int Recursion) {
        if (!blockViable(state, world, pos)) {
			BlockPos pos2 = pos.up(Recursion);
			BlockState state2 = world.getBlockState(pos2);
            if (canHang(state2)) {
                return upCheck(state2, world, pos2);
            } else {
				BlockPos pos3 = pos2.up();
				BlockState state3 = world.getBlockState(pos3);
				if (canHang(state3)) {
					return upCheck(state3, world, pos3);
				}
            }
            return false;
        }

        if (canFloat(state) || isEncased(world, pos)) {
            return true;
        }

        return check(world.getBlockState(pos.down()), world, pos.down(), Recursion + 1);
    }

    public static int getReach(BlockState state) {
        Block block = state.getBlock();

        double resis = Math.max(getHardness(block), getBlastResistance(block));

        if (isProtected(state)) {
            resis = Math.max(resis, DisruptionConfig.getProtectedReachMinimum());
        }

        return (int) Math.min(Math.ceil(resis),50);
    }

    public static boolean isEncased(World world, BlockPos pos) {
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                for (int c = 0; c <= 1; c++) {
					BlockPos pos2 = pos.north(a).east(b).down(c);
                    if (!blockViable(world.getBlockState(pos2), world, pos2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean scan(BlockState state, World world, BlockPos pos) {
		if (canHang(state) && upCheck(state, world, pos)) {
			return true;
		} else {
			BlockPos pos2 = pos.up();
			BlockState state2 = world.getBlockState(pos2);
			if (canHang(state2) && upCheck(state2, world, pos2)) {
				return true;
			}
		}

        int reach = getReach(state);
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a != 0 || b != 0) {
                    int i = 1;
                    int goal = reach;
                    while (i <= goal) {
						BlockPos pos3 = pos.north(a * i).east(b * i);
						BlockState state3 = world.getBlockState(pos3);
                        if (blockViable(state3, world, pos3)) {
                            if (check(state3, world, pos3,0)) {
                                return true;
                            }
                            int oReach = getReach(state3);
                            if (oReach > goal) {
                                goal = oReach;
                            }
                        } else {
                            break;
                        }
                        i = i + 1;
                    }
                }
            }
        }
        return false;
    }
	public static boolean isBlockReplaceable(BlockState state) {
		return state.getMaterial().isReplaceable() || state.getPistonBehavior() == PistonBehavior.DESTROY;
	}

	public static boolean isBlockIntangible(BlockState state, World world, BlockPos pos) {
		return isBlockReplaceable(state) && !(state.getMaterial().blocksMovement() || !state.getCollisionShape(world, pos, FakeAboveShapeContext.INSTANCE).isEmpty());
	}

	public static boolean placeFallingBlock(FallingBlockEntity fallingBlock) {
		BlockPos blockPos = fallingBlock.getBlockPos();
		BlockState blockState = fallingBlock.getBlockState();
		Block block = blockState.getBlock();
		World world = fallingBlock.getWorld();

		boolean canPlace = blockState.canPlaceAt(world, blockPos);
		boolean canPlace2 = false;

		BlockState blockState2 = world.getBlockState(blockPos);

		if (!canPlace || !isBlockReplaceable(blockState2)) {
			canPlace2 = blockState.canPlaceAt(world, blockPos.up()) ;
			if (canPlace2) {
				blockPos = blockPos.up();
				blockState2 = world.getBlockState(blockPos);
			}
		}

		if (isBlockReplaceable(blockState2) && (canPlace || canPlace2)) {

			world.breakBlock(blockPos, true);

			if (blockState2.contains(Properties.WATERLOGGED) && world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
				blockState = blockState.with(Properties.WATERLOGGED, true);
			}

			if (world.setBlockState(blockPos, blockState, 3)) {
				((ServerWorld) world)
						.getChunkManager()
						.threadedAnvilChunkStorage
						.sendToOtherNearbyPlayers(fallingBlock, new BlockUpdateS2CPacket(blockPos, world.getBlockState(blockPos)));
				fallingBlock.discard();

				if (block instanceof LandingBlock) {
					((LandingBlock) block).onLanding(world, blockPos, blockState, blockState, fallingBlock);
				}

				if (fallingBlock.blockEntityData != null && blockState.hasBlockEntity()) {
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity != null) {
						NbtCompound nbtCompound = blockEntity.toNbt();

						for (String string : fallingBlock.blockEntityData.getKeys()) {
							nbtCompound.put(string, fallingBlock.blockEntityData.get(string).copy());
						}

						blockEntity.readNbt(nbtCompound);
						blockEntity.markDirty();
					}
				}

				return true;
			}
		} else if (!isBlockReplaceable(blockState2)) {
			fallingBlock.discard();
			if (fallingBlock.dropItem && fallingBlock.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
				fallingBlock.onDestroyedOnLanding(block, blockPos);
				fallingBlock.dropItem(block);
			}
			return true;
		}

		return false;
	}
    public static void fall(BlockState state, World world, BlockPos pos, FallingBlockGroup fallingGroup) {
        Block block = state.getBlock();

		if (block instanceof FallingBlock) {
			FallingBlockEntity.fall(world, pos, state);
			return;
		}

        if (Disruption.hasBlockTag(Disruption.USE_DEFAULT_STATE, block)) {
            state = block.getDefaultState();
        }

        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(world, pos, state);
		((FallingGroupInterface) fallingBlock).setFallingGroup(fallingGroup);
		fallingGroup.add(fallingBlock);

		float hurtAmount = block.getHardness() * 0.4F * DisruptionConfig.getCaveInDamageScaling();
		fallingBlock.setHurtEntities(hurtAmount, MathHelper.ceil(hurtAmount * 20));
    }

    public static boolean updateNeighbors(World world, BlockPos pos, FallingBlockGroup fallingGroup, int updates) {
		if (updates > DisruptionConfig.getMaxBlockUpdates()) return false;

		boolean activity = false;

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                for (int c = -1; c <= 1; c++) {
                    if (a != 0 || b != 0 || c != 0) {
                        if (update(world, pos.north(a).east(b).up(c), fallingGroup, updates + 1)) {
							activity = true;
						}
                    }
                }
            }
        }

		return activity;
    }
    public static boolean update(World world, BlockPos pos, FallingBlockGroup fallingGroup, int updates) {
		if (updates > DisruptionConfig.getMaxBlockUpdates()) return false;
        BlockState state = world.getBlockState(pos);
		boolean activity = false;

        if (!canFloat(state) && blockViable(state, world, pos)) {
			BlockPos posDown = pos.down();
            if (!blockNotFall(state, world.getBlockState(posDown), world, posDown) && !scan(state, world, pos)) {
				activity = true;
                if (state.getRenderType() == BlockRenderType.MODEL) {
                    fall(state, world, pos, fallingGroup);
                } else {
                    world.breakBlock(pos,true);
                }
                updateNeighbors(world, pos, fallingGroup, updates + 1);
            }
        }

		return activity;
    }

	public static boolean updateNeighbors(World world, BlockPos pos) {
		return updateNeighbors(world, pos, new FallingBlockGroup(), 0);
	}

	public static boolean update(World world, BlockPos pos) {
		return update(world, pos, new FallingBlockGroup(), 0);
	}

	public static boolean updatePosAndNeighbors(World world, BlockPos pos) {
		FallingBlockGroup fallingGroup = new FallingBlockGroup();
		return update(world, pos, fallingGroup, 0) || updateNeighbors(world, pos, fallingGroup, 0);
	}
}

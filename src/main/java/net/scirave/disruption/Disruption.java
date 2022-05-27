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

package net.scirave.disruption;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import net.scirave.disruption.helpers.GameEventReferenceInterface;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Disruption implements ModInitializer {
    public static final String MOD_ID = "disruption";

    public static final TagKey<Block> HANGS = getBlockTag("hangs");
    public static final TagKey<Block> PROTECTED = getBlockTag("protected");
    public static final TagKey<Block> BUOYANT = getBlockTag("buoyant");
	public static final TagKey<Block> FLOATS = getBlockTag("floats");
    public static final TagKey<Block> USE_DEFAULT_STATE = getBlockTag("default_state");

	public static final GameEvent FIRE_SPREAD = new GameEvent(getIdentifier("fire_spread").toString(), 16);
	public static final GameEvent BLOCK_EXPLODED = new GameEvent(getIdentifier("block_exploded").toString(), 16);

	public static final TagKey<GameEvent> DISRUPTION = getGameEventTag("disruption");
	public static final TagKey<GameEvent> NEIGHBOR_DISRUPTION = getGameEventTag("neighbor_disruption");
	public static final TagKey<GameEvent> ENTITY_DISRUPTION = getGameEventTag("entity_disruption");
	public static final TagKey<GameEvent> ENTITY_NEIGHBOR_DISRUPTION = getGameEventTag("entity_neighbor_disruption");

	public static Identifier getIdentifier(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static TagKey<Block> getBlockTag(String id) {
		return TagKey.of(Registry.BLOCK_KEY, getIdentifier(id));
	}
	public static TagKey<GameEvent> getGameEventTag(String id) {
		return TagKey.of(Registry.GAME_EVENT_KEY, getIdentifier(id));
	}

	public static boolean hasBlockTag(TagKey<Block> tag, Block block) {
		return block.getBuiltInRegistryHolder().hasTag(tag);
	}

	public static boolean hasGameEventTag(TagKey<GameEvent> tag, GameEvent event) {
		return ((GameEventReferenceInterface) event).getBuiltInRegistryHolder().hasTag(tag);
	}

    @Override
    public void onInitialize(ModContainer mod) {
		Registry.register(Registry.GAME_EVENT, Identifier.tryParse(FIRE_SPREAD.getId()), FIRE_SPREAD);
		Registry.register(Registry.GAME_EVENT, Identifier.tryParse(BLOCK_EXPLODED.getId()), BLOCK_EXPLODED);

        Logger.getLogger(MOD_ID).log(Level.INFO, "[{}] It's raining stone and.. barrels?", mod.metadata().name());
    }
}


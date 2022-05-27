#### __ONLY__ requires server installation. Client installation is __OPTIONAL__.

#### Does __NOT__ affect world saves or any other form of NBT data.

#### Configuration for values such as HardnessScaling __WILL__ be added as soon as configuration support is added to the Quilt Loader.

<br>
<br>

A mod about cave-ins and falling blocks - Blocks fall, and can cause damage!

Heavy inspiration from ye ol' Enviromine.

Significantly more performant and advanced than its Fabric predecessor, Block Physics.

<br>
<br>

### Basic logic

Blocks search, traditionally, along 8 vectors on the X-Z plane.
Picture the movement pattern of a Queen in Chess.

<br>

Blocks have larger reach depending on their Hardness (how long it takes to mine the block) and their Blast Resistance (self-explanatory).

The equation for reach is: Math.max(Hardness * HardnessScaling, BlastResistance * BlastResistanceScaling), with reach having a maximum of 50 blocks.

The default HardnessScaling is 2, while BlastResistanceScaling is 1.


<br>

Along these vectors, blocks search for supports.

'Supports' are pillars of any number of blocks below a given position on a vector that reaches an encased position or a 'floating' block.

Encased positions are positions where a block is surrounded by other blocks on nearly all vectors in 3D space.

<br>
<br>

### Disruption Events

Blocks only validate their supports, and check if they need to fall, if there is a Disruption Event.

Furthermore, only a limited range of blocks are validated per Disruption Event. This is defined by MaxBlockUpdates, whose default is 50.

<br>

Disruption Events are Game Events who fit into any or all of the related tags:

* [disruption:disruption] - Checks the position, and its neighbors.
* [disruption:entity_disruption] - The above, but only if there is an entity who caused the event.
* [disruption:neighbor_disruption] - Checks the position's neighbors only.
* [disruption:entity_neighbor_disruption] - The above, but only if there is an entity who caused the event.

The default set of Game Events are:

* [minecraft:block_place] in [disruption:entity_disruption], occurs when a block is placed.
* [minecraft:block_destroy] in [disruption:entity_neighbor_disruption], occurs when a block is destroyed.
* [disruption:fire_spread] in [disruption:neighbor_disruption], occurs when fire incinerates a block.
* [disruption:block_exploded] in [disruption:neighbor_disruption], occurs when a block is exploded (and subsequently destroyed).

<br>
<br>

### Damage logic

Like Anvils, falling blocks deal damage based on the distance they fall.

The equation for the resultant damage is: (FallDistance - 1) * DamageModifier, where the maximum is DamageMax.

<br>

DamageModifier and DamageMax are calculated as so:

The default CaveInDamageScaling is 1.
DamageModifier = Hardness * 0.4 * CaveInDamageScaling
DamageMax = DamageModifier * 20

The reason why we calculate damage this way is to match the damage scaling of an Anvil, whose Hardness is 5.

<br>
<br>

### 'Floating' blocks

Floating blocks are blocks that do not require a support, and therefor float. They also act as a support to other blocks.

There are three reasons why such blocks exist:

1. To lessen the impact this mod has on vanilla contraptions.
2. To prevent this mod from impacting generated structures whose material is supposed to be unbreakable or nigh unbreakable.
3. To allow Players to create floating structures.

Floating blocks fulfill any or all of the conditions below:

* Has a Blast Resistance of 1200 (Obsidian's Blast Resistance) or higher
* Is in the [disruption:floats] block tag

<br>
<br>

### 'Hanging' blocks

Hanging blocks are blocks whose supports can go upwards as well as downwards.

There are three reasons why such blocks exist:

1. To prevent the Nether from caving-in every nanosecond.
2. To allow End Islands to float.
3. To allow Players to hang things from ceilings.

Hanging blocks fulfill any or all of the conditions below:

* Emits light
* Has the Glass sound effect, but is not slippery (slipperiness <= 0.6)
* Is in the [disruption:hangs] block tag

<br>
<br>

## 'Protected' blocks

Protected blocks are blocks whose calculated reach is always at least 3 blocks.

There are three reasons why such blocks exist:

1. To prevent trees and giant mushrooms from COMPLETELY pruning themselves
2. To prevent Glass and Glass-like blocks from being overly unstable
3. To prevent cave-ins from happening too often.

Protected blocks fulfill any or all of the conditions below:

* Has the Glass sound effect
* Is in the [disruption:protected] block tag

<br>
<br>

### 'Buoyant' blocks

Buoyant blocks are blocks that do not begin to fall if they are resting upon a liquid.
Keep in mind that the liquid does not count as a 'support' for these blocks.

There are three reasons why such blocks exist:

1. To prevent ice and ice sheets from falling into the sea.
2. To prevent magma blocks from falling into lava.
3. To allow Players to create boats and bridges on water/lava without the need of supports.

Buoyant blocks fulfill any or all of the conditions below:

* Is flammable
* Is slippery (slipperiness > 0.6)
* Is in the [disruption:buoyant] block tag

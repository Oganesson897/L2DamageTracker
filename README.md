# L2DamageTracker

This mod adds attack tracker and damage type multiplex. Attack tracker
is a way for mods to better listener to the attack event series, with
access to information that would have been modified in other events.

Damage Type Multiplex is a system that allows mods to change the
damage type based on scenarios, by enabling or disabling certain flags.
Similar to BlockState, all possible DamageType must be generated beforehand.

## Installation
Requires:
- L2Serial
- L2Library

Compatibility:
- L2Tabs

## Usage

### Attack Tracker
To register an attack tracker, use
`AttackEventHandler.register`.

To modify damage, you should use `AttackCache.addHurtModifier`
or `AttackCache.addDealtModifier`. They adds an entry to the pool
for later calculation. To synchronize calculations across mod, we
let mods add damage modifiers instead of modifying the damage values
directly. This allows better control over inter-mod behavior.

List of damage modifier types in order:
- `nonlinearPre`: Nonlinear modification before everything else.
- `multAttr`: Multiplicative modification, serves the same function
as `AttributeModifier` with operation of `MULTIPLY_TOTAL`.
- `add`: Additive modification for general purpose.
- `MultBase`: Multiplicative modification with `Operation.MULTIPLY_BASE`
- `multTotal`: Multiplicative modification for general purpose.
- `nonlinearMiddle`: Second nonlinear modification
- `addExtra`: Extra damage that should not be scaled by others 
- `nonlinearPost`: Final layer of nonlinear modification

### Damage Type Multiplex
To use damage type multiplex, you need to define your `DamageState` and
register them to corresponding `DamageTypeRoot`. Then you need to
configure generator to generate all relevant data.

To generate damage types, you must specify your namespace, and namespace
of `DamageState` that you would support. To prevent missing damage types,
mods should generate data with a clear dependency.

To set the damage state, use attack tracker to listen to
`CreateSourceEvent`, which will only fire to attack trackers.
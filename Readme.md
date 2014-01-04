# Metropolis World Generator

Authors: Thomas Richner, Aaron Brewer

In the future, wealthy industrialists rule the vast city of Metropolis from high-rise tower complexes, while a lower class of underground-dwelling workers toil constantly to operate the machines that provide its power. The Master of Metropolis is the ruthless Joh Fredersen (Alfred Abel), whose son Freder (Gustav Fröhlich) idles away his time in a pleasure garden with the other children of the rich. Freder is interrupted by the arrival of a young woman named Maria (Brigitte Helm), who has brought a group of workers' children to see the privileged lifestyle led by the rich. Maria and the children are quickly ushered away, but Freder is fascinated by Maria and descends to the workers' city in an attempt to find her.

----

## Schematic Configuration

#### buildName

Type: `string`

The name of the Schematic family

#### groundLevelY

Type: `number`

The exact level that this building will be sunk into a parcel.

#### decayIntensityInPercent

Type: `number`

Pecentage of decay that will be applied to the placed schematic.

#### decayExceptionMaterials

Type: `enum|array`

Enumerated name of materials that won't be decayed.

#### oddsOfAppearanceInPercent

Type: `number`

Percentage chance that this schematic will be placed.

#### lootMinLevel and lootMaxLevel

Type: `number`

Integer representing the min/max level of loot that gets placed in chests.

#### lootCollections

Type: `enum|array`

Valid loot contexts/collections used in chests.

#### chestOddsInPercent

Type: `number`

Percentage odds that chests placed in the schematic will be placed or removed.

#### spawnerOddsInPercent

Type: `number`

Percentage odds that spawners placed in the schematic will be placed or removed.

#### cutouts

Type: `object|array`

Cutout the sidewalk from the start point on the left edge of the schematic for the length passed, can take multipe cutouts.

```json
[
    {
        "startPoint": 3,
        "length": 8
    }
]
```

#### roadType

Type: `enum|string`

Only for roads, allows you to mark the direction of the road and if it's an intersection or a dead end.

#### roadFacing

Type: `boolean`

Does this schematic require a road adjacent for it to be placed?

#### context

Type: `enum|array`

An array of the enumerated name of the context that this schematic will appear in.

#### schematics

Type: `filename|array`

**TODO:** An array of schematics that this configuration will be applied to. Currently defaults to the name of the JSON file.

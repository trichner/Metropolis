# Metropolis World Generator

**Authors:** Thomas Richner, Aaron Brewer

**License:** [GNU General Public License, version 3 (GPL-3.0)](http://opensource.org/licenses/gpl-3.0)

**Website:** [vaast.org](http://vaast.org/)

In the future, wealthy industrialists rule the vast city of Metropolis from high-rise tower complexes, while a lower class of underground-dwelling workers toil constantly to operate the machines that provide its power. The Master of Metropolis is the ruthless Joh Fredersen (Alfred Abel), whose son Freder (Gustav Fröhlich) idles away his time in a pleasure garden with the other children of the rich. Freder is interrupted by the arrival of a young woman named Maria (Brigitte Helm), who has brought a group of workers' children to see the privileged lifestyle led by the rich. Maria and the children are quickly ushered away, but Freder is fascinated by Maria and descends to the workers' city in an attempt to find her.

## Basic Flow
As of commit [48aee](https://github.com/trichner/Metropolis/commit/48aee8f0a946d5edde7055ba6da30d85cba55de5). Most logic can be found in [District.java](https://github.com/trichner/Metropolis/blob/master/src/ch/k42/ch.k42.metropolis/grid/urbanGrid/districts/District.java).

1. start with a 64x64 chunk sized 'grid'
2. place half of a highway around the whole grid
3. start dividing the inner space randomly into two smaller spaces and put a road between them
4. for each of the smaller spaces, do 3. until the longer side is shorter than 'blockSize'
5. If the space is 1x1 chunk, place a schematic at all cost, prefering BUILD over FILLER, end of procedure
6. try to place a schematic in the current space, prefering BUILD over FILLER, no duplicates
7. if a schematic was placed, end of procedure
8. divide the space randomly into two smaller plots and repeat from 5. 
 

Space partitioning is done by 'Binary Space Partitioning', the cuts follow a normal distribution with mean size/2.

## Vagrant Development
Getting started with the plugin in a development environment is now done via [Vagrant](http://www.vagrantup.com/).

1. Download and install Vagrant: http://www.vagrantup.com/
2. Go to the /minecraft directory in your terminal.
3. Run "vagrant up" to start the server, it will grab Ubuntu 12 remotely and install all the dependencies as well as start the server on the default minecraft port.
4. Enter the virtual machine using "vagrant ssh", the minecraft folder is automounted at /vagrant.
5. run "./start.sh" to start the server, and "./rebuild.sh" to reset the database/delete world folders before starting.
 
## Plugin Configuration

```
consoleOutput:
    debug: true # Decides how verbose the console output is. If you have troubles, enable this and investigate.

generator:
    enableChestRenaming:  true   # Decides if all pasted chests should be named and randomly placed to fit chestlootz scheme
    enableSpawnerPlacing: true   # If sponges should be replaced with spawners, more configuration in schematic config
    enableDirectionFallbackPlacing: false # no purpose atm
    iterations: 10    # how many times the generator tries to partition a district, higher might give denser districts but slows down generations
    buildChance: 80   # the chance in percent that a schematic gets placed and the spot not further partitioned
    fillerChance: 80  #  the chance in percent that a filler schematic gets placed and the spot not further partitioned
    blockSize: 14     # after what size the generator stops to partition space with roads inbetween
    sigmaCut: 6       # the number of standart deviations the probability of a partition of a space has from the mean of size/2
    cloneRadius: 7    # the number of chunks in which placing checks for duplicates, distance is measured with the infinity norm
```

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

An array of schematics that this configuration will be applied to. Currently defaults to the name of the JSON file.
This will match any schematic with the same name, no matter what folder it is in, so make sure your names are somewhat unique.
This will also override any "schematic.json" files that are automatically bootstrapped.

```json
{
    "schematics": [
        "blue.schematic",
        "cyan.schematic",
        "gray.schematic",
        "green.schematic",
        "orange.schematic"
    ]
}
```

#### rotate

Type: `boolean`

Do not use rotated version of this schematic.

### Wow, such plugin, much work

_DAthmosSZLtk6LC1wJVcgdXchPXuhb1a9E_

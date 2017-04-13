# Schematic2Structure
Schematic2Structure is a utility to convert .schematic files to Minecraft's .nbt file format for use with the Structure block.
It's currently in a development stage, so not everything works properly yet. 

## Usage
1. Compile the source files

  if on windows: 
  - `javac -cp ";lib/JNBT_1.4.jar" src/*.java `
  
  
  if on UNIX/Mac
  - `javac -cp ";lib/JNBT_1.4.jar" src/*.java`

2. `java Schematic2Structure <schematic>`

where `<schematic>` is the filename of a [MCEdit](http://www.mcedit.net/) Schematic. 


## Known Issues
- blocks from MC 1.11 aren't included yet, and therefore don't work
- certain block rotation isn't yet fully supported (e.g logs)

## Notes
Structures must be at maximum 32x32x32 blocks (width x height x length). This is a requirement of Minecraft's structure block format

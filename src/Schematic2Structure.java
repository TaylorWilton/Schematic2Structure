import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Schematic2Structure {

    /**
     * Main Method
     *
     * @param args - the file name of the schematic
     */
    public static void main(String args[]) {
        String blocksFile = "blocks.csv";
        String propertiesFile = "properties.csv";

        // TODO: Validation
        String schematicFile = args[0];

        // hashmaps for blocks & properties respectively
        HashMap<Integer, String> blockMap = new HashMap<>();
        HashMap<String, String> propertiesMap = new HashMap<>();

        HashMap<Integer, Block> palette = new HashMap<>();
        ArrayList<Block> structureBlocks = new ArrayList<>();

        NBTInputStream schematicNBT;

        String line;

        try {

            FileReader fileReader = new FileReader(blocksFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            /*
             * Read the blocklist csv file, and add all the items to the hashmap created above
             */
            while ((line = bufferedReader.readLine()) != null) {
                /*
                 * Split the line
                 * The data format is as follows:
                 * data[0] - the block id
                 * data[1] - the English name of the block (ex Stone)
                 * data[2] - the Minecraft name of the block (ex minecraft:stone)
                 *
                 * data[1] currently has no use
                 */

                String[] data = line.split(",");
                // the blockID that Minecraft uses
                int id = Integer.parseInt(data[0]);
                // the block name that Minecraft uses (ex minecraft:stone, rather than Stone)
                String blockName = data[2];

                // add to the map
                blockMap.put(id, blockName);

            }
            // close current file
            fileReader.close();

            // open the properties file
            fileReader = new FileReader(propertiesFile);
            bufferedReader = new BufferedReader(fileReader);

            // Read the properties file
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split("\\|");

                String key = data[0];
                String properties = data[1];
                propertiesMap.put(key, properties);

            }

            // Open a new filestream for the schematic
            FileInputStream fis = new FileInputStream(schematicFile);
            schematicNBT = new NBTInputStream(fis);

            // get a CompoundMap of the schematic
            CompoundMap schematicMap = (CompoundMap) schematicNBT.readTag().getValue();

            short height = (short) ((Tag) schematicMap.get("Height")).getValue();
            short width = (short) ((Tag) schematicMap.get("Width")).getValue();
            short length = (short) ((Tag) schematicMap.get("Length")).getValue();

            ArrayList<ShortTag> sizeList = new ArrayList<>();
            sizeList.add(new ShortTag("",length));
            sizeList.add(new ShortTag("", height));
            sizeList.add(new ShortTag("", width));

            ListTag sizeListTag = new ListTag("size",ShortTag.class,sizeList);

            // validate the dimensions to ensure that structure is the right size
            // (i.e 32 blocks or smaller in each dimension)
            // this is done now, so that time isn't wasted reading all the blocks
            if (!validateStructure(height, width, length)) {
                System.out.println("Structure is too large!");
                return;
            }

            byte[] schematicBlocks = (byte[]) ((Tag) schematicMap.get("Blocks")).getValue();
            byte[] schematicBlockData = (byte[]) ((Tag) schematicMap.get("Data")).getValue();

            for (int i = 0; i < schematicBlocks.length; i++) {
                int blockId = schematicBlocks[i];
                byte data = schematicBlockData[i];

                String name = blockMap.get(blockId);

                Block b = new Block(blockId, name, data);

                // add to the list of blocks
                structureBlocks.add(b);

                int hash = b.hashCode();
                String key = b.getKey();
                // get associated properties and set them for the block
                if (propertiesMap.containsKey(key)) {
                    b.setProperties(propertiesMap.get(key));
                }
                // if the block isn't already in the palette, add it now
                if (!palette.containsKey(hash)) {
                    palette.put(hash, b);
                }

            }

            // ArrayList of compound tags, that will eventually become part of the palette in the structure
            ArrayList<CompoundTag> paletteCompoundList = new ArrayList<>();

            // Loop over items in palette
            for (int i = 0; i < palette.values().toArray().length; i++) {
                // current block
                Block current = (Block) (palette.values().toArray())[i];

                if(current.getName() == null){
                    continue;
                }

                // properties
                String blockProperties = current.getProperties();

                // if the block has properties then loop though them and add them to a compound list
                if (blockProperties != null && blockProperties.length() > 0) {
                    CompoundMap blockMapCompound = new CompoundMap();
                    String[] blockPropertiesArray = blockProperties.split(",");
                    CompoundMap propertiesMapCompound = new CompoundMap();

                    // Loop through the block properties
                    for (int j = 0; j < blockPropertiesArray.length; j++) {
                        String[] result = blockPropertiesArray[j].split(":");
                        propertiesMapCompound.put(new StringTag(result[0], result[1]));
                    }

                    blockMapCompound.put("Properties", new CompoundTag("Properties", propertiesMapCompound));
                    blockMapCompound.put("Name", new StringTag("Name", current.getName()));
                    paletteCompoundList.add(new CompoundTag("", blockMapCompound));

                } else { // otherwise just make a compound tag.
                    CompoundMap blockMapCompound = new CompoundMap();
                    blockMapCompound.put("Name", new StringTag("Name", current.getName()));
                    paletteCompoundList.add(new CompoundTag("", blockMapCompound));
                }

            }
            ListTag paletteListTag = new ListTag("palette", CompoundTag.class, paletteCompoundList);
            ArrayList<CompoundTag> blockCompoundList = new ArrayList<>();

            Object[] hashesArray = palette.keySet().toArray();

            ArrayList<Integer> paletteHashes = new ArrayList<>();

            for (Object aHashesArray : hashesArray) {
                paletteHashes.add((Integer) aHashesArray);
            }

            // loop over all the blocks
            //Sorted by height (bottom to top) then length then width -- the index of the block at X,Y,Z is (Y×length + Z)×width + X

            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int position = (y * length + z) * width + x;
                        Block current = structureBlocks.get(position);

                        int index = paletteHashes.indexOf(current.hashCode());

                        CompoundMap itemMap = new CompoundMap();
                        ArrayList<IntTag> pos = new ArrayList<>(3);
                        pos.add(new IntTag("", z));
                        pos.add(new IntTag("", y));
                        pos.add(new IntTag("", x));

                        itemMap.put("pos", new ListTag("pos", IntTag.class, pos));
                        itemMap.put("state", new IntTag("state", index));

                        CompoundTag blockCompound = new CompoundTag("", itemMap);

                        blockCompoundList.add(blockCompound);
                    }
                }
            }

            ListTag blockListTag = new ListTag("blocks", CompoundTag.class, blockCompoundList);

            CompoundMap structureMap = new CompoundMap();
            structureMap.put("blocks",blockListTag);
            structureMap.put("palette",paletteListTag);
            structureMap.put("size",sizeListTag);
            structureMap.put(new StringTag("author", "KingAmles"));
            structureMap.put(new IntTag("version",1));

            CompoundTag structureTag = new CompoundTag("structure",structureMap);

            System.out.println(structureTag);

            FileOutputStream fos = new FileOutputStream("output.nbt");
            NBTOutputStream NBToutput = new NBTOutputStream(fos);

            NBToutput.writeTag(structureTag);

            NBToutput.flush();

            NBToutput.close();


        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error reading file");
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid number");
        }

    }

    static private boolean validateStructure(short h, short w, short l) {
        return (h < 33 && w < 33 && l < 33);
    }
}

import org.jnbt.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Schematic2Structure {

    /**
     * Main Method
     *
     * @param args - the file name of the schematic
     */
    public static void main(String args[]) {
        String blocksFile = "blocks.csv";
        String propertiesFile = "properties.csv";
        String schematicFile;

        if (!validateSchematicFile(args[0])) {
            return;
        } else {
            schematicFile = args[0];
        }

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

            // get a HashMap of the schematic
            Map<String, Tag> schematicMap = (Map<String, Tag>) schematicNBT.readTag().getValue();

            short height = (short) ((Tag) schematicMap.get("Height")).getValue();
            short width = (short) ((Tag) schematicMap.get("Width")).getValue();
            short length = (short) ((Tag) schematicMap.get("Length")).getValue();

            ArrayList<Tag> sizeList = new ArrayList<>();
            sizeList.add(new IntTag("length", length));
            sizeList.add(new IntTag("width", height));
            sizeList.add(new IntTag("height", width));


            ListTag sizeListTag = new ListTag("size", IntTag.class, sizeList);

            // validate the dimensions to ensure that structure is the right size
            // (i.e 32 blocks or smaller in each dimension)
            // this is done now, so that time isn't wasted reading all the blocks
            if (!validateStructure(height, width, length)) {
                System.out.println("Structure is too large!");
                return;
            }


            // note - java byte's are signed...
            byte[] schematicBlocks = (byte[]) schematicMap.get("Blocks").getValue();
            byte[] schematicBlockData = (byte[]) schematicMap.get("Data").getValue();

            for (int i = 0; i < schematicBlocks.length; i++) {
                int blockId = schematicBlocks[i] & 0xff;
                int data = schematicBlockData[i] & 0xff;

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
            ArrayList<Tag> paletteCompoundList = new ArrayList<>();

            // Loop over items in palette
            for (int i = 0; i < palette.values().toArray().length; i++) {
                // current block
                Block current = (Block) (palette.values().toArray())[i];

                // properties
                String blockProperties = current.getProperties();

                // if the block has properties then loop though them and add them to a compound list
                if (blockProperties != null && blockProperties.length() > 0) {
                    HashMap<String, Tag> blockMapCompound = new HashMap<String, Tag>();
                    String[] blockPropertiesArray = blockProperties.split(",");
                    HashMap<String, Tag> propertiesMapCompound = new HashMap<String, Tag>();

                    // Loop through the block properties
                    for (int j = 0; j < blockPropertiesArray.length; j++) {
                        String blockProperty = blockPropertiesArray[j];
                        String[] result = blockProperty.split(":");
                        propertiesMapCompound.put(String.valueOf(j), new StringTag(result[0], result[1]));
                    }
                    blockMapCompound.put("Properties", new CompoundTag("Properties", propertiesMapCompound));
                    blockMapCompound.put("Name", new StringTag("Name", current.getName()));
                    paletteCompoundList.add(new CompoundTag("block", blockMapCompound));

                } else { // otherwise just make a compound tag.
                    HashMap<String, Tag> blockMapCompound = new HashMap<>();
                    blockMapCompound.put("Name", new StringTag("Name", current.getName()));
                    paletteCompoundList.add(new CompoundTag("block", blockMapCompound));
                }

            }
            ListTag paletteListTag = new ListTag("palette", CompoundTag.class, paletteCompoundList);
            ArrayList<Tag> blockCompoundList = new ArrayList<>();

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

                        HashMap<String, Tag> itemMap = new HashMap<String, Tag>();
                        ArrayList<Tag> pos = new ArrayList<>(3);

                        pos.add(new IntTag("x", z));
                        pos.add(new IntTag("y", y));
                        pos.add(new IntTag("z", x));


                        itemMap.put("pos", new ListTag("pos", IntTag.class, pos));
                        itemMap.put("state", new IntTag("state", index));

                        CompoundTag blockCompound = new CompoundTag("block", itemMap);

                        blockCompoundList.add(blockCompound);
                    }
                }
            }

            ListTag blockListTag = new ListTag("blocks", CompoundTag.class, blockCompoundList);

            HashMap<String, Tag> structureMap = new HashMap<>();
            structureMap.put("blocks", blockListTag);
            structureMap.put("palette", paletteListTag);
            structureMap.put("size", sizeListTag);
            structureMap.put("author", new StringTag("author", "KingAmles"));
            structureMap.put("version", new IntTag("version", 1));

            CompoundTag structureTag = new CompoundTag("structure", structureMap);

            //System.out.println(structureTag);

            String output = (schematicFile.split("\\."))[0] + ".nbt";
            System.out.println(output);
            FileOutputStream fos = new FileOutputStream(output);
            NBTOutputStream NBToutput = new NBTOutputStream(fos);

            NBToutput.writeTag(structureTag);
            NBToutput.close();


        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error reading file");
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid number");
        }

    }

    /**
     * validates the structure parameters to make sure that the structure is a valid size
     *
     * @param h height
     * @param w width
     * @param l length
     * @return true if valid, false if invalid
     */
    static boolean validateStructure(int h, int w, int l) {
        return ((h < 33 && w < 33 && l < 33) && (h > 0 && w > 0 && l > 0));
    }

    /**
     * Validation of the schematic filename
     * first - test to see if it ends with '.schematic'
     *
     * @param schematicFilename the name of the file we're opening
     * @return whether the file is a valid schematic or not
     */
    static boolean validateSchematicFile(String schematicFilename){
        String ext = (schematicFilename.split("\\."))[1];
        return ext.compareTo("schematic") == 0;
    }
}

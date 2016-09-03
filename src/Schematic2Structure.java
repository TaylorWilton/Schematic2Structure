import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.gui.NBTViewer;
import com.flowpowered.nbt.stream.NBTInputStream;

import java.io.*;
import java.util.HashMap;

public class Schematic2Structure {


    /**
     * Main Method
     * @param args - the file name of the schematic
     */
    public static void main(String args[]){
        String blocksFile = "blocks.csv";
        String propertiesFile = "properties.csv";

        // TODO: Validation
        String schematicFile = args[0];

        // hashmaps for blocks & properties respectively
        HashMap<Integer,String> blockMap = new HashMap<>();
        HashMap<String,String> propertiesMap = new HashMap<>();

        NBTInputStream schematicNBT;


        String line;

        try {

            FileReader fileReader = new FileReader(blocksFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            /*
             * Read the blocklist csv file, and add all the items to the hashmap created above
             */
            while((line = bufferedReader.readLine()) != null){
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
                blockMap.put(id,blockName);

            }
            // close current file
            fileReader.close();

            // open the properties file
            fileReader = new FileReader(propertiesFile);
            bufferedReader = new BufferedReader(fileReader);

            // Read the properties file
            while((line = bufferedReader.readLine())!= null){
                String[] data = line.split("|");

                String key = data[0];
                String properties = data[1];
                propertiesMap.put(key,properties);

            }

            FileInputStream inputStream = new FileInputStream(schematicFile);
            schematicNBT = new NBTInputStream(inputStream);

            Tag<CompoundMap> root = schematicNBT.readTag();

            CompoundMap m = root.getValue();

            System.out.println(m.values());



        } catch (FileNotFoundException ex){
            System.out.println("Unable to open file " + ex.getMessage());
        } catch (IOException ex){
            System.out.println("Error reading file");
        } catch (NumberFormatException ex){
            System.out.println("Not a valid number");
        }

    }
}

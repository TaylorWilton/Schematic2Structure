import org.jnbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Entity {
    private CompoundTag entityTag;

    /**
     * Entity Class - deals with all the fun parts of NBT!
     *
     * @param ct - the root tag of the entity
     */
    Entity(CompoundTag ct) {
        // get a map of all the tags
        Map<String, Tag> entityMap = ct.getValue();

        // get pos list, we can reuse it
        List<Tag> posList = (List<Tag>) entityMap.get("Pos").getValue();

        // block pos is just integer version of normal position
        ArrayList<Tag> blockPosList = new ArrayList<>();
        blockPosList.add(new IntTag("x", ((DoubleTag) posList.get(0)).getValue().intValue()));
        blockPosList.add(new IntTag("y", ((DoubleTag) posList.get(1)).getValue().intValue()));
        blockPosList.add(new IntTag("z", ((DoubleTag) posList.get(2)).getValue().intValue()));

        // save compound tag, so we can deal with the rest of the stuff later

        // make a hashmap for the tags
        HashMap<String, Tag> entity = new HashMap<>();
        // chuck everything in it
        entity.put("pos", new ListTag("pos", DoubleTag.class, posList));
        entity.put("blockPos", new ListTag("blockPos", IntTag.class, blockPosList));

        // get the data, put it in a map
        Map<String, Tag> rootTag = ct.getValue();
        // then make it a tag again
        // just so it says "nbt"
        CompoundTag entityNBTTag = new CompoundTag("nbt", rootTag);


        entity.put("nbt", entityNBTTag);// hope this works
        // make it a tag!
        entityTag = new CompoundTag("Entity", entity);

    }

    /**
     * getStructureFormat
     *
     * @return - a CompoundTag formatted correctly for Mojang's NBT Structure Specifications
     */
    CompoundTag getStructureFormat() {
        return entityTag;
    }
}

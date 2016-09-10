import org.jnbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    private String id;
    private CompoundTag root;
    private ArrayList<Tag> blockPosList;
    private ArrayList<Tag> posList;
    private CompoundTag entityTag;

    /**
     * Entity Class - deals with all the fun parts of NBT!
     *
     * @param ct - the root tag of the entity
     */
    public Entity(CompoundTag ct) {
        // get a map of all the tags
        Map<String, Tag> entityMap = ct.getValue();

        // get pos list, we can reuse it
        List<Tag> posList = (List<Tag>) entityMap.get("Pos");

        // block pos is just integer version of normal position
        ArrayList<Tag> blockPosList = new ArrayList<>();
        blockPosList.add(new IntTag("x", ((DoubleTag)posList.get(0)).getValue().intValue()));
        blockPosList.add(new IntTag("y", ((DoubleTag)posList.get(1)).getValue().intValue()));
        blockPosList.add(new IntTag("z", ((DoubleTag)posList.get(2)).getValue().intValue()));


        // save for further use
        this.posList = (ArrayList<Tag>) posList;
        this.blockPosList = blockPosList;

        // get id
        this.id = (String) entityMap.get("id").getValue();

        // save compound tag, so we can deal with the rest of the stuff later
        root = ct;

        // make a hashmap for the tags
        HashMap<String, Tag> entity = new HashMap<>();
        // chuck everything in it
        entity.put("pos", new ListTag("pos", DoubleTag.class, posList));
        entity.put("blockPos", new ListTag("blockPos", IntTag.class, blockPosList));
        entity.put("nbt", root);// hope this works
        // make it a tag!
        entityTag = new CompoundTag("Entity", entity);

    }

    /**
     * getStructureFormat
     *
     * @return - a CompoundTag formatted correctly for Mojang's NBT Structure Specifications
     */
    public CompoundTag getStructureFormat() {
        return entityTag;
    }
}

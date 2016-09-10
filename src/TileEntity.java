import org.jnbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntity {
    // the tag in Structure format
    private CompoundTag tileEntityTag;

    public TileEntity(CompoundTag ct) {
        // get the map of everything
        Map<String, Tag> tileEntityMap = ct.getValue();

        // make list of tags
        // is generic type for later use
        ArrayList<Tag> posList = new ArrayList<>();
        posList.add(new IntTag("x", ((IntTag) tileEntityMap.get("x")).getValue()));
        posList.add(new IntTag("y", ((IntTag) tileEntityMap.get("y")).getValue()));
        posList.add(new IntTag("z", ((IntTag) tileEntityMap.get("z")).getValue()));

        // start combining everything together
        HashMap<String, Tag> tileEntity = new HashMap<>();
        tileEntity.put("blockPos", new ListTag("pos", IntTag.class, posList));
        CompoundTag tileEntityNBTTag = new CompoundTag("nbt", tileEntityMap);
        tileEntity.put("nbt", tileEntityNBTTag);

        // make it a tag!
        tileEntityTag = new CompoundTag("TileEntity", tileEntity);
    }

    /**
     * getStructureFormat
     *
     * @return - a CompoundTag formatted correctly for Mojang's NBT Structure Specifications
     */
    CompoundTag getStructureFormat() {
        return tileEntityTag;
    }

}

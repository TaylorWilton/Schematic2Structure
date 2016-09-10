import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.util.List;
import java.util.Map;

public class Entity {
    double[] pos;
    int[] blockPos;
    String id;
    CompoundTag root;

    /**
     * Entity Class - deals with all the fun parts of NBT!
     * @param ct
     */
    public Entity(CompoundTag ct){

        Map<String, Tag> entityMap =  ct.getValue();

        List<Double> posList = (List<Double>) entityMap.get("Pos");

        // set position
        pos[0] = posList.get(0);
        pos[1] = posList.get(1);
        pos[2] = posList.get(2);
        // block pos is just integer version of normal position
        blockPos[0] = (int) pos[0];
        blockPos[1] = (int) pos[1];
        blockPos[2] = (int) pos[2];

        // get id
        this.id = (String) entityMap.get("id").getValue();

        // save compound tag, so we can deal with the rest of the stuff later
        root = ct;
    }
}

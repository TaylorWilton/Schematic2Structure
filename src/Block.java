/**
 * Block Class - Contains information about each individual block in the structure
 */
public class Block {
    private int id;
    private String properties;
    private String key;
    private int data;
    private String name;

    /**
     * Block Constructor, creates individual block
     *
     * @param id   - block id associated with this block
     * @param name - name of the block
     * @param data - any block data
     */
    public Block(int id, String name, int data) {
        this.id = id;
        this.name = name;
        this.data = data;

        // set the key of the object
        this.key = id + ":" + data;

    }

    /**
     * generate a hashcode for each type of block, used for the palette
     *
     * the hashCode is unique to each different type of block, rather than to each unique block.
     * It is used for checking if a block is already in the palette or not.
     *
     * @return - the hashcode corresponding to this type of block;
     */
    @Override
    public int hashCode() {

        return ((id * 10) + data) * 10;
    }

    public String getProperties() {
        return properties;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setProperties(String _properties) {
        this.properties = _properties;
    }
}

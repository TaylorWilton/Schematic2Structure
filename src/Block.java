/**
 * Block Class - Contains information about each individual block in the structure
 */
public class Block {
    private int _id;
    private String _properties;
    private String _key;
    private String _blockProperties;
    private byte _data;
    private String _name;

    /**
     * Block Constructor, creates individual block
     *
     * @param _id   - block id associated with this block
     * @param _name - name of the block
     * @param _data - any block data
     */
    public Block(int _id, String _name, byte _data) {
        this._id = _id;
        this._name = _name;

        // set the key of the object
        this._key = _id + ":" + _data;

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

        return ((_id * 10) + _data) * 10;
    }

    public byte getData() {
        return _data;
    }

    public String getProperties() {
        return _properties;
    }

    public String getKey() {
        return _key;
    }

    public String getName() {
        return _name;
    }

    public void setProperties(String _properties) {
        this._properties = _properties;
    }
}

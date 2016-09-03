/**
 * Block Class - Contains information about each individual block in the structure
 */
public class Block {
    private int _id;
    private String _properties;
    private String _key;
    private int _x;
    private int _y;
    private int _z;
    private String _blockProperties;
    private byte _data;
    private String _name;

    /**
     * Block Constructor, creates individual block
     * @param _id - block id associated with this block
     * @param _name - name of the block
     * @param _data - any block data
     * @param _y - ypos
     * @param _z - zpos
     * @param _x xpos
     */
    public Block(int _id, String _name, byte _data, int _y, int _z, int _x) {
        this._id = _id;
        this._name = _name;
        this._data = _data;
        this._y = _y;
        this._z = _z;
        this._x = _x;

        // set the key of the object
        this._key = _id + ":" + _data;

    }

    public byte get_data() {
        return _data;
    }

    public String get_properties() {
        return _properties;
    }

    public String get_key() {
        return _key;
    }

    public String get_name() {
        return _name;
    }



    public void set_properties(String _properties) {
        this._properties = _properties;
    }
}

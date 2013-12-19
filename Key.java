/**
 * Key handler
 */
public class Key {
    boolean owned;
    
    public Key() {
        owned = false;
    }
    
    /**
     * key is owned atm.
     */
    public boolean isOwned() {
        return owned;
    }
    
    /**
     * key is obtained, and is now owned.
     */
    public void claim() {
        owned = true;
    }
}
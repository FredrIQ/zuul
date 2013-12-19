/**
 * Key handler
 */
public class Key {
    boolean owned;
    String identifier;
    
    public Key(String identifier) {
        owned = false;
        this.identifier = identifier;
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
    
    public String toString() {
        return identifier;
    }
}
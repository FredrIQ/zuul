import java.util.Set;
import java.util.HashMap;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Fredrik Ljungdahl, Michael Kölling and David J. Barnes
 * @version 2013.12.19
 */

public class Room {
    private String description;
    private Key key;
    private HashMap<String, Room> exits; // stores exits of this room.
    private HashMap<String, String> exitInfo; // stores properties of the exits

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description) {
        initRoom(description, null);
    }
    
    /**
     * Create a room, but clarify whether or not it has a key.
     * @param description The room's description.
     * @param hasKey Whether or not the room contains a key.
     */
    public Room(String description, Key key) {
        initRoom(description, key);
    }
    
    /**
     * Actual room initialization.
     */
    private void initRoom(String description, Key key) {
        this.description = description;
        this.key = key;
        exits = new HashMap<String, Room>();
        exitInfo = new HashMap<String, String>();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) {
        setExit(direction, neighbor, "ok");
    }
    
    /**
     * Define a locked exit.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     * @param key       The key needed to open the door.
     */
    public void setExit(String direction, Room neighbor, Key key) {
        exitInfo.put(direction + "Key", key.toString());
        setExit(direction, neighbor, "locked");
    }
    
    /**
     * Define a special exit from this room (one-way, locked, etc).
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     * @param state     The state of the exit (default "ok" - i.e. locked, trapped, ok)
     */
    public void setExit(String direction, Room neighbor, String state) {
        exits.put(direction, neighbor);
        exitInfo.put(direction + "State", state);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription() {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Gets the state of a specific exit
     */
    public String getState(String direction) {
        return exitInfo.get(direction + "State");
    }
    
    /**
     * Sets the exit state.
     */
    public void setState(String direction, String state) {
        exitInfo.put(direction + "State", state);
    }
    
    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString() {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }
    
    /**
     * Get the room key lock identifier.
     */
    public String getExitKey(String direction) {
        return exitInfo.get(direction + "Key");
    }
    
    /**
     * Checks if the room has the key.
     */
    public boolean hasKey() {
        if (this.key == null) {
            return false;
        }
        return true;
    }
    
    /**
     * Get the room key.
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get the key identifer.
     */
    public String getKeyInfo() {
        return key.toString();
    }
}


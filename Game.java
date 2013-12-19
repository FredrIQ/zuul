/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Fredrik Ljungdahl, Michael Kölling and David J. Barnes
 * @version 2013.12.19
 */

public class Game {
    private Parser parser;
    private Timer timer;
    private Key key;
    private Room currentRoom;
    private Room beamerRoom;
    
    /**
     * Starts the game
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        timer = new Timer(60, -1, 5);
        key = new Key();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room outside, theater, pub, lab, office, classroom;
      
        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater", true);
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        classroom = new Room("in a plain classroom");
        
        // initialise room exits
        outside.setExit("east", theater);
        outside.setExit("south", classroom);
        outside.setExit("west", pub);

        theater.setExit("west", outside);
        theater.setExit("south", lab);

        pub.setExit("east", outside);

        classroom.setExit("north", outside);
        classroom.setExit("east", lab);
        
        lab.setExit("north", theater, "trapdoor");
        lab.setExit("east", office, "locked");
        lab.setExit("west", classroom);

        office.setExit("west", lab);

        currentRoom = outside;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println("You have "+timer+"s to win.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean quitGame = false;
        boolean updateTimer = true;

        String commandWord = command.getCommandWord();
        if (commandWord == null) { // unknown command
            System.out.println("Huh? I don't understand what you're talking about...");
            updateTimer = false;
            return false;
        }
        switch (commandWord) {
            case "back":
                gotoWaypoint();
                break;

            case "go":
                goRoom(command);
                break;

            case "help":
                printHelp();
                updateTimer = false; // this is metagaming, don't bother with the timer
                break;

            case "mark":
                setWaypoint(currentRoom);
                break;

            case "quit":
                quitGame = quit(command);
                updateTimer = false;
                break;

        }
        if (updateTimer) {
            timer.updateTimer();
            if (timer.hasExpired()) {
                System.out.println("Time's up - you lost!");
                quitGame = true;
            } else if (timer.isLow()) {
                System.out.println("Time is running low!");
                System.out.println("You have "+timer+"s left...");
            }
        }
        return quitGame;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) { // there's nothing in that direction
            System.out.println("There's nothing there!");
            return;
        }
        if (key.isOwned() && currentRoom.getState(direction) == "locked") {
            currentRoom.setState(direction, "ok");
            System.out.println("You unlocked the door!");
        }
        
        switch (currentRoom.getState(direction)) {
            case "locked":
                System.out.println("That door is locked! You can unlock it with a key, though.");
                break;

            case "ok":
                currentRoom = nextRoom;
                getRoomInfo();
                break;

            case "trapdoor":
                System.out.println("That way can only be taken from the other side!");
                break;

            default:
                System.out.println("Internal error. Please file a bug report.");
                break;

        }
    }
    
    /**
     * Retrieves room information.
     */
    private void getRoomInfo() {
        System.out.println(currentRoom.getLongDescription());
        if (currentRoom.hasKey() && !key.isOwned()) {
            System.out.println("You found a key!");
            key.claim();
        }
    }
    
    /**
     * Waypoint (beamer) - allows you to "remember" this place.
     * This way, you can always go back to the place,
     * unless you mark a new point.
     */
    private void setWaypoint(Room room) {
        beamerRoom = currentRoom;
        System.out.println("You've put this room in your memory.");
        System.out.println("Now, you can go back to this room whenever you want to with 'back'.");
    }
    private boolean gotoWaypoint() {
        if (beamerRoom == null) {
            System.out.println("You never bothered to remember any place...");
            return false;
        }
        currentRoom = beamerRoom;
        System.out.println("You went back!");
        getRoomInfo();
        return true;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        return true;
    }
}

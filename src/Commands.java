public enum Commands {
    REMOVE_ALL(2),
    REMOVE(2),
    ADD(2),
    IMPORT(1),
    SHOW(0),
    INFO(0),
    REMOVE_FIRST(0),
    HELP(0);

    int commandType;

    Commands(int commandType) {
        this.commandType = commandType;
    }
}
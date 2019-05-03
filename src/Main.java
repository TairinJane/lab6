import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This is my console app class that do some disgusting things.
 * Have fun in destroying it :c
 *
 * @author Kozhemyako Anna P3111
 * @version 0.1
 */
public class Main {

    /**
     * Queue to hold Clouds.
     *//*
    private static PriorityBlockingQueue<Cloud> queue= new PriorityBlockingQueue<>();
    *//**
     * GSON library object to parse Json.
     *//*
    private static Gson gson = new Gson();
    *//**
     * GSON library object to make pretty Json objects.
     *//*
    private static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    *//**
     * Field for holding date when app was opened.
     *//*
    private static Date openTime = new Date();

    *//**
     * Removing from the collection all items equivalent to the given.
     *
     * @param jsonString json object you want to remove
     *//*
    private static void removeAll(String jsonString) {
        try {
            Cloud cloud = gson.fromJson(jsonString, Cloud.class);
            while (queue.contains(cloud)) {
                queue.remove(cloud);
            }
            System.out.println("Elements were removed");
            saveCollection();
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid json!"+
                    "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                    "color\": \"white\"}" +
                    "\nYou can use only nickname or speed if you want.");
        }
    }

    *//**
     * Removing from the collection one item equivalent to the given.
     * @param jsonString json object you want to remove
     *//*
    private static void removeElement(String jsonString) {
        try {
            Cloud cloud = gson.fromJson(jsonString, Cloud.class);
            queue.remove(cloud);
            System.out.println("Element was removed");
            saveCollection();
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid json!"+
                    "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                    "color\": \"white\"}" +
                    "\nYou can use only nickname or speed if you want.");
        }
    }

    *//**
     * Prints all elements in collection
     *//*
    private static void show() {
        System.out.println(prettyGson.toJson(queue));
    }

    *//**
     * Removes first element in collection (with highest speed).
     *//*
    private static void removeFirst() {
        Cloud element = queue.poll();
        if (element==null) {
            System.out.println("There are no elements in queue");
            saveCollection();
        } else {
            System.out.println("First element was removed." +
                    "\nIt was cloud named " + element.getNickname() +
                    " with speed = "+element.getSpeed());
        }
    }

    *//**
     * Shows list of all commands.
     *//*
    private static void help() {
        System.out.println("\nremove_all {element}: удалить из коллекции все элементы, эквивалентные заданному\n" +
                "remove {element}: удалить элемент из коллекции по его значению\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "remove_first: удалить первый элемент из коллекции\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "import {String path}: добавить в коллекцию все данные из файла\n" +
                "add {element}: добавить новый элемент в коллекцию"+
                "\n\nexit: выход из программы"+
                "\n\n{element} выглядит как {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                "\"color\": \"white\"}");
    }

    *//**
     * Shows information about collection.
     *//*
    private static void info() {
        System.out.println("Collection size: "+ queue.size() +
                "\nType: PriorityQueue" +
                "\nOpen time: "+ openTime);
    }

    *//**
     * Imports all elements given in file.
     *
     * @param filePath path to file
     *//*
    private static void importCollection(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Type queueType = new TypeToken<PriorityQueue<Cloud>>(){}.getType();
            String line;
            String fileLines = "";
            while ((line = br.readLine()) != null) {
                fileLines += line;
            }
            PriorityQueue<Cloud> importQueue = gson.fromJson(fileLines, queueType);
            for (Cloud cloud : importQueue) {
                queue.add(cloud);
            }
            saveCollection();
            System.out.println("All elements imported in collection");
        } catch (IOException e) {
            System.out.println("No such file!");
        }
    }

    *//**
     * Adds element in collection. If there is no element given, adds default element.
     *
     * @param jsonString json object you want to add
     *//*
    private static void addElement(String jsonString) {
        try {
            Cloud cloud = gson.fromJson(jsonString, Cloud.class);
            queue.add(cloud);
            System.out.println("Element was added");
            saveCollection();
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid json!"+
                    "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                    "color\": \"white\"}" +
                    "\nYou can use only nickname or speed if you want.");
        }
    }

    *//**
     * Void to save collection to 'outputCollection.txt'.
     *//*
    private static void saveCollection() {
        try (PrintWriter pw = new PrintWriter(new File("outputCollection.txt"))) {
            pw.write(prettyGson.toJson(queue));
        } catch (FileNotFoundException e) {
            System.out.println("No such file to save this!");
        }
    }

    *//**
     * Main void to run console app.
     * @param args console arguments to pass the path for file with collection.
     *//*
    public static void main(String[] args) {

        if (args.length != 0) {
            importCollection(args[0]);
        } else {
            System.out.println("No collection given in command line to import");
        }

        importCollection("outputCollection.txt");
        for (int i = 0; i<10; i++) {
            Cloud cloud = new Cloud();
            cloud.setRider(new MumyTroll("Troll"));
            queue.add(cloud);
        }

        System.out.println("Print your command here. Use 'help' for help.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine())!=null && !line.equals("exit")) {
                line = line.trim();
                String command = line.split(" ", 2)[0];
                String object;
                if (line.contains(" ")) {
                    object = line.split(" ", 2)[1];
                    switch (command) {
                        case "remove_all": removeAll(object); break;
                        case "remove": removeElement(object); break;
                        case "import": importCollection(object); break;
                        case "add": addElement(object); break;
                        default:
                            System.out.println("No such command. Write 'help' to see commands.");
                    }
                } else
                    switch (command) {
                        case "show": show(); break;
                        case "info": info(); break;
                        case "remove_first": removeFirst(); break;
                        case "help": help(); break;
                        default:
                            System.out.println("No such command. Write 'help' to see commands.");
                    }
            }
        } catch (IOException e) {
            System.out.println("Can't read this. Give me beautiful words.");
        }


        if (new File("outputCollection.txt").canWrite()) {
            saveCollection();
            System.out.println("Collection was saved to 'outputCollection.txt'");
        } else {
            System.out.println("No permission to write 'outputCollection.txt'. Collection wasn't saved");
        }

    }*/
}

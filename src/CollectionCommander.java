import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

class CollectionCommander {
    private static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private static PriorityBlockingQueue<Cloud> queue = new PriorityBlockingQueue<>();
    private static Date openTime = new Date();

    static String removeAll(Cloud cloud) {
        if (queue.isEmpty()) return "Queue is empty!";
        while (queue.contains(cloud)) {
            queue.remove(cloud);
        }
        saveCollection();
        return "Elements like this were removed";
    }

    static String removeElement(Cloud cloud) {
        if (queue.remove(cloud)) {
            saveCollection();
            return "Element was removed";
        }
        return "No such element";
    }

    static String addElement(Cloud cloud) {
        if (queue.add(cloud)) {
            saveCollection();
            return "Element was added";
        }
        return "Element wasn't added";
    }

    static String show() {
        if (queue.isEmpty()) return "Queue is empty";
        return prettyGson.toJson(queue);
    }

    static String removeFirst() {
        Cloud element = queue.poll();
        saveCollection();
        if (element == null) {
            return "There are no elements in queue";
        } else {
            return "First element was removed.";
        }
    }

    static String help() {
        return "\nremove_all {element}: удалить из коллекции все элементы, эквивалентные заданному\n" +
                "remove {element}: удалить элемент из коллекции по его значению\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "remove_first: удалить первый элемент из коллекции\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "import {String path}: добавить в коллекцию все данные из файла\n" +
                "add {element}: добавить новый элемент в коллекцию" +
                "\n\nquit: выход из программы" +
                "\n\n{element} выглядит как {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                "\"color\": \"white\"}";
    }

    static String info() {
        return "Collection size: " + queue.size() +
                "\nType: PriorityQueue" +
                "\nOpen time: " + openTime;
    }

    static String importCollection(PriorityBlockingQueue<Cloud> importQueue) {
        if (queue.addAll(importQueue)) {
            saveCollection();
            return "All elements imported in collection";
        }
        return "Elements wasn't imported";
    }

    private static void saveCollection() {
        try (PrintWriter pw = new PrintWriter(new File("outputCollection.txt"))) {
            pw.write(prettyGson.toJson(queue));
        } catch (FileNotFoundException e) {
            System.out.println("No such file to save this!");
        }
    }
}

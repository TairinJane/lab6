package collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
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
        return "Elements like this were removed";
    }

    static String removeElement(Cloud cloud) {
        if (queue.remove(cloud)) {
            return "Element was removed";
        }
        return "No such element";
    }

    static String addElement(Cloud cloud) {
        queue.add(cloud);
        return "Element was added";
    }

    static String show() {
        return prettyGson.toJson(queue);
    }

    static String removeFirst() {
        Cloud element = queue.poll();
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
                "\n\nexit: выход из программы" +
                "\n\n{element} выглядит как {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                "\"color\": \"white\"}";
    }

    static String info() {
        return "Collection size: " + queue.size() +
                "\nType: PriorityQueue" +
                "\nOpen time: " + openTime;
    }

    //TODO: IMPORT
    /*static void importCollection(String filePath) {
        try {
            Type queueType = new TypeToken<PriorityQueue<Cloud>>() {
            }.getType();
            String line;
            String fileLines = String.join("\n", Files.readAllLines(Paths.get(filePath)));
            PriorityQueue<Cloud> importQueue = gson.fromJson(fileLines, queueType);
            *//*for (Cloud cloud : importQueue) {
                queue.add(cloud);
            }*//*
            Collections.addAll(importQueue);
            saveCollection();
            System.out.println("All elements imported in collection");
        } catch (IOException e) {
            System.out.println("No such file!");
        }
    }*/

    static void saveCollection() {
        try (PrintWriter pw = new PrintWriter(new File("outputCollection.txt"))) {
            pw.write(prettyGson.toJson(queue));
        } catch (FileNotFoundException e) {
            System.out.println("No such file to save this!");
        }
    }
}

package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.userconsoleex5.Console;

public class TestSingleton {
    public static void main(String[] args) {
        Console console1 = Console.getInstance();
        Console console2 = Console.getInstance();

        System.out.println(console1 == console2); // Doit afficher "true"
    }
}


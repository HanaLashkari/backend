package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Helpful {
    public static void main(String[] args) throws IOException {
        ClientHandlerForLogin server = new ClientHandlerForLogin();
        List<String> s = new ArrayList<>();
        s.add("20.0");
        s.add("15.5");
        s.add("17.25");
        s.stream().sorted().forEach(System.out::println);
       //server.showClass("4301").forEach(System.out::println);
    }
}

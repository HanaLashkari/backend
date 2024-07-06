package Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class Server {
    public static void main(String[] args) {
        System.out.println("Welcome to the server!");
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            Executor executor = Executors.newCachedThreadPool();
            while (true) {
                System.out.println("Waiting for client...");
                Thread t = new ClientHandlerForLogin(serverSocket.accept());
                executor.execute(t);
                System.out.println("This client finished :" + t.getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class ClientHandlerForLogin extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private List<String> loggedInUsers;
    private File studentsFile = new File("C:\\Users\\Asus\\Desktop\\project\\StudentsOfFlutter.txt");

    private String id = "402243094";
    private String identity = "Hana -402243094";

    public ClientHandlerForLogin(Socket socket) throws IOException {
        this.socket = socket;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("connected to server");
    }

    public ClientHandlerForLogin() {

    }

    public String listener() throws IOException {
        try{
            System.out.println("listener is activated");
            StringBuilder sb = new StringBuilder();
            int index = dis.read();
            while (index != 0) {
                sb.append((char) index);
                index = dis.read();
            }
            Scanner s = new Scanner(sb.toString());
            StringBuilder request = new StringBuilder();
            while (s.hasNextLine()) {
                request.append(s.nextLine());
            }
            System.out.println("listener2 -> read command successfully " + request.toString());
            return request.toString();}
        catch (IOException e) {
            System.out.println("error in listener : " + e);}
        return "Error!";
    }
    public void writer(String write) throws IOException {
        dos.writeBytes(write);
        dos.flush();
        dos.close();
        dis.close();
        socket.close();
        System.out.println(write);
        System.out.println("command finished and response sent to server");
    }
    private String passwordExistsChecker(String s){
        String respond = "";
        try {
            List<String> strs = Files.readAllLines(studentsFile.toPath());
            String[] NIP = s.split("-");
            System.out.println(Arrays.toString(NIP));
            for(String string : strs){
                String[] parts = string.split("-");
                for(int i=1 ; i<3 ; i++)
                    respond += parts[i].equals(NIP[i]) ? "1" : "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respond;
    }
    private boolean studentExists(String s){
        try {
            List<String> strs = Files.readAllLines(studentsFile.toPath());
            for(String string : strs)
                if (string.equals(s))
                    return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void run(){
        try {
            String command = listener();
            switch (command){
                case "login" :{
                    login();
                    break;
                }
                case "signup" :{
                    signup();
                    break;
                }
                case "changeInformation":{

                }
                case "addClass" :{
                    writer(addClass(listener()));
                    break;
                }
                case "showClasses" :{
                    List<String> list = showClass();
                    StringBuffer stringBuffer = new StringBuffer();
                    for(int i=0 ; i< list.size() ; i++) {
                        if (i == list.size() - 1)
                            stringBuffer.append(list.get(i));
                        else stringBuffer.append(list.get(i)).append("=");
                    }
                    System.out.println(stringBuffer.toString());
                    writer(stringBuffer.toString());
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void signup() {
        String command = "";
        try {
            command = listener();
            String[] parts = command.split("-");
            command = PasswordValidator.checkPass(parts[2]);
            if(studentExists(parts[0] + "-" + parts[1] + "-" + parts[2])){
                command = "7";
            }
            else if (parts[2].contains(parts[0]) || parts[2].contains(parts[1])) {
                command = "6";
            } else if (command.equals("5")) {
                DataBase.add(studentsFile, parts[0] + "-" + parts[1] + "-" + parts[2] , true);
            }
            writer(command);
            System.out.println(command);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("signup was completed.");
        }
    }

    private void login(){
        String command = "";
        try {
            command = listener();
            System.out.println(command + "---------");
            String[] parts = command.split("-");
            id = parts[1];
            identity = parts[0] + "-" + parts[1];
            writer(passwordExistsChecker(command));
            System.out.println(command);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("login was completed.");
    }

    private String addClass(String command) throws IOException {
        if(DataBase.checkOut(new File("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt") , command))
            return "exists";
        String s = DataBase.CheckoutBackString(new File("C:\\Users\\Asus\\Desktop\\project\\Courses.txt") , command);
        if(DataBase.checkOut(new File("C:\\Users\\Asus\\Desktop\\project\\Courses.txt") , command)) {
            DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt"), s, true);
            DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\studentOfcourse\\course" + command + ".txt") , identity , true);
            return "found";
        }
        return "not found";
    }

    public List<String> showClass() throws IOException {
        List<String> courses = Files.readAllLines(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt"));
        List<String> classes = new ArrayList<>();
        for (String course : courses){
            String[] parts = course.split("-");
            StringBuffer s = new StringBuffer();
            s.append(parts[0]).append("-").append(parts[3]).append("-").append(parts[2]).append("-")
                    .append(numberOfAssignment(course.split("-")[1])).append("-").append(bestStudent(course.split("-")[1]));
            classes.add(s.toString());
        }
        return classes;
    }
    private String bestStudent(String command){
        String best;
        try {
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudentForcourse\\course" + command + ".txt");
            if(!p.toFile().exists())
                return "The are not any grades";
            List<String> students = Files.readAllLines(p);
            String bestStr = students.stream().max((a , b) -> a.split("-")[1].compareTo(b.split("-")[1])).orElse("0");
            System.out.println("beststr = " + bestStr );
            best = DataBase.CheckoutBackString(studentsFile , bestStr.split("-")[0]).split("-")[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return best;
    }
    private int numberOfAssignment(String command) {
        int i = 0;
        try {
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmentOfcourse\\course" + command + ".txt");
            if(p.toFile().exists())
                i = Files.readAllLines(p).size();
        } catch (IOException e) {
            i = 0;
            e.printStackTrace();
        }
        return i;
    }

}
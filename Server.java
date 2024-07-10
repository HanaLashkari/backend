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
    static Map<String , String> loggedInUsers = new HashMap<>();
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
    private File studentsFile = new File("C:\\Users\\Asus\\Desktop\\project\\StudentsOfFlutter.txt");
    private String id = "";
    private String identity = "";
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
    public void writer(String write) throws IOException, InterruptedException {
        dos.writeBytes(write);
        dos.flush();
        dos.close();
        dis.close();
        socket.close();
        System.out.println("writer = " + write);
        System.out.println("command finished and response sent to server");
    }

    @Override
    public void run(){
        try {
            String command = listener();
            id = command.split("-")[0];
            id = !id.equals("login") && !id.equals("signup") ? id : "";
            System.out.println("iddddddd = "+id +"  "+Server.loggedInUsers.containsKey(id));
            if(Server.loggedInUsers.containsKey(id)) {
                identity = Server.loggedInUsers.get(id);
                command = command.split("-")[1];
                System.out.println("command   ======   "+command);
            }if(command.split("-").length>1)
                command = command.split("-")[1];
            switch (command){
                case "login" :{
                    login();
                    System.out.println("id ====== " + id);
                    break;
                }
                case "signup" :{
                    signup();
                    submitInformation();
                    break;
                }
                case "showInformation" :{
                    submitInformation();
                    String s = showInformation();
                    Thread.sleep(1000);
                    System.out.println("show information   ====  " + s);
                    writer(s);
                    break;
                }
                case "changeInformation":{
                    System.out.println("--------------changeInformation -------------");
                    changeInformation();
                    break;
                }
                case "addClass" :{
                    String s = listener();
                    System.out.println("here in add class =======  " + s);
                    writer(addClass(s));
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
                case "showAssignment" :{
                    List<String> list = showAssignment();
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
                case "changeDescription" :{
                    String title = listener();
                    String s = listener();
                    changeProject(title , s);
                }
                case "addToDoList" :{
                    String s = listener();
                    System.out.println("added to do list = " + s);
                    addToDoList(s);
                    break;
                }
                case "showToDoList" :{
                    List<String> list = showToDoList();
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
                case "doTask" :{
                    doTask(listener());
                    break;
                }
                case "finishTask" :{
                    finishTask(listener());
                    break;
                }
                case "showHomePage" :{
                    String s = showHomePage();
                    System.out.println("show home page ===== " + s);
                    writer(s);
                    break;
                }
                case "exit" :{
                    Server.loggedInUsers.remove(id);
                    id = "";
                    identity = "";
                    break;
                }
                case "delete" :{
                    delete();
                    Server.loggedInUsers.remove(id);
                    id = "";
                    identity = "";
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private String passwordExistsChecker(String s){
        String respond = "";
        try {
            List<String> strs = Files.readAllLines(studentsFile.toPath());
            String[] NIP = s.split("-");
            System.out.println(Arrays.toString(NIP));
            for(String string : strs){
                String[] parts = string.split("-");
                String ssss = "";
                for(int i=1 ; i<3 ; i++)
                    ssss += parts[i].equals(NIP[i]) ? "1" : "0";
                if(ssss.equals("11")) {
                    respond = ssss;
                    break;
                } else if (ssss.equals("10")) {
                    respond = ssss;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(respond.isEmpty())
            respond = "00";
        System.out.println("response in passwordExistsChecker   ======  " + respond);
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
                if(id.isEmpty() || id.equals("signup")) {
                    id = parts[1];
                    identity = parts[0] + "-" + parts[1];
                    Server.loggedInUsers.put(id , identity);
                    System.out.println("the id is not empty");
                }
                Server.loggedInUsers.put(id , identity);
                System.out.println("id === " + id);
                System.out.println("identity   ===  " + identity);
                if(!DataBase.checkOut(new File("C:\\Users\\Asus\\Desktop\\project\\Students.txt") , parts[0] + "-" + parts[1])) {
                    DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\Students.txt"), parts[0] + "-" + parts[1], true);
                    DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\passwords\\studentPassword.txt"), parts[1] + "-" + parts[2], true);
                }
                command += ("-" + id);
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
            if(id.isEmpty() || id.equals("login")) {
                id = parts[1];
                identity = parts[0] + "-" + parts[1];
                Server.loggedInUsers.put(id , identity);
                System.out.println("the id is not empty");
            }
            System.out.println("id === " + id);
            System.out.println("identity   ===  " + identity);
            String s = passwordExistsChecker(command);
            if(s.equals("11")){
                writer(s+"-"+id);
            }
            else writer(s);
            System.out.println(command);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("login was completed.");
    }
    private void submitInformation() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(identity).append("-");
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student"+id+".txt");
        if(!p.toFile().exists())
            Files.createFile(p);
        int num = 0;
        List<String> courses = Files.readAllLines(p);
        for(String c : courses)
            num += Integer.parseInt(c.split("-")[2]);
        stringBuffer.append(num).append("-");
        p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudent\\student" + id + ".txt");
        if(!p.toFile().exists())
            Files.createFile(p);
        double sum = 0;
        List<String> grades = Files.readAllLines(p);
        for(String g : grades)
            sum += Double.parseDouble(g.split("-")[1]);
        stringBuffer.append(sum/grades.size());
        p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\informationOfstudentForFlutter.txt");
        if(!DataBase.checkOut(p.toFile() , identity))
            DataBase.add(p.toFile() , stringBuffer.toString() , true);
        else if (DataBase.checkOut(p.toFile() , identity) && !DataBase.checkOut(p.toFile() , stringBuffer.toString())) {
            DataBase.remove(p.toFile() , identity);
            DataBase.add(p.toFile() , stringBuffer.toString() , true);
        }
    }
    private String showInformation() throws IOException {
        Path file = Paths.get("C:\\Users\\Asus\\Desktop\\project\\informationOfstudentForFlutter.txt");
        if(!file.toFile().exists())
            Files.createFile(file);
        if(!DataBase.checkOut(file.toFile() , id))
            return "not complete";
        return DataBase.CheckoutBackString(file.toFile() , id);
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
    private List<String> showClass() throws IOException {
        if(!Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt").toFile().exists())
            return new ArrayList<>();
        List<String> courses = Files.readAllLines(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt"));
        List<String> classes = new ArrayList<>();
        for (String course : courses){
            String[] parts = course.split("-");
            StringBuffer s = new StringBuffer();
            s.append(parts[0]).append("-").append(parts[3]).append("-").append(parts[2]).append("-")
                    .append(numberOfAssignment(course.split("-")[1] , "course")).append("-").append(bestStudent(course.split("-")[1]));
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
    private int numberOfAssignment(String command , String keyWord) {
        int i = 0;
        try {
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmentOf"+keyWord+"\\" + keyWord + command + ".txt");
            if(p.toFile().exists())
                i = Files.readAllLines(p).size();
        } catch (IOException e) {
            i = 0;
            e.printStackTrace();
        }
        return i;
    }
    private List<String> showAssignment() throws IOException {
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmetOfstudent\\student" + id + ".txt");
        if(!Files.exists(p))
            Files.createFile(p);
        List<String> assignments = Files.readAllLines(p);
        List<String> allAssignments = new ArrayList<>();
        for (String assignment : assignments){
            String[] parts = assignment.split("-");
            StringBuffer s = new StringBuffer();
            s.append(parts[0]).append("-").append(parts[2]).append("-").append(findGrade(parts[1])).append("-").append(parts[4]);
            allAssignments.add(s.toString());
        }
        return allAssignments;
    }
    private String findGrade(String command) throws IOException {
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudentForcourse\\course" + command + ".txt");
        if(!p.toFile().exists())
            return "The are not any grades";
        List<String> grades = Files.readAllLines(p);
        String s = "The are not any grades";
        for(String grade : grades){
            String[] parts = grade.split("-");
            if(parts[0].equals(id)){
                s = parts[1];
                break;
            }
        }
        return s;
    }
    private void changeProject(String title , String s){
        String[] parts = s.split("-");
        try {
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmetOfstudent\\student" + id + ".txt");
            if(!p.toFile().exists())
                return;
            List<String> assignments = Files.readAllLines(p);
            for(String a : assignments){
                if(title.equals(a.split("-")[0])){
                    System.out.println("assignment is ==== " + a);
                    DataBase.remove(p.toFile() , title);
                    String[] p2 = a.split("-");
                    int time = p2[2].lastIndexOf(",");
                    StringBuffer sb = new StringBuffer();
                    sb.append(p2[0]).append("-").append(p2[1]).append("-").append(p2[2].substring(0 , time+1)).append(parts[0]).append("-").append("project").append("-").append(parts[1]).append(",").append(parts[2]);
                    DataBase.add(p.toFile() , sb.toString() , true);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<String> showToDoList() throws IOException {
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt");
        if(!p.toFile().exists())
            return new ArrayList<>();
        return Files.readAllLines(p);
    }
    private void addToDoList(String s) throws IOException {
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt");
        DataBase.add(p.toFile() , s , true);
    }
    private void doTask(String s){
        try {
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt");
            if(!p.toFile().exists())
                return;
            DataBase.remove(p.toFile() , s);
            s = s.substring(0 , s.lastIndexOf("-")+1)+"true";
            System.out.println(s + " ==== s");
            if(!DataBase.checkOut(p.toFile() , s)) {
                DataBase.add(p.toFile(), s, true);
                System.out.println("do task was successful.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void finishTask(String s){
        Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt");
        DataBase.remove(p.toFile() , s);
    }
    private String showHomePage(){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            Path path = Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmetOfstudent\\student" + id + ".txt");
            if(!Files.exists(path))
                Files.createFile(path);
            stringBuffer.append(Files.readAllLines(path).size()).append(",");
            System.out.println("id for show home :::::: " + id);
            if(!Files.exists(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student"+id+".txt")))
                return stringBuffer.append("0").toString();
            List<String> classes = Files.readAllLines(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student"+id+".txt"));
            int numberOfExam = classes.size();
            List<String> exams = Files.readAllLines(Paths.get("C:\\Users\\Asus\\Desktop\\project\\Exams.txt"));
            for (String c : classes){
                for(String e : exams)
                    if(c.split("-")[1].equals(e))
                        numberOfExam++;
            }
            stringBuffer.append(numberOfExam).append(",");
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudent\\student" + id + ".txt");
            if(!Files.exists(p))
                Files.createFile(p);
            String best = "0";
            String worst = "0";
            if(!Files.readAllLines(p).isEmpty()) {
                best = Files.readAllLines(p).stream().map(a -> a.split("-")[1]).sorted().toList().getLast();
                worst = Files.readAllLines(p).stream().map(a -> a.split("-")[1]).sorted().toList().getFirst();
            }
            stringBuffer.append(worst).append(",").append(best).append("#");
            List<String> list = showToDoList();
            for(int i=0 ; i< list.size() ; i++) {
                if (i == list.size() - 1)
                    stringBuffer.append(list.get(i));
                else stringBuffer.append(list.get(i)).append("=");
            }
            stringBuffer.append("#");
            list = showAssignment();
            for(int i=list.size()-1 ; i>=0 ; i--) {
                if(!list.get(i).split("-")[3].contains(","))
                    list.remove(i);
            }
            for(int i=0 ; i< list.size() ; i++) {
                if (i == list.size() - 1)
                    stringBuffer.append(list.get(i));
                else stringBuffer.append(list.get(i)).append("=");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
    private void delete(){
        try {
            DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\StudentsOfFlutter.txt") , id);
            DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\Students.txt") , id);
            DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\informationOfstudentForFlutter.txt") , id);
            DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\passwords\\studentPassword.txt") , id);
            if (Files.exists(Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt")))
                Files.delete(Paths.get("C:\\Users\\Asus\\Desktop\\project\\todolistOfstudent\\student" + id + ".txt"));
            if(Files.exists(Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudent\\student" + id + ".txt")))
                Files.delete(Paths.get("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudent\\student" + id + ".txt"));
            if(Files.exists(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt")))
                Files.delete(Paths.get("C:\\Users\\Asus\\Desktop\\project\\courseOfstudent\\student" + id + ".txt"));
            if(Files.exists(Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmetOfstudent\\student" + id + ".txt")))
                Files.delete(Paths.get("C:\\Users\\Asus\\Desktop\\project\\assignmetOfstudent\\student" + id + ".txt"));
            Path p = Paths.get("C:\\Users\\Asus\\Desktop\\project\\Courses.txt");
            if(!Files.exists(p))
                Files.createFile(p);
            List<String> list = Files.readAllLines(p);
            for(String l : list){
                File file = new File("C:\\Users\\Asus\\Desktop\\project\\studentOfcourse\\course"+l.split("-")[1]+".txt");
                System.out.println(l.split("-")[1]);
                System.out.println("courese   =====  " + l);
                if(Files.exists(file.toPath()))
                    if(DataBase.checkOut(file, id))
                        DataBase.remove(file , id);
                file = new File("C:\\Users\\Asus\\Desktop\\project\\gradeOfstudentForcourse\\course"+l.split("-")[1]+".txt");
                if(Files.exists(file.toPath()))
                    if(DataBase.checkOut( file, id))
                        DataBase.remove(file , id);
            }
        }catch (Exception e){e.printStackTrace();}
    }
    private void changeInformation(){
        String command = "";
        try {
            command = listener();
            String[] parts = command.split("-");
            command = PasswordValidator.checkPass(parts[1]);
            if (parts[1].contains(parts[0]) || parts[1].equals(id)) {
                command = "6";
            } else if (command.equals("5")) {
                DataBase.remove(studentsFile , id);
                DataBase.add(studentsFile, parts[0] + "-" + id + "-" + parts[1] , true);
                DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\Students.txt") , id);
                DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\Students.txt"), parts[0] + "-" + id, true);
                DataBase.remove(new File("C:\\Users\\Asus\\Desktop\\project\\passwords\\studentPassword.txt") , id);
                DataBase.add(new File("C:\\Users\\Asus\\Desktop\\project\\passwords\\studentPassword.txt"), id + "-" + parts[1], true);
                command += ("-" + id);
            }
            writer(command);
            System.out.println(command);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("change information was completed.");
        }
    }

}
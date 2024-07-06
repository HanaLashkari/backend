package Server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DataBase {
    public static void add(File file , String string , boolean b){
        try(FileWriter writer = new FileWriter(file , b);) {
            if(!file.exists()){
                Files.createFile(file.toPath());
                writer.write(string);
                return;
            }
            if(Files.readAllLines(file.toPath()).isEmpty())
                writer.write(string);
            else writer.write("\n" + string);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void remove(File file , String string){
        try(FileWriter writer = new FileWriter(file);) {
            List<String> strs = Files.readAllLines(file.toPath());
            for(int i=0 ; i<strs.size() ; i++)
                if(!strs.get(i).contains(string)) {
                    if(i == 0)
                        writer.write(string);
                    else writer.write("\n" + string);
                }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean checkOut(File file , String string) throws IOException {
        List<String> list = Files.readAllLines(file.toPath());
        for(String s : list)
            if(s.contains(string))
                return true;
        return false;
    }

    public static String CheckoutBackString(File file , String string) throws IOException {
        List<String> list = Files.readAllLines(file.toPath());
        for (String s : list ){
            //System.out.println("s = "+s);
            if (s.contains(string))
                return s;
        }
        return "not found";
    }
}
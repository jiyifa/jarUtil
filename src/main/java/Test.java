import java.io.*;

public class Test {
    public static void main(String[] args) {
        BufferedReader br = null;
        PrintWriter pw = null;

        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            pw = new PrintWriter(new BufferedWriter(new FileWriter("./std.in")));
            String str = null;
            while((str=br.readLine())!=null){
                if (str.equals("quit")){
                    System.out.println("谢谢使用！");
                    break;
                }
                pw.println(str);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if (br!=null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(pw!=null) pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

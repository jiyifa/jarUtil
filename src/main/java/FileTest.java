import java.io.*;
import java.net.URL;

public class FileTest {
    public static void main(String[] args) {
        try {
            new FileTest().t();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void t() throws IOException {
        URL resource = Test.class.getResource("t.txy");
        String filePath = System.getProperty("user.dir");
        System.out.println(filePath);
        System.out.println(resource);
        InputStream in = new BufferedInputStream(new FileInputStream(new File(resource.getPath().toString())));
        byte[] bytes = new byte[0];
        bytes = new byte[in.available()];
        in.read(bytes);
        String str = new String(bytes);
        System.out.println(str);
    }
}

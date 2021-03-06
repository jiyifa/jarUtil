import com.yourkit.Natives;
import util.ByteToInputStream;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.*;

public class TestJarFile1 {

    //做一些预处理，比如获取当前项目路径，和创建文件夹的操作
    private String projectPath;
    private String targetDirPath;
    private String resPath;

    {
        projectPath = System.getProperty("user.dir");//当前项目路径，

        targetDirPath = projectPath + File.separator + "test";//使用File提供的分隔符，区分windows和linux
        File file = new File(targetDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public static void main(String[] args) throws Exception {
        new TestJarFile1().writeJarFile();
    }

    public void writeJarFile() throws Exception {
        InputStream is = null;
        //String jarFilePath = projectPath + File.separator + "test.jar";
        String jarFilePath = projectPath +File.separator+"lib"+File.separator+"yjp.jar";
        JarFile jarFile = new JarFile(jarFilePath);
        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) { //这个循环会读取jar包中所有文件，包括文件夹
            JarEntry jarEntry = e.nextElement();//jarEntry就是我们读取的jar包中每一个文件了，包括目录
            System.out.println(jarEntry.getName());

            String[] split = jarEntry.getName().split("/");
            String name = jarEntry.getName();
            if (name.contains(".")&&name.lastIndexOf(".")>name.lastIndexOf("/")) {
                int i = name.lastIndexOf("/");
                if(i!=-1){
                    String filepath = name.substring(0, i);
                    File f = new File(targetDirPath + File.separator + filepath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                }
                is = jarFile.getInputStream(jarEntry); //将目标文件读到流中
                String targetFileName = targetDirPath + File.separator + name;
                if(name.contains("_")){
                    byte[] bytes = ByteToInputStream.input2byte(is);
                    byte[] bytes1 = Natives.decipher1(bytes);
                    is = ByteToInputStream.byte2Input(bytes1);
                    int i1 = name.lastIndexOf("_");
                    targetFileName = targetDirPath + File.separator + name.substring(0,i1);
                }
                File file = new File(targetFileName);
                //我们自己手写一个方法，用来读写文件
                writeFile(is, file);
            } else {
                File f = new File(targetDirPath  + File.separator + jarEntry.getName());
                if (!f.exists()) {
                    f.mkdirs();
                }
            }
        }
        if (is != null) {
            is.close();
        }
    }


    public void writeFile(InputStream is, File file) throws Exception {
        if (file != null) {
            //推荐使用字节流读取，因为虽然读取的是文件，如果是 .exe, .c 这种文件，用字符流读取会有乱码
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bytes = new byte[2048]; //这里用小数组读取，使用file.length()来一次性读取可能会出错（亲身试验）
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.close();
        }
    }
//    //将class文件打成jar包
//    public void generateJar() throws FileNotFoundException, IOException {
//
//        System.out.println("*** --> 开始生成jar包...");
//        String targetDirPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
//        File targetDir = new File(targetDirPath);
//        if (!targetDir.exists()) {
//            targetDir.mkdirs();
//        }
//
//        Manifest manifest = new Manifest();
//        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
//
//        JarOutputStream target = new JarOutputStream(new FileOutputStream(targetPath), manifest);
//        writeClassFile(new File(javaClassPath), target);
//        target.close();
//        System.out.println("*** --> jar包生成完毕。");
//    }
//    private void writeClassFile(File source, JarOutputStream target) throws IOException {
//        BufferedInputStream in = null;
//        try {
//            if (source.isDirectory()) {
//                String name = source.getPath().replace("\\", "/");
//                if (!name.isEmpty()) {
//                    if (!name.endsWith("/")) {
//                        name += "/";
//                    }
//                    name = name.substring(javaClassPath.length());
//                    JarEntry entry = new JarEntry(name);
//                    entry.setTime(source.lastModified());
//                    target.putNextEntry(entry);
//                    target.closeEntry();
//                }
//                for (File nestedFile : source.listFiles())
//                    writeClassFile(nestedFile, target);
//                return;
//            }
//
//            String middleName = source.getPath().replace("\\", "/").substring(javaClassPath.length());
//            JarEntry entry = new JarEntry(middleName);
//            entry.setTime(source.lastModified());
//            target.putNextEntry(entry);
//            in = new BufferedInputStream(new FileInputStream(source));
//
//            byte[] buffer = new byte[1024];
//            while (true) {
//                int count = in.read(buffer);
//                if (count == -1)
//                    break;
//                target.write(buffer, 0, count);
//            }
//            target.closeEntry();
//        } finally {
//            if (in != null)
//                in.close();
//        }
//    }
}

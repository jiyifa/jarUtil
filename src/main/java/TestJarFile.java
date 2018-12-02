import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestJarFile {

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

//        resPath = targetDirPath + File.separator + "res"; // res文件夹
//        File f = new File(resPath);
//        if (!f.exists()) {
//            f.mkdirs();
//        }
    }

    public static void main(String[] args) throws Exception {
        new TestJarFile().writeJarFile();
    }
    public void writeJarFile() throws Exception {
        InputStream is = null;
        String jarFilePath = projectPath + File.separator + "test.jar";
        JarFile jarFile = new JarFile(jarFilePath);
        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) { //这个循环会读取jar包中所有文件，包括文件夹
            JarEntry jarEntry = e.nextElement();//jarEntry就是我们读取的jar包中每一个文件了，包括目录
            System.out.println(jarEntry.getName());
            if (jarEntry.getName().contains("META-INF/MANIFEST.MF")) { //getName()会获取文件全路径名称

                //如果是aa.txt就将其拷贝到test文件夹下
                is = jarFile.getInputStream(jarEntry); //将目标文件读到流中
                String targetFileName = targetDirPath + File.separator + "aa.txt";
                File file = new File(targetFileName);
                //我们自己手写一个方法，用来读写文件
                writeFile(is, file);
            } else if (jarEntry.getName().contains("resource/res/")) { //读取res文件夹和res文件夹下的所有文件，
                //在读取aa.txt文件时，投机取巧了一下，直接写死为aa.txt，但是循环读取多个文件，不知道文件名，需要截取目标文件的名字
                String[] split = jarEntry.getName().split("/"); //jar包中都是以 "/" 分割的
                String targetFileName = resPath + File.separator + split[split.length - 1]; //最后一位就是文件的名字
                File file = new File(targetFileName);
                //注意，因为这个if判断会读取res文件夹和res文件夹下的文件，因此需要区分
//                if(jarEntry.isDirtory()){
//                    continue;
//                }else {
//                    is = jarFile.getInputStream(jarEntry);
//                    writeFile(is, file);
//                }
                is = jarFile.getInputStream(jarEntry);
                writeFile(is, file);
            }else{
                String[] split = jarEntry.getName().split("/");
                String name = jarEntry.getName();
                if(name.contains(".")){
                    int i = name.lastIndexOf("/");
                    String filepath = name.substring(0, i);
                    File f = new File(targetDirPath+ File.separator +filepath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    is = jarFile.getInputStream(jarEntry); //将目标文件读到流中
                    String targetFileName = targetDirPath + File.separator + name;
                    File file = new File(targetFileName);
                    //我们自己手写一个方法，用来读写文件
                    writeFile(is, file);
                }else{
                    File f = new File(resPath+jarEntry.getName());
                    if (!f.exists()) {
                        f.mkdirs();
                    }
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
}

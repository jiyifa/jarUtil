package jarfile;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 利用java代码实现java源文件的编译和打包为jar文件
 */
public class CompilerAndJarTools {
    //java源码路径
    private String javaSourcePath;
    private String javaClassPath;
    private String targetPath;

    public CompilerAndJarTools(String javaSourcePath, String javaClassPath, String targetPath) {
        this.javaSourcePath = javaSourcePath;
        this.javaClassPath = javaClassPath;
        this.targetPath = targetPath;
    }

    //一、编译部分
    public void complier() throws IOException {

        System.out.println("*** --> 开始编译java源代码...");

        File javaclassDir = new File(javaClassPath);
        if (!javaclassDir.exists()) {
            javaclassDir.mkdirs();
        }

        List<String> javaSourceList = new ArrayList<String>();
        getFileList(new File(javaSourcePath), javaSourceList);

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        int result = -1;
        for (int i = 0; i < javaSourceList.size(); i++) {
            result = javaCompiler.run(null, null, null, "-d", javaClassPath, javaSourceList.get(i));
            System.out.println(result == 0 ? "*** 编译成功 : " + javaSourceList.get(i) : "### 编译失败 : " + javaSourceList.get(i));
        }
        System.out.println("*** --> java源代码编译完成。");
    }

    //
    private void getFileList(File file, List<String> fileList) throws IOException {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFileList(files[i], fileList);
                } else {
                    fileList.add(files[i].getPath());
                }
            }
        }
    }

    //二、打包部分
    public void generateJar() throws FileNotFoundException, IOException {

        System.out.println("*** --> 开始生成jar包...");
        String targetDirPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        JarOutputStream target = new JarOutputStream(new FileOutputStream(targetPath), manifest);
        writeClassFile(new File(javaClassPath), target);
        target.close();
        System.out.println("*** --> jar包生成完毕。");
    }


    private void writeClassFile(File source, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            if (source.isDirectory()) {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/")) {
                        name += "/";
                    }
                    name = name.substring(javaClassPath.length());
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : source.listFiles())
                    writeClassFile(nestedFile, target);
                return;
            }

            String middleName = source.getPath().replace("\\", "/").substring(javaClassPath.length());
            JarEntry entry = new JarEntry(middleName);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }



    public static void main(String[] args) throws IOException, InterruptedException {

        String currentDir = "E:/projectall/androidProject/testa/Projecttest/myProject";
        String javaSourcePath = currentDir + "/src/main/java/";
        String javaClassPath = currentDir + "/classes";
        String targetPath = currentDir + "/target/MyProject.jar";

        CompilerAndJarTools cl = new CompilerAndJarTools(javaSourcePath, javaClassPath, targetPath);
        cl.complier();
        cl.generateJar();
    }
}

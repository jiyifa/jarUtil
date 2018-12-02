package jarfile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;


public class Util_Jar_Test {

    //复制jar
    public void copyJar(File src , File des) throws FileNotFoundException, IOException{
        JarInputStream jarIn = new JarInputStream(new BufferedInputStream(new FileInputStream(src)));
        Manifest manifest = jarIn.getManifest();
        JarOutputStream jarOut = null;
        if(manifest == null){
            jarOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(des)));
        }else{
            jarOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(des)),manifest);
        }
        byte[] bytes = new byte[1024];
        while(true){
            //重点
            ZipEntry entry = jarIn.getNextJarEntry();
            if(entry == null)break;
            jarOut.putNextEntry(entry);

            int len = jarIn.read(bytes, 0, bytes.length);
            while(len != -1){
                jarOut.write(bytes, 0, len);
                len = jarIn.read(bytes, 0, bytes.length);
            }
            System.out.println("Copyed: " + entry.getName());
//            jarIn.closeEntry();
//            jarOut.closeEntry();
            String a = new String();
            a.length();
        }
        jarIn.close();
        jarOut.finish();
        jarOut.close();
    }

    //解压jar
    public void unJar(File src , File desDir) throws FileNotFoundException, IOException{
        JarInputStream jarIn = new JarInputStream(new BufferedInputStream(new FileInputStream(src)));
        if(!desDir.exists())desDir.mkdirs();
        byte[] bytes = new byte[1024];

        while(true){
            ZipEntry entry = jarIn.getNextJarEntry();
            if(entry == null)break;

            File desTemp = new File(desDir.getAbsoluteFile() + File.separator + entry.getName());

            if(entry.isDirectory()){    //jar条目是空目录
                if(!desTemp.exists())desTemp.mkdirs();
                System.out.println("MakeDir: " + entry.getName());
            }else{    //jar条目是文件
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desTemp));
                int len = jarIn.read(bytes, 0, bytes.length);
                while(len != -1){
                    out.write(bytes, 0, len);
                    len = jarIn.read(bytes, 0, bytes.length);
                }

                out.flush();
                out.close();

                System.out.println("Copyed: " + entry.getName());
            }
            jarIn.closeEntry();
        }

        //解压Manifest文件
        Manifest manifest = jarIn.getManifest();
        if(manifest != null){
            File manifestFile = new File(desDir.getAbsoluteFile()+File.separator+JarFile.MANIFEST_NAME);
            if(!manifestFile.getParentFile().exists())manifestFile.getParentFile().mkdirs();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(manifestFile));
            manifest.write(out);
            out.close();
        }

        //关闭JarInputStream
        jarIn.close();
    }


    //复制jar by JarFile
    public void copyJarByJarFile(File src , File des) throws IOException{
        //重点
        JarFile jarFile = new JarFile(src);
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        JarOutputStream jarOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(des)));
        byte[] bytes = new byte[1024];

        while(jarEntrys.hasMoreElements()){
            JarEntry entryTemp = jarEntrys.nextElement();
            jarOut.putNextEntry(entryTemp);
            BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(entryTemp));
            int len = in.read(bytes, 0, bytes.length);
            while(len != -1){
                jarOut.write(bytes, 0, len);
                len = in.read(bytes, 0, bytes.length);
            }
            in.close();
            jarOut.closeEntry();
            System.out.println("Copyed: " + entryTemp.getName());
        }

        jarOut.finish();
        jarOut.close();
        jarFile.close();
    }

    //解压jar文件by JarFile
    public void unJarByJarFile(File src , File desDir) throws FileNotFoundException, IOException{
        JarFile jarFile = new JarFile(src);
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        if(!desDir.exists())desDir.mkdirs(); //建立用户指定存放的目录
        byte[] bytes = new byte[1024];

        while(jarEntrys.hasMoreElements()){
            ZipEntry entryTemp = jarEntrys.nextElement();
            File desTemp = new File(desDir.getAbsoluteFile() + File.separator + entryTemp.getName());

            if(entryTemp.isDirectory()){    //jar条目是空目录
                if(!desTemp.exists())desTemp.mkdirs();
                System.out.println("makeDir" + entryTemp.getName());
            }else{    //jar条目是文件
                //因为manifest的Entry是"META-INF/MANIFEST.MF",写出会报"FileNotFoundException"
                File desTempParent = desTemp.getParentFile();
                if(!desTempParent.exists())desTempParent.mkdirs();

                BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(entryTemp));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desTemp));

                int len = in.read(bytes, 0, bytes.length);
                while(len != -1){
                    out.write(bytes, 0, len);
                    len = in.read(bytes, 0, bytes.length);
                }

                in.close();
                out.flush();
                out.close();

                System.out.println("Copyed: " + entryTemp.getName());
            }
        }

        jarFile.close();
    }



    /*实验结论:
     * 1.JarInputStream的getNextJarEntry()和jarOutputStream的putNextJarEntry()中没有包括"META-INF/MANIFEST.MF"这一项,因此复制和解压都    要注意
     * 2.JarFile的entries()方法包含了全部Entry,也包括"META-INF/MANIFEST.MF",没有"META-INF/"这一项,因此在解压的时候要先检测父文件存不存在
     * 4.复制jar文件有3中方法, A是直接用BufferedInputStream和BufferedOutputStream复制,
     *                      B是用JarInputStream的getNextJarEntry()和jarOutputStream的putNextJarEntry()
     *                      C是用JarFile的entries()方法,遍寻JarEntry的InputStream,以此写出
     * 5.解压jar的话推荐使用JarFile,当前实例方法只支持解压jar文件
     * 6.在复制的时候,src文件只可以是jar文件,但des文件可以是带zip或rar后缀的文件
     */

    @Test
    public void testCopyJar(){
        File src = new File("E:/projectall/androidProject/testa/test.jar");
        File des = new File("E:/projectall/androidProject/testa/testCopy.jar");
        //实验表明只运行复制和解压jar文件
//        File src = new File("C:/rtf.zip");
//        File des = new File("C:/testCopy.zip");
        try {
            copyJar(src,des);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testUnJar(){
        File src = new File("C:/a.jar");
//        File src = new File("C:/b.rar");    //不支持rar解压
        String desFile = "aa";
        File desDir = new File(src.getParent()+File.separator+desFile);
        try {
            unJar(src, desDir);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Test
    public void testCopyJarByJarFile(){
        File src = new File("C:/a.jar");
        File des = new File("C:/testCopy.zip");
        try {
            copyJarByJarFile(src,des);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testUnJarByJarFile(){
        File src = new File("C:/a.jar");
//        File src = new File("C:/b.rar");    //不支持rar解压
        String desFile = "aa";
        File desDir = new File(src.getParent()+File.separator+desFile);
        try {
            unJarByJarFile(src, desDir);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
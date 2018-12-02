//解析jar文件。获取jar中。后缀为class文件的全限定名。而且判断。该class文件。是否继承和实现某个接口。
//以下是测试类
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.*;
public class JarDir {
    public static void main (String args[])
            throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

        List<String > size=new ArrayList<String>();

        //JarFile jarFile = new JarFile("D:/projectList2017_10_24reconciliation_ba_pageconfig/reconciliation_ba/target/classes/deploy/1311-v1.0.0.jar");
        String des="E:/projectall/androidProject/testa/lib";
        String namess="yjp.jar";
        @SuppressWarnings("resource")
        JarFile jarFile = new JarFile(des+ File.separator+namess);
        File file=new File(des+File.separator+namess);
        URL url=file.toURI().toURL();
        ClassLoader  loader=new URLClassLoader(new URL[]{url});

        //  当你有了该JAR文件的一个引用之后，你就可以读取其文件内容中的目录信息了。JarFile的entries方法返回所有entries的枚举集合 (Enumeration)。通过每一个entry，你可以从它的manifest文件得到它的属性，任何认证信息，以及其他任何该entry的信息，如它的名字或者大小等。
        Enumeration<?> files  = jarFile.entries();
        while (files .hasMoreElements()) {
            //process(files .nextElement(),size,loader);
            JarEntry element = (JarEntry) files.nextElement();
            String name = element.getName();
            long size1 = element.getSize();
            long time = element.getTime();
            long compressedSize = element.getCompressedSize();
            System.out.print(name+"\t");
            System.out.print(size1+"\t");
            System.out.print(compressedSize+"\t");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)));
        }
        System.out.println(size.toString());
    }


    private static void process(Object obj,List<String > size ,ClassLoader loader) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

        JarEntry entry = (JarEntry)obj;
        String name = entry.getName();

        //格式化
        formatName( size, name, loader);
    }


    private static void formatName(List<String> size, String name,ClassLoader loader) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

        if(!name.endsWith(".MF")){
            if(name.endsWith(".class")){
                String d=name.replaceAll("/",".");
                int n=6;
                //第一个参数是开始截取的位置，第二个是结束位置。
                String names=d.substring(0,name.length()-n);
                /*动态加载指定jar包调用其中某个类的方法*/
                Class<?> cls=loader.loadClass(names);//加载指定类，注意一定要带上类的包名
                Class<?>  lifeCycle= com.jcraft.jsch.ChannelShell.class;
                System.out.println(names+"="+lifeCycle.isAssignableFrom(cls));
                if(lifeCycle.isAssignableFrom(cls)){
                    size.add(names);
                }

            }

        }
    }
}

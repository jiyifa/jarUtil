import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class MyZip {
    public static void main(String[] args) throws ZipException, IOException {
        //初始化路径
        String SIS = File.separator;
        String root = "E:" + SIS + "ziptest";
        String fromDir = root + SIS + "temp";
        String toZipFullname = root + SIS + "data" + SIS + "123001.zip";
        System.out.println(fromDir);
        System.out.println(toZipFullname);
        System.out.println("请选择：1.压缩文件，2.解压文件");
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();
        if(i==1){
            //压缩:
            createZip(fromDir, toZipFullname);
        }else if(i==2){
            //解压(完毕后删除压缩包):
            File zip = new File(toZipFullname);
            if(outZip(zip)){
                zip.delete();//实际测试时,这条语句没有执行,可以试试加个Thread.sleep(10);
            }
        }
    }

    public static boolean createZip(String fromDir, String toZipFullname) {
        File fromFile = new File(fromDir);
        List<File> fileList = new ArrayList<File>();
        getAllFiles(fromFile, fileList);
        writeZipFile(toZipFullname, fromFile, fileList);
        return true;
    }

    /**
     * 添加文件到zip
     * @param zipfilename
     * @param file
     * @param zos
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void addToZip(File zipfilename, File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(
                zipfilename.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        System.out.println("Writing '" + zipFilePath + "' to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);


        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }


        zos.closeEntry();
        fis.close();
    }

    /**
     * 创建压缩包
     * @param directoryToZip
     * @param fileList
     */
    private static void writeZipFile(String toFileName, File directoryToZip,
                                     List<File> fileList) {
        try {
            FileOutputStream fos = new FileOutputStream(toFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip directory, not
                    // directories
                    addToZip(directoryToZip, file, zos);
                }
            }
            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read all the files recursively from the directory
     * @param dir
     * @param fileList
     */
    private static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压zip文件到当前目录
     * @param theZipFile
     * @return
     * @throws ZipException
     * @throws IOException
     */
    public static boolean outZip(File theZipFile) throws ZipException, IOException {
        // 所提供的File不存在或者不是文件,则直接返回false
        if (!theZipFile.exists() || !theZipFile.isFile()) {
            return false;
        }
        String SIS = File.separator; //当前系统路径分隔符
        // zip所在路径
        String zipPath = theZipFile.getParent();
        // 建立ZipFile对象,并获取所有文件条目的枚举(文件夹不算枚举元素[不同的压缩方式,获得的条目数量不同])
        ZipFile zipFile = new ZipFile(theZipFile);
        Enumeration<?> enu = zipFile.entries();//该枚举只含文件,通常windows下压缩包把文件夹也算进去了
        // 遍历所有枚举元素
        while (enu.hasMoreElements()) {
            // 获取一个枚举元素(zip中的一个文件)
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();
            // 元素名字(不含路径)
            String entryName = zipEntry.getName();
            System.out.println("正在解压:" + entryName);
            // 当前目录下创建空文件(用于存储解压出来的条目,如果文件在某个文件夹下,则会先创建这些文件夹)
            File file = new File(zipPath, entryName);
            // 如果条目在某个文件夹下,则先创建文件夹,再创建空文件
            if (entryName.contains(SIS)) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists())
                    parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
            // 从zip文件中读取文件存储到刚刚创建的空文件中
            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            is.close();
            fos.close();
        }
        // 解压完成,关闭资源
        zipFile.close();
        return true;
    }
}
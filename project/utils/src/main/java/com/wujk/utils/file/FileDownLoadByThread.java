package com.wujk.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileDownLoadByThread {
    private final int SUCCESS = 200;
    private final int SUCCESS_SPLIT = 206;
    private int threadNum = 3; // 线程数
    private boolean autoResume = false; // 是否断点续传
    private String fileName; // 文件名称
    private String url; // 网络路径
    private List<Model> fileList = new ArrayList<Model>(); // 文件列表
    private ExecutorService pool;

    /**
     * 通过url 获取文件名称
     * @param url
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        String[] strs = url.split("/");
        return strs[strs.length - 1];
    }

    // 文件实体
    class Model {
        public long startIndex;       // 开始位置
        public long endIndex;         // 结束位置
        public String tempFile;       // 临时文件
        public String file;           // 真实文件
        public boolean isLast = false;// 是否是最后一段
    }

    public FileDownLoadByThread(String url, String fileName) {
        this(url, false, fileName);
    }

    public FileDownLoadByThread(String url, boolean autoResume, String fileName) {
        this(url, autoResume, 1, fileName);
    }

    public FileDownLoadByThread(String url, int threadNum, String fileName) {
        this(url, false, threadNum, fileName);
    }

    public FileDownLoadByThread(String url, boolean autoResume, int threadNum, String fileName) {
        this.threadNum = threadNum;
        this.url = url;
        this.autoResume = autoResume;
        this.fileName = fileName;
        pool = Executors.newFixedThreadPool(threadNum);
    }

    /**
     * 获取文件大小
     * @return
     */
    private long fileSize() {
        try {
            URL _url = new URL(url);
            HttpURLConnection con = (HttpURLConnection) _url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (SUCCESS == status) {
                long length = con.getContentLength();
                return length;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 计算每个线程下载数据量
     */
    private void calculation(long length) {
        long each = length / threadNum;
        for (int i = 0; i < threadNum; i++) {
            Model model = new Model();
            model.startIndex = i * each;
            model.endIndex = (i + 1) * each - 1;
            if (i == threadNum - 1) {
                model.endIndex = length;
                model.isLast = true;
            }
            model.file = fileName;
            if (autoResume) {
                File file = new File(fileName);
                File parent = file.getParentFile();
                file = new File(parent, model.startIndex + "" + model.endIndex
                        + "" + i + ".tmp");
                model.tempFile = file.getAbsolutePath();
            }
            fileList.add(model);
        }
    }

    /**
     * 读取临时文件
     */
    private void readTempFile() {
        if (!autoResume) {
            return;
        }
        for (int i = 0; i < fileList.size(); i++) {
            try {
                Model model = fileList.get(i);
                if (model.tempFile == null) {
                    return;
                }
                File file = new File(model.tempFile);
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file)));
                    String line = null;
                    String result = "";
                    while ((line = reader.readLine()) != null) {
                        result += line.trim();
                    }
                    if (result != null) {
                        model.startIndex = Integer.parseInt(result.trim()) - model.startIndex;
                    }
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }

    }

    /**
     * 保存临时文件
     *
     * @param file
     * @param lentgh
     */
    private void saveTempFile(File file, String lentgh) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rwd");
            raf.write(lentgh.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void download() {
        long length = fileSize();  // 获取文件大小
        try {
            RandomAccessFile file = new RandomAccessFile(new File(fileName), "rw");
            file.setLength(length);
            file.close();
            calculation(length);     // 计算多线程
            readTempFile();    // 读取临时文件
            for (int i = 0; i < fileList.size(); i++) {
                pool.execute(new DownLoadThread(fileList.get(i)));
            }
            pool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(File file) {
        file.delete();
    }

    /**
     * 下载线程
     * @author eeesys
     *
     */
    class DownLoadThread implements Runnable {

        private Model model;

        public DownLoadThread(Model model) {
            this.model = model;
        }

        @Override
        public void run() {
            try {
                URL _url = new URL(url);
                HttpURLConnection con = (HttpURLConnection) _url.openConnection();
                con.setRequestMethod("GET");
                if (model.isLast) {
                    con.addRequestProperty("Range", "bytes=" + model.startIndex + "-");
                } else {
                    con.addRequestProperty("Range", "bytes=" + model.startIndex + "-" + model.endIndex);
                }
                int status = con.getResponseCode();
                if (SUCCESS_SPLIT == status || SUCCESS == status) {
                    InputStream is = con.getInputStream();
                    RandomAccessFile file = new RandomAccessFile(new File(fileName), "rwd");
                    file.seek(model.startIndex);
                    int len = 0;
                    byte[] buf = new byte[1024];
                    int count = 0;
                    while((len = is.read(buf)) != -1) {
                        file.write(buf, 0, len);
                        count += len;
                        if (autoResume && model.tempFile != null) {
                            saveTempFile(new File(model.tempFile), model.startIndex + count + "");
                        }
                    }
                    is.close();
                    file.close();
                    if (autoResume && model.tempFile != null) {
                        deleteFile(new File(model.tempFile));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


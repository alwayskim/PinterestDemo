package wyz.whaley.pinterest.http.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by alwayking on 16/3/31.
 */
public class AKFileDownLoader {

    public AKFileDownLoader(Context context, String downloadUrl,
                          File fileSaveDir, int threadNum) {
//        try {
//            this.context = context;
//            this.downloadUrl = downloadUrl;
//            fileService = new FileService(this.context);
//            URL url = new URL(this.downloadUrl);
//            if (!fileSaveDir.exists()) // 判断目录是否存在，如果不存在，创建目录
//                fileSaveDir.mkdirs();
//            this.threads = new DownloadThread[threadNum];// 实例化线程数组
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5 * 1000);
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty(
//                    "Accept",
//                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//            conn.setRequestProperty("Accept-Language", "zh-CN");
//            conn.setRequestProperty("Referer", downloadUrl);
//            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty(
//                    "User-Agent",
//                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.connect(); // 连接
//            printResponseHeader(conn);
//            if (conn.getResponseCode() == 200) { // 响应成功
//                this.fileSize = conn.getContentLength();// 根据响应获取文件大小
//                if (this.fileSize <= 0)
//                    throw new RuntimeException("Unkown file size ");
//
//                String filename = getFileName(conn);// 获取文件名称
//                this.saveFile = new File(fileSaveDir, filename);// 构建保存文件
//                Map<Integer, Integer> logdata = fileService
//                        .getData(downloadUrl);// 获取下载记录
//                if (logdata.size() > 0) {// 如果存在下载记录
//                    for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
//                        data.put(entry.getKey(), entry.getValue());// 把各条线程已经下载的数据长度放入data中
//                }
//                if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据总长度
//                    for (int i = 0; i < this.threads.length; i++) {
//                        this.downloadSize += this.data.get(i + 1);
//                    }
//                    print("已经下载的长度" + this.downloadSize);
//                }
//                // 计算每条线程下载的数据长度
//                this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize
//                        / this.threads.length
//                        : this.fileSize / this.threads.length + 1;
//            } else {
//                throw new RuntimeException("server no response ");
//            }
//        } catch (Exception e) {
//            print(e.toString());
//            throw new RuntimeException("don't connection this url");
//        }
    }
}

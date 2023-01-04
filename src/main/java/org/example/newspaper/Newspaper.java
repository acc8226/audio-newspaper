package org.example.newspaper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Newspaper {
    public static void main(String[] args) throws IOException {
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC+8"));
        ItemEnum itemEnum = ItemEnum.FM_367750;
        if (now.getHour() > 12) {
            itemEnum = ItemEnum.FM_456498;
        }
        int pageNum = 1;
        int pageSize = 20;
        JSONArray jsonArray = getJsonArray(itemEnum, pageNum, pageSize);
        writeMdFile(jsonArray);
    }

    private static JSONArray getJsonArray(ItemEnum item, int pageNum, int pageSize) throws IOException {
        if (pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }
        // 测试地址 https://d.fm.renbenai.com/fm/read/fmd/h5/getPayResourceList_714.html?pid=367750&isFree=2&pageNum=1&pageSize=1
        String basePath = "https://d.fm.renbenai.com/fm/read/fmd/h5/getPayResourceList_714.html?pid=%s&isFree=2&pageNum=%s&pageSize=%s";
        String path = String.format(basePath, item.getPid(), pageNum, pageSize);
        HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
        connection.setRequestMethod("GET");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 打开网络通信输入流
        try (InputStream is = connection.getInputStream();
             OutputStream bos = new BufferedOutputStream(byteArrayOutputStream)) {
            byte[] bytes = new byte[1024 * 8];
            int len;
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
        } finally {
            connection.disconnect();
        }
        String s = byteArrayOutputStream.toString();
        System.out.println(s);
        JSONObject obj = new JSONObject(s);
        String code = obj.getString("code");
        if (!"0".equals(code)) {
            String msg = obj.getString("msg");
            throw new IOException(msg);
        }
        JSONObject data = obj.getJSONObject("data");
        return data.getJSONArray("list");
    }

    public static void writeMdFile(JSONArray array) throws IOException {
        StringBuilder context = new StringBuilder();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0, size = array.length(); i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            if (i == 0) {
                String image = obj.getString("img640_640");
                context.append(String.format("![](%s)", image)).append(System.lineSeparator()).append(System.lineSeparator());
            }
            String title = obj.getString("title");
            String filePath = obj.getJSONArray("audiolist").getJSONObject(0).getString("filePath");
            context.append("* ").append(String.format("[%s](%s)", title, filePath)).append(System.lineSeparator());
            // 发布时间
            String publishTime = obj.getString("publishTime");
            Instant timestamp = Instant.ofEpochSecond(Long.parseLong(publishTime));
            ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.systemDefault());
            System.out.println(dateTimeFormatter.hashCode());
            String dataTime = zonedDateTime.format(dateTimeFormatter);
            System.out.println(i + 1 + " " + title + " " + dataTime);
        }
        Path readMe = Paths.get("README.md");
        if (!Files.exists(readMe)) {
            Files.createFile(readMe);
        }
        Files.write(readMe, context.toString().getBytes());
    }
}

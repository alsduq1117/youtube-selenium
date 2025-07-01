package com.youtubeselenium;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeService {

    public List<String> extractUrlsFromCsv(MultipartFile file) throws Exception {
        List<String> searchUrls = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 3 && row[2] != null && !row[2].trim().isEmpty()) {
                    searchUrls.add(row[2].trim());
                }
            }
        }

        return searchUrls;
    }

    public File extractVideoIds(List<String> urls) {
        List<String> videoIds = SeleniumRunner.extractVideoIds(urls);

        File csvFile = new File("video_ids_result.csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            writer.writeNext(new String[]{"videoId"});
            for (String id : videoIds) {
                writer.writeNext(new String[]{id});
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return csvFile;
    }

}

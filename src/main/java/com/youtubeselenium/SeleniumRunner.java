package com.youtubeselenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SeleniumRunner {

    public static List<String> extractVideoIds(List<String> urls) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // UI 없이 실행
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<String> videoIds = new ArrayList<>();

        for (String url : urls) {
            try {
                log.info("검색 페이지 진입: " + url);
                driver.get(url);
                List<WebElement> videoElements = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("video-title"))
                );

                WebElement firstVideo = videoElements.get(0);
                String href = firstVideo.getAttribute("href");
                if (href != null && href.contains("watch?v=")) {
                    String videoId = extractVideoIdFromUrl(href);
                    log.info("추출된 videoId: " + videoId);
                    videoIds.add(videoId);
                } else {
                    log.warn("href에서 videoId 추출 실패");
                    videoIds.add("INVALID");
                }
            } catch (Exception e) {
                log.warn("실패: " + url + " → " + e.getMessage());
                videoIds.add("ERROR");
            }
        }

        driver.quit();
        return videoIds;
    }

    private static String extractVideoIdFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("v=")) {
                        return param.substring(2);
                    }
                }
            }

            // shorts 또는 다른 형태의 링크 처리
            if (url.contains("/shorts/")) {
                return url.substring(url.indexOf("/shorts/") + 8);
            }

            // ?v= 없이 바로 ID만 붙은 형태 처리
            if (url.contains("watch/")) {
                return url.substring(url.indexOf("watch/") + 6);
            }

        } catch (Exception e) {
            log.warn("videoId 파싱 실패: " + url);
        }
        return "";
    }
}


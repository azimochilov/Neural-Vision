package com.fotodetector.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.net.URL;
import java.nio.file.StandardCopyOption;

public class ImageFetcher {

    public static Set<String> fetchImageUrlsUtil(String url, String driverPath) {
        Set<String> images = new HashSet<>();

        System.setProperty("webdriver.chrome.driver", driverPath);
        WebDriver wd = new ChromeDriver();

        try {
            wd.get(url);
            List<WebElement> thumbnailResults = wd.findElements(By.cssSelector("img[class ='irc_mi']"));
            for (WebElement img : thumbnailResults) {
                String src = img.getAttribute("src");
                if (src != null && src.startsWith("http")) {
                    images.add(src);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wd != null) {
                wd.quit(); // Close the WebDriver
            }
        }
        return images;
    }

    public static void fetchImageUrls(String query, int maxLinksToFetch, WebDriver wd, int sleepBetweenInteractions, String driverPath, String targetPath, String searchTerm) throws InterruptedException, IOException {
        Path targetFolder = Paths.get(targetPath, searchTerm.toLowerCase().replace(" ", "_"));
        Files.createDirectories(targetFolder);

        String searchUrl = "https://www.google.com/search?safe=off&site=&tbm=isch&source=hp&q={q}&oq={q}&gs_l=img";

        wd.get(searchUrl.replace("{q}", query));
        Set<String> imageUrls = new HashSet<>();
        int imageCount = 0;
        int resultsStart = 0;
        while (imageCount < maxLinksToFetch) {
            scrollToEnd(wd, sleepBetweenInteractions);

            List<WebElement> thumbnailResults = wd.findElements(By.cssSelector("img.Q4LuWd"));
            int numberResults = thumbnailResults.size();
            System.out.println("Found: " + numberResults + " search results. Extracting links from " + resultsStart + ":" + numberResults);
            for (WebElement img : thumbnailResults.subList(Math.min(50, thumbnailResults.size()), numberResults)) {
                try {
                    img.click();
                    Thread.sleep(sleepBetweenInteractions);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                List<WebElement> links = wd.findElements(By.cssSelector("a[jsname='sTFXNd']"));
                for (WebElement link : links) {
                    String href = link.getAttribute("href");
                    if (href != null && href.startsWith("http")) {
                        Set<String> actualUrls = fetchImageUrlsUtil(href, driverPath);
                        imageUrls.addAll(actualUrls);
                    }
                }
                int imageCount2 = imageUrls.size();
                System.out.println(imageCount2);
                if (imageCount2 >= maxLinksToFetch / 10) {
                    System.out.println("Found: " + imageUrls.size() + " image links, saving!");
                    for (String imageUrl : imageUrls) {
                        persistImage(targetFolder, imageUrl);
                    }
                    imageUrls.clear();
                }
                imageCount += imageCount2;
            }

            if (imageUrls.size() >= maxLinksToFetch) {
                System.out.println("Found: " + imageUrls.size() + " image links, done!");
                break;
            } else {
                System.out.println("Found: " + imageUrls.size() + " image links, looking for more ...");
                Thread.sleep(30000);
                WebElement loadMoreButton = wd.findElement(By.cssSelector(".mye4qd"));
                if (loadMoreButton != null) {
                    ((JavascriptExecutor) wd).executeScript("document.querySelector('.mye4qd').click();");
                }
            }
            resultsStart = imageCount;
        }
        System.out.println(imageUrls.size());
    }

    private static void scrollToEnd(WebDriver wd, int sleepBetweenInteractions) throws InterruptedException {
        ((JavascriptExecutor) wd).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(sleepBetweenInteractions);
    }

    private static void persistImage(Path targetFolder, String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String fileName = Paths.get(url.getPath()).getFileName().toString();
        Path destinationFile = targetFolder.resolve(fileName);
        Files.copy(url.openStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
    }

    // Main method or other methods to call fetchImageUrls can be added here
}
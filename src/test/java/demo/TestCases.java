package demo;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    static ChromeDriver driver;
    
        private WebDriverWait wait;
        private static final int PAGES_TO_SCRAPE = 4;
        /*
         * TODO: Write your tests here with testng @Test annotation. 
         * Follow `testCase01` `testCase02`... format or what is provided in instructions
         */
    
         
        /*
         * Do not change the provided methods unless necessary, they will help in automation and assessment
         */
        @BeforeTest
        public void startBrowser()
        {
            System.setProperty("java.util.logging.config.file", "logging.properties");
    
            // NOT NEEDED FOR SELENIUM MANAGER
            // WebDriverManager.chromedriver().timeout(30).setup();
    
            ChromeOptions options = new ChromeOptions();
            LoggingPreferences logs = new LoggingPreferences();
    
            logs.enable(LogType.BROWSER, Level.ALL);
            logs.enable(LogType.DRIVER, Level.ALL);
            options.setCapability("goog:loggingPrefs", logs);
            options.addArguments("--remote-allow-origins=*");
    
            System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 
    
            driver = new ChromeDriver(options);
    
            driver.manage().window().maximize();
        }
    
      @Test(priority = 1, enabled = true)
        public void testCase01() {
            List<HashMap<String, Object>> teamData = new ArrayList<>();
    
            try {
                System.out.println("Start Test case: Scrape hockey team data");
                driver.get("https://www.scrapethissite.com/pages//");
                Thread.sleep(4000);
               driver.findElement(By.xpath("//a[text()='Hockey Teams: Forms, Searching and Pagination']")).click();
                Thread.sleep(5000);
                for (int i = 0; i <= PAGES_TO_SCRAPE; i++) {
                    if (i > 1) {
                    driver.findElement(By.xpath("//*[@id='hockey']/div/div[5]/div[1]/ul/li[25]/a")).click();
                    }
    Thread.sleep(4000);
                    driver.findElement(By.xpath("//table/tbody/tr"));
                    List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
    
                    for (WebElement row : rows) {
                        List<WebElement> columns = row.findElements(By.tagName("td"));
                      
                        if (columns.size() > 0) {
                            String teamName = columns.get(0).getText();
                            String year = columns.get(1).getText();
                            String pctText = columns.get(5).getText();
                            double winPercentage = pctText.isEmpty() ? 0.0 : Double.parseDouble(pctText);
    
                            if (winPercentage < 0.40) {
                                HashMap<String, Object> team = new HashMap<>();
                                team.put("EpochTime", System.currentTimeMillis());
                                team.put("TeamName", teamName);
                                team.put("Year", year);
                                team.put("WinPercentage", winPercentage);
                                teamData.add(team);
                            }
                        }
                    }
                }
    
               saveToJson(teamData, "output/hockey-team-data.json");
                System.out.println("End Test case: Hockey team data scraped and saved successfully");
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Test case 01 failed due to an exception: " + e.getMessage());
            }
        }
    
         @Test(priority = 2, enabled = true)
        public void testCase02() {
            List<HashMap<String, Object>> filmData = new ArrayList<>();
    
            try {
                System.out.println("Start Test case: Scrape Oscar winning films data");
                driver.get("https://www.scrapethissite.com/pages/");
                Thread.sleep(4000);
                WebElement oscarLink = driver.findElement(By.linkText("Oscar Winning Films: AJAX and Javascript"));
                oscarLink.click();
                Thread.sleep(4000);
                List<WebElement> years = driver.findElements(By.xpath("//a[@href='#']"));
                Thread.sleep(4000);
                for (int yearIndex = 0; yearIndex < years.size(); yearIndex++) {
                    WebElement yearElement = years.get(yearIndex);
                    String year = yearElement.getText();
                    System.out.println("Scraping data for year: " + year);
                    yearElement.click();
                    Thread.sleep(3000);
                    // Wait for the movie table to be present
                    WebElement movieTable = driver.findElement(By.xpath("(//div[@class='col-md-12'])[4]"));
                    Thread.sleep(4000);
                    List<WebElement> rows = movieTable.findElements(By.xpath(".//tbody/tr"));
                    Thread.sleep(4000);
                    // Scraping the top 5 movies for each year
                    int count = 0;
                    for (int i = 0; i < rows.size() && count < 5; i++) {
                        WebElement row = rows.get(i);
                        List<WebElement> columns = row.findElements(By.tagName("td"));
    
                        if (columns.size() > 0) {
                            String title = columns.get(0).getText();
                            String nomination = columns.get(1).getText();
                            String awards = columns.get(2).getText();
                            boolean isWinner = !row.findElements(By.xpath(".//i[contains(@class, 'glyphicon-flag')]"))
                                    .isEmpty();
    
                            if (!title.isEmpty() && !nomination.isEmpty() && !awards.isEmpty()) {
                                HashMap<String, Object> film = new HashMap<>();
                                film.put("EpochTime", System.currentTimeMillis());
                                film.put("Year", year);
                                film.put("Title", title);
                                film.put("Nomination", nomination);
                                film.put("Awards", awards);
                                film.put("isWinner", isWinner);
                                filmData.add(film);
                                count++;
                            }
                        }
                    }
                
                }
               saveToJson(filmData, "output/oscar-winner-data.json");
                System.out.println("End Test case: Oscar winning films data scraped and saved successfully");
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Test case 02 failed due to an exception: " + e.getMessage());
            }
           
        }
    
        
        private static void saveToJson(List<HashMap<String, Object>> data, String filePath) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                File outputDir = new File("Output");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                mapper.writeValue(new File(filePath), data);
            } catch (IOException e) {
                e.printStackTrace();
            }}
    
        @AfterTest
        public void endTest()
        {
            driver.close();
        driver.quit();

    }
}
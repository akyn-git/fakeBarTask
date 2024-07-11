package Test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class FetchNumbers {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        // Setup WebDriver
        //System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://sdetchallenge.fetch.com/");
    }

    @AfterClass
    public void tearDown() {
        // Close the browser
//        if (driver != null) {
//            driver.quit();
//        }
    }

    private String weigh(int[] leftBars, int[] rightBars) {
        // Clear the grids
        WebElement resetButton = driver.findElement(By.xpath("//button[.='Reset']"));
        resetButton.click();

        // Fill the left grid
        fillGrid("left", leftBars);

        // Fill the right grid
        fillGrid("right", rightBars);

        // Perform the weighing
        WebElement weighButton = driver.findElement(By.xpath("//button[.='Weigh']"));
        weighButton.click();

        // Get the result
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='reset']//following-sibling::button")));
        return resultButton.getText();
    }

    private void fillGrid(String side, int[] bars) {
        List<WebElement> gridCells = driver.findElements(By.cssSelector("input[data-side='" + side + "']"));
        for (int i = 0; i < bars.length; i++) {
            gridCells.get(i).sendKeys(String.valueOf(bars[i]));
        }
    }

    private int findFakeGoldBar() {
        int[] bars = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        while (bars.length > 1) {
            int groupSize = bars.length / 3;
            int[] leftBars = new int[groupSize];
            int[] rightBars = new int[groupSize];
            int[] remainingBars = new int[bars.length - 2 * groupSize];

            System.arraycopy(bars, 0, leftBars, 0, groupSize);
            System.arraycopy(bars, groupSize, rightBars, 0, groupSize);
            System.arraycopy(bars, 2 * groupSize, remainingBars, 0, remainingBars.length);

            String result = weigh(leftBars, rightBars);

            if (result.contains("left")) {
                bars = leftBars;
            } else if (result.contains("right")) {
                bars = rightBars;
            } else {
                bars = remainingBars;
            }
        }
        return bars[0];
    }

    @Test
    public void testFindFakeGoldBar() throws InterruptedException {
        int fakeBar = findFakeGoldBar();

        // Click on the identified fake bar
        WebElement fakeBarButton = driver.findElement(By.id("coin_" + fakeBar));
        fakeBarButton.click();

        // Get the alert message
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertMessage = alert.getText();
        Assert.assertTrue(alertMessage.contains("Yay"), "Fake bar: " + fakeBar);
        System.out.println("Fake bar: " + fakeBar);
        System.out.println("Alert message: " + alertMessage);

        // Accept the alert
        alert.accept();
    }
}

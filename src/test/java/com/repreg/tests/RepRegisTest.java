package com.repreg.tests;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class RepRegisTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private Random random = new Random();
    private JavascriptExecutor js;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
    }

    // Utility: Generate random DOB >18 years old
    private String generateRandomDOBOver18() {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.minusYears(18);
        LocalDate minDate = today.minusYears(70);
        long daysBetween = minDate.until(maxDate).toTotalMonths() * 30; // approximate days
        long randomDays = random.nextInt((int) daysBetween + 1);
        LocalDate dob = minDate.plusDays(randomDays);
        return dob.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    // Utility: Generate random 9-digit SSN
    private String generateRandomSSN() {
        int part1 = 100 + random.nextInt(900);
        int part2 = 10 + random.nextInt(90);
        int part3 = 1000 + random.nextInt(9000);
        return String.format("%03d%02d%04d", part1, part2, part3);
    }

    // Utility: Generic dropdown selection
    private void selectDropdown(String elementId, String value) throws InterruptedException {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
        dropdown.click();
        dropdown.sendKeys(value);
        dropdown.sendKeys(Keys.ENTER);
        Thread.sleep(500);
    }

    // Utility: Robust spinner wait
    private void waitForSpinnerToDisappear() {
        System.out.println("🕐 Waiting for spinner to disappear (slow response possible)...");
        By spinner = By.cssSelector(".vld-overlay.is-active[aria-label='Loading'][aria-busy='true']");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            longWait.until(ExpectedConditions.presenceOfElementLocated(spinner));
            longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
            System.out.println("✅ Spinner disappeared, proceeding...");
        } catch (Exception e) {
            System.out.println("⚠ Spinner not detected or took too long. Continuing execution...");
        }
    }

    @Test
    public void RepRegisDashboard() throws InterruptedException {
        driver.get("https://qa-reps.corenroll.com/registration");
        Thread.sleep(2000);

        // RANDOM Personal or Company selection
        boolean choosePersonal = random.nextBoolean();
        String valueToSelect = choosePersonal ? "individual" : "business";

        WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='pay_to' and @value='" + valueToSelect + "']")));
        js.executeScript("arguments[0].click();", radio);

        System.out.println("✅ Selected: " + (choosePersonal ? "Personal" : "Company"));

        // Optional pause to verify visually
        Thread.sleep(2000);

        waitForSpinnerToDisappear();
    }

    @Test(dependsOnMethods = {"RepRegisDashboard"})
    public void contactInformation() throws InterruptedException {
        // Wait for page fully loaded
        wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        waitForSpinnerToDisappear();

        // Random Test Data
        String firstName = "John" + random.nextInt(1000);
        String middleInitial = String.valueOf((char) ('A' + random.nextInt(26)));
        String lastName = "Doe" + random.nextInt(1000);
        String ssn = generateRandomSSN();
        String dob = generateRandomDOBOver18();
        String[] expertises = {"Financial Planner", "Group Benefit Broker", "Life Insurance Broker"};
        String[] products = {"Major Medical", "Life Insurance", "Dental + Vision"};
        String[] experiences = {"1-5 years", "6-10 years", "11-20 years", "20+ years"};
        String expertise = expertises[random.nextInt(expertises.length)];
        String product = products[random.nextInt(products.length)];
        String experience = experiences[random.nextInt(experiences.length)];

        // Fill Form
        driver.findElement(By.id("first_name")).sendKeys(firstName);
        driver.findElement(By.id("middle_initial")).sendKeys(middleInitial);
        driver.findElement(By.id("last_name")).sendKeys(lastName);
        driver.findElement(By.id("ssn")).sendKeys(ssn);
        WebElement dobField = driver.findElement(By.cssSelector("input[placeholder='MM/DD/YYYY']"));
        dobField.sendKeys(dob);
        dobField.sendKeys(Keys.TAB);

        selectDropdown("experties_in", expertise);
        selectDropdown("product_sold", product);
        selectDropdown("years_in_business", experience);

        // Print random data
        System.out.println("🧾 Random Test Data:");
        System.out.println("First Name: " + firstName);
        System.out.println("Middle Initial: " + middleInitial);
        System.out.println("Last Name: " + lastName);
        System.out.println("SSN: " + ssn);
        System.out.println("DOB: " + dob);
        System.out.println("Expertise: " + expertise);
        System.out.println("Product Sold: " + product);
        System.out.println("Years in Business: " + experience);

        // Click Next
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-submit")));
        js.executeScript("arguments[0].click();", nextButton);

        System.out.println("✅ Form submitted successfully with random data!");
        Thread.sleep(2000);
    }

    @AfterClass
    public void tearDown() {
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }
}

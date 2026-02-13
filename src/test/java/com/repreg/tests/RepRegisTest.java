package com.repreg.tests;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
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
    private String randomStreetName() { 
    	String[] streetNames = {"Main St", "Highland Ave", "Broadway", "Elm St", "Maple Dr", "Sunset Blvd", "Park Ave"}; 
    	return streetNames[random.nextInt(streetNames.length)]; 
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
    
    @Test(dependsOnMethods = {"contactInformation"})
    public void addressInformation()  throws InterruptedException {
    	System.out.println("\n=== 🏠 Address Information Form Automation ===");

        // Valid US address set
        String[][] cityStateZip = {
            {"New York", "NY", "10001"},
            {"Los Angeles", "CA", "90001"},
            {"Chicago", "IL", "60601"},
            {"Houston", "TX", "77001"},
            {"Phoenix", "AZ", "85001"}
        };

        int index = random.nextInt(cityStateZip.length);
        String selectedCity = cityStateZip[index][0];
        String selectedState = cityStateZip[index][1];
        String selectedZip = cityStateZip[index][2];
        
        String streetAddress = (100 + random.nextInt(900)) + " " + randomStreetName();
        String aptSuite = random.nextBoolean() ? "Apt " + (1 + random.nextInt(999)) : "";

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address1"))).sendKeys(streetAddress);
        driver.findElement(By.id("apt")).sendKeys(aptSuite);
        driver.findElement(By.id("city")).sendKeys(selectedCity);

        WebElement stateField = driver.findElement(By.id("state"));
        System.out.println(stateField.getAttribute("disabled") != null
            ? "🔒 State field is disabled as expected."
            : "❗️ State field is not disabled!");

        driver.findElement(By.id("zip")).sendKeys(selectedZip);

        // Check USPS validation checkbox always
        WebElement uspsCheckbox = driver.findElement(By.id("flexCheckDefault"));
        if (!uspsCheckbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", uspsCheckbox);
            System.out.println("☑️ USPS validation checkbox was unchecked. Now selected via JS.");
        } else {
            System.out.println("☑️ USPS validation checkbox already selected.");
        }

        System.out.println("🧾 Valid Address Data:");
        System.out.println("Street Address: " + streetAddress);
        System.out.println("Apt/Suite: " + aptSuite);
        System.out.println("City: " + selectedCity);
        System.out.println("Zip Code: " + selectedZip);

        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-submit")));
        nextButton.click();
        System.out.println("➡️ Address form submitted successfully!");
    	
    }
   
    	

		@Test(dependsOnMethods = {"addressInformation"})
    	public void loginInformation() throws InterruptedException {
    	    System.out.println("\n=== 🔐 Login Information Form Automation ===");

    	    // Wait for the login form to load
    	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

    	    // Generate random valid email
    	    String email = "repregistrationtest123@gmail.com"; // Unique email
    	    String password = "Pass@" + (100000 + random.nextInt(900000)); // Password with uppercase, special char, and numbers

    	    // Fill Email
    	    WebElement emailField = driver.findElement(By.id("email"));
    	    emailField.clear();
    	    emailField.sendKeys(email);
    	    System.out.println("📧 Entered Email: " + email);

    	    // Click "Display Email Address" checkbox
    	    WebElement displayEmailCheckbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
    	    if (!displayEmailCheckbox.isSelected()) {
    	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", displayEmailCheckbox);
    	        System.out.println("☑️ Display Email Address checkbox selected.");
    	    }

    	    // Fill Password
    	    WebElement passwordField = driver.findElement(By.id("password"));
    	    passwordField.sendKeys(password);
    	    System.out.println("🔑 Entered Password: " + password);

    	    // Confirm Password
    	    WebElement confirmPassword = driver.findElement(By.id("password_confirmation"));
    	    confirmPassword.sendKeys(password);
    	    System.out.println("🔁 Confirmed Password.");

    	    // Optionally click the eye icon to show password for verification
    	    try {
    	        WebElement eyeIcon = driver.findElement(By.id("eyeIconSlash_password"));
    	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", eyeIcon);
    	        System.out.println("👁️ Password revealed for verification.");
    	    } catch (Exception e) {
    	        System.out.println("⚠️ Could not click eye icon: " + e.getMessage());
    	    }

    	    // Submit form
    	    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-submit")));
    	    nextButton.click();
    	    System.out.println("📤 Login Information form submitted successfully!");

    	    // Small wait for next page load
    	    Thread.sleep(2000);
    	}
    	@Test(dependsOnMethods = {"loginInformation"})
    	public void brokerBackOfficeLoginInformation()  throws InterruptedException {

    	    // ----------- TIMEZONE SELECT ---------------
    	    WebElement timezoneDropdown = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//label[contains(text(),'Time zone')]/following::div[contains(@class,'multiselect__tags')]")
    	    ));
    	    timezoneDropdown.click();
    	    Thread.sleep(500);

    	    WebElement timezoneOption = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//span[contains(text(),'Eastern (UTC-5)')]")
    	    ));
    	    js.executeScript("arguments[0].scrollIntoView(true);", timezoneOption);
    	    timezoneOption.click();
    	    Thread.sleep(500);

    	    // ----------- MOBILE NUMBER ---------------
    	    WebElement mobileNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mobile_number")));
    	    mobileNumber.clear();
    	    mobileNumber.sendKeys("9876543210");

    	    // ----------- PHONE NUMBER ---------------
    	    WebElement phoneNumber = driver.findElement(By.id("phone_number"));
    	    phoneNumber.clear();
    	    phoneNumber.sendKeys("1234567890");

    	    // ----------- FAX NUMBER ---------------
    	    WebElement faxNumber = driver.findElement(By.id("fax"));
    	    faxNumber.clear();
    	    faxNumber.sendKeys("1112223333");

    	    // ----------- DISPLAY PHONE CHECKBOX ---------------
    	    WebElement displayPhoneCheckbox = driver.findElement(
    	            By.xpath("//label[contains(text(),'Display Phone')]/preceding-sibling::input")
    	    );
    	    if (!displayPhoneCheckbox.isSelected()) {
    	        displayPhoneCheckbox.click();
    	    }

    	    // ----------- WEB ACCESS ---------------
    	    WebElement webAccess = driver.findElement(By.id("web_access"));
    	    webAccess.clear();
    	    webAccess.sendKeys("www.example.com");

    	    // ----------- SAVE & CONTINUE LATER ---------------
    	    WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Save & Continue Later')]"));
    	    js.executeScript("arguments[0].click();", saveBtn);
    	    Thread.sleep(2000);

    	    // ----------- NEXT BUTTON ---------------
    	    WebElement nextBtn = driver.findElement(By.xpath("//button[contains(text(),'Next')]"));
    	    js.executeScript("arguments[0].click();", nextBtn);

    	    Thread.sleep(3000);
    	}

    	@Test(dependsOnMethods = {"brokerBackOfficeLoginInformation"})
    	public void commissionPaymentInformation() throws InterruptedException {

    	    Thread.sleep(3000);

    	    // ===========================
    	    // 1. RANDOM SELECTION: Personal vs Company
    	    // ===========================
    	    boolean choosePersonal = new Random().nextBoolean(); // TRUE = Personal, FALSE = Company

    	    if (choosePersonal) {
    	        WebElement personalRadio = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.id("pay_to_personal"))); // replace with actual ID
    	        if (!personalRadio.isSelected()) {
    	            js.executeScript("arguments[0].click();", personalRadio);
    	        }
    	        System.out.println("Selected: Personal");
    	    } else {
    	        WebElement companyRadio = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.id("pay_to_company"))); // replace with actual ID
    	        if (!companyRadio.isSelected()) {
    	            js.executeScript("arguments[0].click();", companyRadio);
    	        }
    	        System.out.println("Selected: Company");
    	    }

    	    // ===========================
    	    // 2. RANDOM SELECTION: ACH vs SUPPLY BANK INFO LATER
    	    // ===========================
    	    boolean chooseACH = new Random().nextBoolean();  // TRUE = ACH, FALSE = supply later

    	    if (chooseACH) {
    	        WebElement ach = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.xpath("//input[@id='pay_by' and @value='ach']")));
    	        js.executeScript("arguments[0].click();", ach);
    	        System.out.println("Selected: ACH");
    	    } else {
    	        WebElement supplyLater = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.xpath("//input[@id='pay_by' and @value='check']")));
    	        js.executeScript("arguments[0].click();", supplyLater);
    	        System.out.println("Selected: Supply Bank Info Later");

    	        // ⚠ Do NOT fill ACH details
    	        WebElement next = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.xpath("//button[contains(text(),'Next')]")));
    	        js.executeScript("arguments[0].click();", next);

    	        Thread.sleep(1200);
    	        return;  // STOP — do NOT continue
    	    }

    	    // ===========================
    	    // 3. IF ACH SELECTED → FILL BANK DETAILS
    	    // ===========================

    	    WebElement routing = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("routing_number")));
    	    routing.clear();
    	    routing.sendKeys("021000021");

    	    int len = 6 + new Random().nextInt(10);
    	    StringBuilder acc = new StringBuilder();
    	    for (int i = 0; i < len; i++) acc.append(new Random().nextInt(10));

    	    WebElement accountNumber = driver.findElement(By.id("account_number"));
    	    accountNumber.clear();
    	    accountNumber.sendKeys(acc.toString());

    	    // Account Name
    	    String accountNameVal;
    	    if (choosePersonal) {
    	        String[] fn = {"John","Michael","Sarah","David","Emily","Kevin"};
    	        String[] ln = {"Smith","Brown","Wilson","Johnson","Taylor"};
    	        accountNameVal = fn[new Random().nextInt(fn.length)] + " " + ln[new Random().nextInt(ln.length)];
    	    } else {
    	        String[] part1 = {"Global","Prime","Apex","Summit","BlueRock"};
    	        String[] part2 = {"Tech","Solutions","Systems","LLC","Corp"};
    	        accountNameVal = part1[new Random().nextInt(part1.length)] + " " + part2[new Random().nextInt(part2.length)];
    	    }

    	    WebElement accName = driver.findElement(By.id("account_name"));
    	    accName.clear();
    	    accName.sendKeys(accountNameVal);

    	    // ===========================
    	    // 4. MULTISELECT — ACCOUNT TYPE
    	    // ===========================
    	    List<String> acctTypes = Arrays.asList("Checking", "Savings");
    	    String selectedType = acctTypes.get(new Random().nextInt(acctTypes.size()));

    	    WebElement acctTypeDD = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//label[contains(text(),'Account Type')]/following::div[contains(@class,'multiselect__tags')]")));
    	    js.executeScript("arguments[0].click();", acctTypeDD);

    	    WebElement acctTypeOption = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//div[contains(@class,'multiselect__content-wrapper')]//span[normalize-space()='" + selectedType + "']")));
    	    js.executeScript("arguments[0].click();", acctTypeOption);

    	    // ===========================
    	    // 5. MULTISELECT — HOLDER TYPE
    	    // ===========================
    	    List<String> holderTypes = Arrays.asList("Individual", "Company");
    	    String selectedHolder = holderTypes.get(new Random().nextInt(holderTypes.size()));

    	    WebElement holderDD = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//label[contains(text(),'Account Holder Type')]/following::div[contains(@class,'multiselect__tags')]")));
    	    js.executeScript("arguments[0].click();", holderDD);

    	    WebElement holderOption = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//div[contains(@class,'multiselect__content-wrapper')]//span[normalize-space()='" + selectedHolder + "']")));
    	    js.executeScript("arguments[0].click();", holderOption);

    	    // ===========================
    	    // 6. CLICK NEXT
    	    // ===========================
    	    WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.xpath("//button[contains(text(),'Next')]")));
    	    js.executeScript("arguments[0].click();", nextBtn);

    	    Thread.sleep(1200);
    	}
    	@Test(dependsOnMethods = {"commissionPaymentInformation"})
    	public void signatureVerification() throws InterruptedException {

    	    // === 1. Enter Random Full Name ===
    	    WebElement fullNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
    	            By.id("full_name")
    	    ));

    	    String randomFullName = "TestUser" + new Random().nextInt(9999);
    	    fullNameInput.clear();
    	    fullNameInput.sendKeys(randomFullName);

    	    // === 2. Locate Canvas ===========================
    	    WebElement canvas = wait.until(ExpectedConditions.visibilityOfElementLocated(
    	            By.xpath("//canvas")
    	    ));

    	    // Canvas dimensions
    	    int canvasWidth = Integer.parseInt(canvas.getAttribute("width"));    // 1198
    	    int canvasHeight = Integer.parseInt(canvas.getAttribute("height"));  // 200

    	    Actions act = new Actions(driver);
    	    Random rand = new Random();

    	    // Generate 3–6 random strokes
    	    int strokes = 3 + rand.nextInt(4);

    	    for (int i = 0; i < strokes; i++) {

    	        // Random starting point inside canvas
    	        int startX = rand.nextInt(canvasWidth - 50) + 25;
    	        int startY = rand.nextInt(canvasHeight - 50) + 25;

    	        // Random ending point
    	        int endX = rand.nextInt(canvasWidth - 50) + 25;
    	        int endY = rand.nextInt(canvasHeight - 50) + 25;

    	        act.moveToElement(canvas, startX, startY)
    	                .clickAndHold()
    	                .moveByOffset(endX - startX, endY - startY)
    	                .pause(Duration.ofMillis(150))
    	                .release()
    	                .perform();

    	        Thread.sleep(200);
    	    }

    	    Thread.sleep(1500);
    	}

    @AfterClass
    public void tearDown() {
        System.out.println("Page title is: " + driver.getTitle());
//        driver.quit();
    }
}

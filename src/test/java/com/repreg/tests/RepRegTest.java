package com.repreg.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

@SuppressWarnings("unused")
public class RepRegTest {

	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor js;

	@BeforeClass
	public void setup() {

		// setupChromeDriver
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();

		// initialize wait and JS Executor

		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		js = (JavascriptExecutor) driver;

		// Open Rep Registration URL
		driver.get("https://qa-reps.corenroll.com/registration?repCode=PHOH3123&contract=MTI4OA%3D%3D");

		System.out.println("Browser launcehd and URL opened successfully.");
	}

	@Test
	public void registerNewRep() throws InterruptedException {

		// homepage assertion
		WebElement contactElement = driver.findElement(By.cssSelector("div.contact-details .d-flex.gap-2 h6"));
		String actualText = contactElement.getText().trim();

		Assert.assertEquals(actualText, "Direct Contact: Pukar Hamal", "Contact text did not match!");

		System.out.println("Contact Text Found: " + actualText);

		// click on th "Individual Registraion" button
		WebElement individualReg = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("button.personal-reg.btn-submit.mt-0")));
		individualReg.click();

		// Wait until page is fully loaded
		wait.until(
				driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		// lisensing verification page automation
		WebElement licensedRadioOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("input[name='license_status'][value='yes']")));
		licensedRadioOption.click();
		WebElement licenseDetailsForm = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("div.form-card")));

		Assert.assertTrue(licenseDetailsForm.isDisplayed());

		System.out.println("Add Licesnse Upload Details form is displayed.");

		// select state dropdown
		List<WebElement> licenseDropdowns = driver.findElements(
				By.cssSelector("select.custom-select"));
		licenseDropdowns.get(0).click();

		List<WebElement> stateOptions = driver.findElements(By.cssSelector("option"));
		for (WebElement stateOpt : stateOptions) {

			if (!stateOpt.isDisplayed())
				continue;

			if (stateOpt.getText().trim().equals("California")) {
				js.executeScript("arguments[0], scrollIntoView(True);", stateOpt);
				js.executeScript("arguments[0].click();", stateOpt);
			}
		}
		Thread.sleep(2000);

		// populate license number
		WebElement licenseNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("lable + input[type='text'][maxlength='9']")));

		licenseNumber.click();
		licenseNumber.clear();
		licenseNumber.sendKeys("123456789");
		Thread.sleep(2000);

		// populate license expiry date
		WebElement licenseExpDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("lable + input[type='date'][min='2026-02-01']")));

		licenseExpDate.click();
		licenseExpDate.clear();
		licenseExpDate.sendKeys("12/12/2030");
		Thread.sleep(2000);

		// select resident status
		licenseDropdowns.get(1).click();
		WebElement residentStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("option[value='yes']")));
		residentStatus.click();
		Thread.sleep(2000);

		// rep-registration step2 page assertion

		WebElement stepTwo = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("span.step-count")));
		String stepTwoText = stepTwo.getText().trim();

		Assert.assertEquals(stepTwoText, "| Step 2 of 8", "Step two text did not match!");

		System.out.println("Rep Registration Step 2 Page Text: " + stepTwoText);

		// Wait until page is fully loaded
		wait.until(
				driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		// Scroll the page until Next button is visible
		WebElement nextButton = driver.findElement(By.cssSelector("button.button-submit"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].scrollIntoView({behavior: 'instant', block:
		// 'center', inline: 'center'});", nextButton);
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		js.executeScript("arguments[0].scrollIntoView(true);", nextButton);

		// driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		//
		// wait till the spinner is completely invisible
		// Spinner wait with try/catch for slow backend
		System.out.println("Waiting for spinner to disappear.");
		By spinner = By.cssSelector(".vld-overlay.is-active[aria-label='Loading'][aria-busy='true']");
		WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(120));
		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		// Thread.sleep(3000);

		// populate first name
		WebElement firstName = wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.input-icon .reg-field")));
		// js.executeScript("arguments[0].value='Rep';", firstName);
		firstName.click();
		firstName.clear();
		firstName.sendKeys("Rep");
		// Pause execution for 2 seconds (2000 milliseconds)
		Thread.sleep(2000);

		// populate middle name
		WebElement middleName = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("middle_initial")));
		middleName.click();
		middleName.clear();
		middleName.sendKeys("Reg");
		// driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		Thread.sleep(2000);

		// populate last name
		WebElement lastName = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("last_name")));
		lastName.click();
		lastName.clear();
		lastName.sendKeys("Automation");
		Thread.sleep(2000);

		// populate SSN
		WebElement ssnNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("ssn")));
		ssnNumber.click();
		ssnNumber.clear();
		ssnNumber.sendKeys("534634565");
		Thread.sleep(2000);

		// populate Date of Birth
		WebElement datePickerContainer = driver.findElement(
				By.cssSelector("div.mx-datepicker"));

		WebElement dobInput = datePickerContainer.findElement(
				By.cssSelector("input.mx-input"));
		dobInput.clear();
		dobInput.sendKeys("01/13/1999");
		// js.executeScript("arguments[0].value='01/01/1990';
		// arguments[0].dispatchEvent(new Event('input'));", dobInput);

		// click outside to close the calendar
		driver.findElement(By.tagName("body")).click();
		Thread.sleep(2000);

		// drop down option seletion of Expertise field
		// click to open select expertise dropdown
		// WebElement selectExpertise = driver.findElement(
		// By.cssSelector(".multiselect__tags"));
		// selectExpertise.click();
		List<WebElement> dropdowns = driver.findElements(By.cssSelector(".multiselect__tags"));
		dropdowns.get(0).click();
		// wait until options are visible
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("li.multiselect__element .multiselect__option")));

		// click on the one of the dropdown options

		List<WebElement> expertiseOptions = driver.findElements(
				By.cssSelector("li.multiselect__element .multiselect__option"));
		for (WebElement eOpt : expertiseOptions) {

			if (!eOpt.isDisplayed())
				continue;

			if (eOpt.getText().trim().contains("Property and Casulity Broker")) {

				js.executeScript("arguments[0].scrollIntoView(true);", eOpt);
				js.executeScript("arguments[0].click();", eOpt);
				break;
			}
		}
		driver.switchTo().activeElement().sendKeys(Keys.TAB);
		Thread.sleep(2000);

		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		js.executeScript("arguments[0].scrollIntoView(true);", nextButton);

		dropdowns.get(1).click();
		Thread.sleep(2000);

		List<WebElement> productOptions = driver.findElements(
				By.cssSelector("li.multiselect__element span.multiselect__option"));
		for (WebElement pOpt : productOptions) {

			if (!pOpt.isDisplayed())
				continue;

			if (pOpt.getText().trim().equals("Medicare Supplement")) {
				// js.executeScript("arguments[0].scrollIntoView(True);", pOpt);
				js.executeScript("arguments[0].click();", pOpt);
			}
		}
		Thread.sleep(2000);

		dropdowns.get(2).click();
		Thread.sleep(2000);
		// click on one of the options
		List<WebElement> experienceOptions = driver.findElements(
				By.cssSelector("li.multiselect__element .multiselect__option"));
		for (WebElement expOpt : experienceOptions) {

			if (!expOpt.isDisplayed())
				continue;

			if (expOpt.getText().trim().equals("6-10 years")) {
				// js.executeScript("arguments[0], scrollIntoView(True);", expOpt);
				js.executeScript("arguments[0].click();", expOpt);
			}
		}
		Thread.sleep(2000);
		System.out.println("All the input fields are populated.");

		WebElement chatBot = driver.findElement(By.className("pe-cursor"));
		chatBot.click();
		System.out.println("Chatbot is disabled.");

		nextButton.click();

		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		// rep-registration step3 page assertion

		// WebElement stepFour =
		// wait.until(ExpectedConditions.visibilityOfElementLocated(
		// By.cssSelector("div.vld-icon")));
		// String stepFourText = stepFour.getText().trim();
		//
		// Assert.assertEquals(stepFourText, "| Step 4 of 8", "Step four text did not
		// match!");
		//
		// System.out.println("Rep Registration Step 4 Page Text: " + stepFourText);

		WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(".container")));
		Assert.assertTrue(
				container.getText().contains("Address Information"),
				"You've not reached to the address infomation page of rep registration.");

		// populate the input fields of the address information page
		WebElement streetAddress1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#address1")));
		streetAddress1.clear();
		streetAddress1.sendKeys("57th Street Midtown Manhattan");
		System.out.println("Street Address is populated.");
		Thread.sleep(2000);

		WebElement city = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#city")));
		city.clear();
		city.sendKeys("New York");
		System.out.println("City is populated.");
		Thread.sleep(2000);

		WebElement zipCode = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#zip")));
		zipCode.clear();
		zipCode.sendKeys("10019");
		System.out.println("Zip is populated.");
		Thread.sleep(2000);

		WebElement validateZip = wait.until(ExpectedConditions.elementToBeClickable(
				By.id("flexCheckDefault")));
		validateZip.click();

		WebElement nextPage = wait.until(ExpectedConditions.elementToBeClickable(
				By.cssSelector(".button-submit")));
		nextPage.click();

		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		// login information page assertion
		WebElement stepFive = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("span.step-count")));
		String stepFiveText = stepFive.getText().trim();

		Assert.assertEquals(stepFiveText, "| Step 5 of 8", "Step five text did not match!");

		System.out.println("Rep Registration Step 5 Page Text: " + stepFiveText);

		Assert.assertTrue(
				container.getText().contains("Login Information"),
				"You've not reached to the login infomation page of rep registration.");

		// populate the email address field

		WebElement emailAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("email")));
		emailAddress.clear();
		emailAddress.sendKeys("testrepcts+313@gmail.com");
		System.out.println("Email Address field is populated");
		Thread.sleep(2000);

		// check display email address checkbox

		WebElement displayEmailCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(".mb-1")));
		displayEmailCheckbox.click();
		System.out.println("Display Email Address checkbox is checked.");
		Thread.sleep(2000);

		// displayy email assertion and populate
		WebElement displayEmailLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(".d-flex.align-items-center label")));
		Assert.assertEquals(displayEmailLabel.getText().trim(), "Display Email Address");

		WebElement displayEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#display_email")));
		displayEmail.clear();
		displayEmail.sendKeys("testrepcts+313@gmail.com");
		System.out.println("Dsplay email is populated.");
		Thread.sleep(2000);

		// populate the password field
		WebElement passWord = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("password")));
		passWord.clear();
		passWord.sendKeys("Cts@2019");
		Thread.sleep(1500);

		WebElement eyeIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("eyeIconSlash_password")));
		eyeIcon.click();
		Thread.sleep(2000);

		// populate the confirm password field
		WebElement confirmPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("password_confirmation")));
		confirmPassword.clear();
		confirmPassword.sendKeys("Cts@2019");
		Thread.sleep(1500);

		WebElement eyeIcon2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("eyeIconSlash_password_confirmation")));
		eyeIcon2.click();
		Thread.sleep(2000);

		// click the next button
		WebElement loginInfoNext = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("button.button-submit")));
		loginInfoNext.click();

		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		WebElement stepSix = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("span.step-count")));
		String stepSixText = stepSix.getText().trim();

		Assert.assertEquals(stepSixText, "| Step 6 of 8", "Step six text did not match!");

		System.out.println("Rep Registration Step 6 Page Text: " + stepSixText);

		Assert.assertTrue(
				container.getText().contains("Broker Back Office Login Information"),
				"You've not reached to the broker back office login infomation page of rep registration.");

		// populate the Time Zone field

		WebElement timeZone = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.className("multiselect__tags")));

		List<WebElement> timeZoneOptions = driver.findElements(
				By.cssSelector("li.multiselect__element .multiselect__option"));
		for (WebElement tOpt : timeZoneOptions) {

			if (!tOpt.isDisplayed())
				continue;

			if (tOpt.getText().trim().contains("Hawaii-Aleutian (UTC-10)")) {

				js.executeScript("arguments[0].scrollIntoView(true);", tOpt);
				js.executeScript("arguments[0].click();", tOpt);
				break;
			}
		}

		System.out.println("Time zone dropdown is selected.");
		Thread.sleep(1500);

		// populate the mobile number field
		WebElement mobileNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#mobile_number")));
		mobileNumber.clear();
		mobileNumber.sendKeys("3445452625");
		System.out.println("Mobile number field is populated.");
		Thread.sleep(2000);

		// populate the phone number field
		WebElement phoneNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#phone_number")));
		phoneNumber.clear();
		phoneNumber.sendKeys("4563546357");
		System.out.println("Phone number field is populated.");
		Thread.sleep(2000);

		// populate the phone number field
		WebElement faxNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#fax")));
		phoneNumber.clear();
		phoneNumber.sendKeys("1231231234");
		System.out.println("Fax field is populated.");
		Thread.sleep(2000);

		// check display phone checkbox
		WebElement displayPhoneCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("label.mb-0 .mb-2")));
		displayPhoneCheckbox.click();
		System.out.println("Display Phone checkbox is checked.");
		Thread.sleep(2000);

		// display email assertion and populate
		WebElement displayPhoneLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(".d-flex.align-items-center label")));
		Assert.assertEquals(displayPhoneLabel.getText().trim(), "Display Phone");

		WebElement displayPhone = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("display_phone")));
		displayEmail.clear();
		displayEmail.sendKeys("2424524522");
		System.out.println("Display phone is populated.");
		Thread.sleep(2000);

		// click on next button
		WebElement brokerLoginInfoNext = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("button.button-submit")));
		brokerLoginInfoNext.click();

		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		// Commission Payment Info page assertion
		WebElement stepSeven = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("span.step-count")));
		String stepSevenText = stepSeven.getText().trim();

		Assert.assertEquals(stepSevenText, "| Step 7 of 8", "Step seven text did not match!");

		System.out.println("Rep Registration Step 7 Page Text: " + stepSevenText);

		Assert.assertTrue(
				container.getText().contains("Commission Payment Information"),
				"You've not reached to the commission payment infomation page of rep registration.");
		Thread.sleep(1500);

		// select radio option of Pay To
		WebElement payTo = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("label.custom-radio:has(input[value='individual'])")));
		payTo.click();
		Thread.sleep(2000);

		// select payment method radio option
		WebElement paymentMethod = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("label.custom-radio:has(input[value='ach'])")));
		Thread.sleep(2000);

		WebElement achInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(".form-group")));
		Assert.assertTrue(
				achInfo.getText().contains(" Personal Name "),
				"Ach option for payment method is not selected.");
		Thread.sleep(1500);

		try {
			longWait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
			System.out.println("Spinner disappeared, form is ready to interact.");
		} catch (Exception e) {
			System.out.println("Timeout: Spinner still visible, proceeding anyway.");
		}

		WebElement stepEight = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("span.step-count")));
		String stepEightText = stepSix.getText().trim();

		Assert.assertEquals(stepEightText, "| Step 8 of 8", "Step eight text did not match!");

		System.out.println("Rep Registration Step 8 Page Text: " + stepEightText);

		Assert.assertTrue(
				container.getText().contains("Signature & Verification"),
				"You've not reached to the Signature & Verification page of rep registration.");
		Thread.sleep(1500);

		WebElement fullName = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("#full_name")));
		fullName.clear();
		fullName.sendKeys("Rep Reg Automation");
		System.out.println("Full name field is populated.");
		Thread.sleep(1500);

		// sign the rep registration
		WebElement canvas = driver.findElement(By.cssSelector("canvas"));

		// Create Actions instance
		Actions actions = new Actions(driver);

		// Get the canvas location & size
		int width = canvas.getSize().getWidth();
		int height = canvas.getSize().getHeight();

		// Start drawing: move to center of canvas
		actions.moveToElement(canvas, width / 4, height / 2) // starting point (offset inside canvas)
				.click()
				.perform();

		// Create signature strokes
		actions.moveByOffset(30, 10)
				.moveByOffset(30, -20)
				.moveByOffset(30, 20)
				.moveByOffset(-20, 15)
				.moveByOffset(-30, -10)
				.moveByOffset(-40, 20)
				.release()
				.perform();
		Thread.sleep(1500);

		//
		//// populate the password field
		// WebElement passWord =
		// wait.until(ExpectedConditions.visibilityOfElementLocated(
		// By.id("password")));
		// passWord.clear();
		// passWord.sendKeys("Cts@2019");
		// Thread.sleep(1500);
		//
		// WebElement eyeIcon =
		// wait.until(ExpectedConditions.visibilityOfElementLocated(
		// By.id("eyeIconSlash_password")));
		// eyeIcon.click();
		// Thread.sleep(2000);
		//
		//// populate the confirm password field
		// WebElement confirmPassword =
		// wait.until(ExpectedConditions.visibilityOfElementLocated(
		// By.id("password_confirmation")));
		// confirmPassword.clear();
		// confirmPassword.sendKeys("Cts@2019");
		// Thread.sleep(1500);
		//
		//// clic on next button
		//

	}

	// @AfterClass
	// public void teardown() {
	// if (driver != null) {
	// driver.quit();
	// System.out.println("Browser closed successfully.");
	// }
	// }

}

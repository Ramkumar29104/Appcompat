package test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class OpenPackages {

	public WebDriver driver;
	public int totalApps;
	public String fileName;
	public String fileName1;
	public String url;
	public String path;

	public int from;
	public int to;

	public Scanner input = new Scanner(System.in);

	public void getInputs() throws Exception {

		System.out.print("Paste the testtracker link here: ");
		url = input.next();

		System.out.println("Enter the First app number");
		from = input.nextInt();

		System.out.println("Enter the second app number");
		to = input.nextInt();

	}

	public void invokeBrowser() {
		try {
			System.out.println("Invoking the Chrome Browser");

			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
			driver.manage().window().maximize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void navigateToUrl() {

		input = new Scanner(System.in);
		driver.get(url);
	}

	public void login() throws Exception {
		WebElement userName, password, signIn;
		userName = driver.findElement(By.xpath("//input[@id='username']"));
		userName.sendKeys(getPropertyDetails("ldap"));
		password = driver.findElement(By.xpath("//input[@id='password']"));
		password.sendKeys(getPropertyDetails("password"));
		signIn = driver.findElement(By.xpath("//input[@type='submit']"));
		signIn.click();
	}

	public void openIndividual() throws AWTException {

		WebElement appPackage;

		Actions action = new Actions(driver);
		Robot rb = new Robot();

		appPackage = driver
				.findElement(By.xpath("(//form[@name='defaultForm']//td[@class='tooltipped']/a)[1]"));
		//scrollIntoView(appPackage);
		rb.keyPress(KeyEvent.VK_CONTROL);
		appPackage.click();
		rb.keyRelease(KeyEvent.VK_CONTROL);

		driver.switchTo().window(driver.getWindowHandles().toArray()[1].toString());

	}

	public String getPropertyDetails(String key) throws Exception {
		String file = "data/Environment.properties";
		InputStream input = new FileInputStream(file);
		Properties pro = new Properties();
		pro.load(input);
		String proValue = pro.getProperty(key);
		return proValue;
	}

	public String getJsonDetails(String key) throws Exception {
		String configPath = getPropertyDetails("configPath");
		FileReader read = new FileReader(configPath);
		JSONParser par = new JSONParser();
		Object parObj = par.parse(read);
		JSONObject jsonObj = (JSONObject) parObj;
		String jsonValue = (String) jsonObj.get(key);
		return jsonValue;
	}

	public String stringSplit(WebElement element) {
		String name = element.getText();
		String[] splitName = name.split("-");
		String appName = splitName[1].replace(" ", "");
		return appName;
	}

	public void scrollIntoView(WebElement element) {

		JavascriptExecutor exe = (JavascriptExecutor) driver;

		int x = element.getRect().getX();
		int y = element.getRect().getY();
		String cmd = String.format("window.scrollTo(%d,%d)", x, y);

		exe.executeScript(cmd);

	}

	public static void main(String[] args) throws Exception {

		OpenPackages open = new OpenPackages();
		open.getInputs();
		open.invokeBrowser();
		open.navigateToUrl();
		open.login();
		open.openIndividual();

	}

}

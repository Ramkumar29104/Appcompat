package test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BugList {

	WebDriver driver;
	public Scanner input;

	public void invokeBrowser() {
		try {
			System.out.println("Invoking the Chrome Browser");
			WebDriverManager.chromedriver().setup();

			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
			driver.manage().window().maximize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void navigateToUrl() {
		input = new Scanner(System.in);
		System.out.print("Paste the testtracker link here: ");
		String url = input.next();
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

	public void getBugDetails() throws Exception {

		WebElement buildElement = driver.findElement(By.xpath("//div[@class='page-content']//h5"));
		String[] name = buildElement.getText().split(" ");
		String buildDateString = name[0];
		String build = name[1];

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date buildDate = dateFormat.parse(buildDateString);

		List<String> newBugs = new ArrayList<String>();
		List<String> oldBugs = new ArrayList<String>();

		try {
			String bugId = null;
			String bugDateString = null;
			String bugPriority = null;
			String bugDescription = null;
			String bug = null;

			int i = 1;
			int j = 2;
			int k = 4;
			int l = 5;

			System.out.println("Test tracker's Build: " + build);
			
			while(driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + i + "]")).getText() != null) {
				bugId = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + i + "]")).getText();
				bugPriority = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + j + "]")).getText();
				bugDescription = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + k + "]"))
						.getText();
				bug = "b/" + bugPriority + " | " + bugId + " | " + bugDescription;

				bugDateString = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + l + "]")).getText();
				Date bugDate = getDate(bugDateString);
				if (bugDate.equals(buildDate) || bugDate.after(buildDate)) {
					newBugs.add(bug);
				} else {
					oldBugs.add(bug);
				}
				i = i + 6;
				j = j + 6;
				k = k + 6;
				l = l + 6;
			}
		} catch (Exception e) {
		}

		System.out.println("New Bugs:");
		try {
			Iterator<String> newIterator = newBugs.iterator();
			if (newBugs.size() == 0) {
				System.out.println("This testtracker don't have any new bugs");
			} else {
				while (newIterator.hasNext()) {
					System.out.println(newIterator.next());
				}
			}
		} catch (Exception e) {
		}

		System.out.println("Old Bugs:");
		try {
			Iterator<String> oldIterator = oldBugs.iterator();
			if (oldBugs.size() == 0) {
				System.out.println("This testtracker don't have any old bugs");
			} else {
				while (oldIterator.hasNext()) {
					System.out.println(oldIterator.next());
				}
			}
		} catch (Exception e) {
		}
	}

	public void closeBrowser() {
		driver.close();
	}

	public String getPropertyDetails(String key) throws Exception {
		String file = "data/Environment.properties";
		InputStream input = new FileInputStream(file);
		Properties pro = new Properties();
		pro.load(input);
		String proValue = pro.getProperty(key);
		return proValue;
	}

	public Date getDate(String value) throws Exception {
		String[] valueArray = value.split(",");
		String[] dateArray = valueArray[0].split("/");
		String month = dateArray[0];
		String day = dateArray[1];
		String year = dateArray[2];
		if (month.length() < 2) {
			month = "0" + month;
		}
		if (day.length() < 2) {
			day = "0" + day;
		}
		String dateString = year + "-" + month + "-" + day;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse(dateString);
		return date;

	}

	public void scrollToView(WebDriver driver, WebElement element) {

		JavascriptExecutor exe = (JavascriptExecutor) driver;
		int x = element.getRect().getX();
		int y = element.getRect().getY();
		String cmd = String.format("window.ScrollTo(%d,%d);", x, y);
		exe.executeScript(cmd);
	}
	
	public void open() {
		driver.get("https://buganizer.corp.google.com/issues/299170947");
	}

	public static void main(String[] args) throws Exception {
		BugList list = new BugList();

		list.invokeBrowser();
		list.navigateToUrl();
		list.login();
		list.getBugDetails();
		list.closeBrowser();
	}
}

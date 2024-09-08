package test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BugListFull {

	WebDriver driver;
	public Scanner input = new Scanner(System.in);
	String newBugIds;
	String oldBugIds;

	public String url;

	public void getInputs() throws Exception {

		System.out.print("Paste the testtracker link here: ");
		url = input.next();
	}

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

		// Initialization of elements
		WebElement buildElement = driver.findElement(By.xpath("//div[@class='page-content']//h5"));
		String[] name = buildElement.getText().split(" ");
		String buildDateString = name[0];
		String build = name[1];

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date buildDate = dateFormat.parse(buildDateString);

		List<String> newBugs = new ArrayList<String>();
		List<String> oldBugs = new ArrayList<String>();
		List<String> failApps = new ArrayList<String>();

		// Getting bug info and segregating in new bugs and existing bugs
		int failAppsCount = 0;
		StringBuffer newBugIdsBuffer = new StringBuffer();
		StringBuffer oldBugIdsBuffer = new StringBuffer();

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

			// Printing Build
			System.out.println("Test tracker's Build: " + build);
			System.out.println();

			while (driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + i + "]")).getText() != null) {

				bugPriority = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + i + "]")).getText();
				bugId = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + j + "]")).getText();
				bugDescription = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + k + "]"))
						.getText();
				bug = "b/" + bugId + " | " + bugPriority + " | " + bugDescription;

				bugDateString = driver.findElement(By.xpath("(//table[@id='bug_table']//tr//td)[" + l + "]")).getText();
				Date bugDate = getDate(bugDateString);
				if (bugDate.equals(buildDate) || bugDate.after(buildDate)) {
					newBugs.add(bug);
					newBugIdsBuffer.append(bugId + " | ");

				} else {
					oldBugs.add(bug);
					oldBugIdsBuffer.append(bugId + " | ");
					oldBugIdsBuffer.substring(0, oldBugIdsBuffer.length() - 3);
				}

				String failApp = getApkFromBugDesc(bugDescription);
				if (!failApps.contains(failApp)) {
					failApps.add(failApp);
					failAppsCount += 1;
				}

				i = i + 6;
				j = j + 6;
				k = k + 6;
				l = l + 6;

			}

		} catch (Exception e) {
		}

		// Printing new bugs list
		System.out.println("New Bugs:");
		try {
			Iterator<String> newIterator = newBugs.iterator();
			if (newBugs.size() == 0) {
				System.out.println("This testtracker doesn't have any new bugs");
			} else {
				while (newIterator.hasNext()) {
					System.out.println(newIterator.next());
				}
			}
		} catch (Exception e) {
		}

		// Printing Old bug list
		System.out.println("Existing bugs:");
		try {
			Iterator<String> oldIterator = oldBugs.iterator();
			if (oldBugs.size() == 0) {
				System.out.println("This testtracker doesn't have any Existing bugs");
			} else {
				while (oldIterator.hasNext()) {
					System.out.println(oldIterator.next());
				}
			}
		} catch (Exception e) {
		}

		int lsPassAppsCount = 0;
		try {
			int i = 14;
			while (driver.findElement(By.xpath("(//td[@class='linkedeffort'])[" + i + "]")).getText().contains("0")
					|| driver.findElement(By.xpath("(//td[@class='linkedeffort'])[" + i + "]")).getText()
							.contains("100")) {
				if (driver.findElement(By.xpath("(//td[@class='linkedeffort'])[" + i + "]")).getText()
						.contains("100")) {
					lsPassAppsCount++;
				}
				i += 16;
			}

		} catch (Exception e) {
		}

		if (newBugIdsBuffer.length() > 3) {
			newBugIds = newBugIdsBuffer.substring(0, newBugIdsBuffer.length() - 3);
		}
		System.out.println("------------------------------------");
		System.out.println();
		System.out.println("New BugId List: " + newBugIds);

		if (oldBugIdsBuffer.length() > 3) {
			oldBugIds = oldBugIdsBuffer.substring(0, oldBugIdsBuffer.length() - 3);
		}
		System.out.println("Existing BugId List: " + oldBugIds);
		System.out.println("------------------------------------");
		System.out.println();

		// Getting Date in format
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(buildDateString, inputFormatter);

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		String formattedDate = date.format(outputFormatter);

		// Total Pass/done apps
		List<WebElement> elements = driver.findElements(By.xpath("//td[@title='Pass+Fail+Skip/Total %']"));
		int pixelPassAppsCount = 0;

		try {
			for (int i = 0; i < elements.size(); i++) {
				if (elements.get(i).getText().contains("100")) {
					pixelPassAppsCount++;
				}
			}
		} catch (Exception e) {

		}

		// Getting Percentage
		String percentage = new String();
		try {
			percentage = driver.findElement(By.xpath("(//table[@width='90%']//tfoot//td[@align='center'])[10]"))
					.getText();

		} catch (Exception e) {

		}

		// Getting Devices List
		driver.findElement(By.xpath("//a[text()='Summary by Test Environment']")).click();

		Set<String> deviceList = new HashSet<String>();
		deviceList.add("Tangor pro");
		deviceList.add("Felix");
		String Devices = new String();

		String Device = new String();
		String dev = new String();

		try {

			int n = 12;

			while (n < 962) {

				String per = driver.findElement(By.xpath("(//table[@width='80%']//td)[" + n + "]")).getText();

				if (per.contains("100")) {

					String devicePlusVersion = driver
							.findElement(By.xpath("(//table[@width='80%']//td)[" + (n - 11) + "]")).getText();

					String[] splitdevicePlusVersion = devicePlusVersion.split("-");
					String device = splitdevicePlusVersion[0].replace(" ", "");

					char[] devCharArray = device.toCharArray();

					for (int i = 0; i < devCharArray.length; i++) {
						if (i == 0) {
							dev += devCharArray[i];
							Device += dev.toUpperCase();
						} else {
							Device += devCharArray[i];
						}
					}
				}
				deviceList.add(Device);
				dev = "";
				Device = "";
				n += 12;
			}

			Object[] array = deviceList.toArray();

			for (int i = 0; i < array.length; i++) {
				if (i == array.length - 1) {
					Devices += array[i];
				} else {
					Devices += array[i] + "/";
				}
			}

		} catch (Exception e) {

		}

		System.out.println("**QA Update " + formattedDate + "**");
		System.out.println();
		System.out.println("- Build: " + build);
		System.out.println("- Test Tracker: [Link](" + url + ")");
		System.out.println("- Devices: " + Devices);
		System.out.println("- Testing status: " + percentage);
		System.out.println("- Total apps tested: " + (pixelPassAppsCount + lsPassAppsCount));
		System.out.println("- Failed Apps: " + failAppsCount);

		if (failAppsCount != (newBugs.size() + oldBugs.size())) {

			System.out.println("- Total Bugs: " + (newBugs.size() + oldBugs.size()));
		}
	}

	public void updateBug() {
		driver.get("https://b.corp.google.com/home");
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

	public String getApkFromBugDesc(String bugDesc) throws Exception {
		String[] split = bugDesc.split("-");
		String split1 = split[0].replace("[App Compat][Dev Test]", "");
		String split2 = split1.replace(" ", "");
		String split3 = split2.replace("{", "");
		String apkName = split3.replace("\"", "");
		return apkName;
	}

	public String stringSplit(WebElement element) {
		String name = element.getText();
		String[] splitName = name.split("-");
		String appName = splitName[1].replace(" ", "");
		return appName;
	}

	public static void main(String[] args) throws Exception {
		BugListFull list = new BugListFull();

		list.getInputs();
		list.invokeBrowser();
		list.navigateToUrl();
		list.login();
		list.getBugDetails();
		list.updateBug();
		// list.closeBrowser();

	}
}

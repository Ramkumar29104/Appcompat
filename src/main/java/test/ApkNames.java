package test;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ApkNames {

	public WebDriver driver;
	public int totalApps;
	public String fileName;
	public String fileName1;
	public String url;
	public String path;

	public Scanner input = new Scanner(System.in);

	public void getInputs() throws Exception {

		System.out.print("Paste the testtracker link here: ");
		url = input.next();

		System.out.println("Kindly select the text file");
		System.out.println("1.top100.txt");
		System.out.println("2.top1k.txt");
		System.out.println("3.newRequest.txt");
		int fileName = input.nextInt();
		String textFilePath = getJsonDetails("textFilePath");
		
		String configPath = getPropertyDetails("configPath");

		ObjectMapper obj = new ObjectMapper();
		File jsonFile = new File(configPath);
		ObjectNode rootNode = obj.readValue(jsonFile, ObjectNode.class);
		switch (fileName) {
		case 1:
			rootNode.put("apkList", "top100.txt");
			break;
		case 2:
			rootNode.put("apkList", "top1k.txt");
			break;
		case 3:
			rootNode.put("apkList", "newRequest.txt");
			break;
		default:
			System.out.println("Kindly give the correct text file");
			System.exit(0);
		}

		obj.writeValue(jsonFile, rootNode);
		fileName1 = getJsonDetails("apkList");
		path = textFilePath + fileName1;

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

	public void getApkNames() throws Exception {

		int a = 1, b = 2;
		String appNamePixel = null, appNameLs = null;
		List<String> appList = new ArrayList<String>();
		System.out.println("Kindly wait for some time, Appreciate your patience");
		try {
			WebElement apkLinkPixel;

			while (driver.findElement(By.xpath("(//td[@class='tooltipped']//a)[" + a + "]")).getText() != null) {
				apkLinkPixel = driver.findElement(By.xpath("(//td[@class='tooltipped']//a)[" + a + "]"));
				appNamePixel = stringSplit(apkLinkPixel);
				appList.add(appNamePixel);
				a += 1;
			}
		} catch (Exception e) {
		}

		try {
			WebElement apkLinkLs;

			while ((driver.findElement(By.xpath("(//a[@class='linkedeffort'])[" + b + "]")).getText() != null)) {
				apkLinkLs = driver.findElement(By.xpath("(//a[@class='linkedeffort'])[" + b + "]"));
				appNameLs = stringSplit(apkLinkLs);
				appList.add(appNameLs);
				b += 1;
			}
		} catch (Exception e) {
		}

		totalApps = a + b - 3;

		try {
			fileWrite(path, appList);
		} catch (Exception e) {
		}

		try {
			WebElement apkLinkPixel = driver.findElement(By.xpath("(//td[@class='tooltipped']//a)[1]"));
			String firstAppPixel = stringSplit(apkLinkPixel);
			if (appList.contains(firstAppPixel) && appList.contains(appNamePixel)) {
				System.out.print("Pixel Test Effort-    ");
				System.out.print("First App: " + firstAppPixel + "     ");
				System.out.print("Last app: " + appNamePixel + "    ");
				System.out.print("Total= " + (a - 1) + "    ");
				System.out.println("Order= 1-" + (a - 1));
				try {
					WebElement apkLinkLs = driver.findElement(By.xpath("(//a[@class='linkedeffort'])[2]"));
					String firstAppLs = stringSplit(apkLinkLs);
					if (appList.contains(firstAppLs) && appList.contains(appNameLs)) {
						System.out.print("LS Test Effort   -    ");
						System.out.print("First App: " + firstAppLs + "    ");
						System.out.print("Last app: " + appNameLs + "    ");
						System.out.print("Total= " + (b - 2) + "    ");
						System.out.println("Order= " + a + "-" + totalApps);
					} else {
						System.out.println("LS apps are not added to the file " + fileName1);
					}
				} catch (Exception e) {
				}
				WebElement buildElement = driver.findElement(By.xpath("//div[@class='page-content']//h5"));
				String[] name = buildElement.getText().split(" ");
				String build = name[1];
				System.out.println("Totally " + totalApps + " apps from the test tracker having build " + build
						+ " are successfully added to the file " + fileName1);
			} else {
				System.out.println("Pixel apps are not added to the file " + fileName1);
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

	public void fileWrite(String path, List<String> appList) {

		try {
			FileWriter file = new FileWriter(path);
			BufferedWriter buff = new BufferedWriter(file);
			for (String app : appList) {
				buff.write(app);
				buff.newLine();
			}

			buff.flush();
			buff.close();
			file.flush();
			file.close();
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) throws Exception {

		ApkNames apk = new ApkNames();
		apk.getInputs();
		apk.invokeBrowser();
		apk.navigateToUrl();
		apk.login();
		apk.getApkNames();
		apk.closeBrowser();

	}
}

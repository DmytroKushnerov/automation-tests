import io.github.bonigarcia.wdm.WebDriverManager;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class AddToCartTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Parameters("browser")
    @BeforeMethod
    public void setUp(@Optional("chrome") @NotNull String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test //adding an item to the card
    public void addToCartTest() {
        driver.get("https://demowebshop.tricentis.com/smartphone");

        String productUrl = driver.getCurrentUrl();
        String productName = driver.findElement(By.className("product-name")).getText();

        driver.findElement(By.cssSelector(".add-to-cart-button")).click();

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='content']")));
        Assert.assertEquals("The product has been added to your shopping cart", successMessage.getText());

        driver.get("https://demowebshop.tricentis.com/cart");

        List<WebElement> tds = driver.findElements(By.tagName("td"));
        boolean isProductInCart = false;
        for (WebElement td : tds) {
            if (td.getText().equals(productName)) {
                isProductInCart = true;
                break;
            }
        }
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[@class='product']/a")));
        isProductInCart = link.getAttribute("href").equals(productUrl);

        Assert.assertTrue(isProductInCart);
    }

    @Test //removing an item from the card
    public void removeFromCartTest() {
        // Use the method from AddToCardTest to add an item to the cart
        addToCartTest();


        // Find the "removefromcart" checkbox and select it
        WebElement removeCheckbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("removefromcart")));
        removeCheckbox.click();

        // Click the "Update shopping cart" button
        WebElement updateCartButton = driver.findElement(By.cssSelector(".update-cart-button"));
        updateCartButton.click();

        // Wait for the "Your Shopping Cart is empty!" message to appear
        WebElement emptyCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='order-summary-content']")));

        // Verify the cart is empty
        Assert.assertEquals("Your Shopping Cart is empty!", emptyCartMessage.getText().trim());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
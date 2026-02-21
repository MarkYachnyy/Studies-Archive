import pytest
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestPoryadokLogin:
    """Тесты авторизации на сайте Порядок"""

    @pytest.fixture
    def links(self):
        return ["https://poryadok.ru/catalog/musornye_pakety/2437139/", "https://poryadok.ru/catalog/shpateli/46009/", "https://poryadok.ru/catalog/sredstva_zashchity_truda/683580/"]

    @pytest.fixture
    def driver(self):
        chrome_options = Options()
        chrome_options.add_argument("--disable-notifications")
        prefs = {
            "profile.default_content_setting_values.notifications": 2
        }
        chrome_options.add_experimental_option("prefs", prefs)
        driver = webdriver.Chrome(options=chrome_options)
        driver.implicitly_wait(10)
        yield driver
        driver.quit()

    def test_name_search(self, driver, links):

        product_names = []
        total_price = 0

        driver.implicitly_wait(1)

        for link in links:
            driver.get(link)

            name_title = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CLASS_NAME, "detail-product-name"))
            )

            product_names.append(name_title.text)
            price_div = driver.find_element(By.CLASS_NAME, "price-block__product-price")
            try:
                discounted_price_element = price_div.find_element(By.CLASS_NAME, "product-price__price--discounted")
                print("WHIGGA")
                total_price += int("".join(discounted_price_element.text[:-2].split()))
            except NoSuchElementException:
                price_element = price_div.find_element(By.CLASS_NAME, "product-price__price")
                print("NIGGA")
                total_price += int("".join(price_element.text[:-2].split()))

            cart_btn = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.CLASS_NAME, "add-to-cart"))
            )

            cart_btn.click()

        svg_go_to_fav = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.XPATH, "//*[local-name()='svg' and contains(@class, 'pi-cart-outline')]"))
        )
        svg_go_to_fav.click()

        WebDriverWait(driver, 10).until(
            EC.url_to_be("https://poryadok.ru/personal/checkout/")
        )

        product_names_elements = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.CLASS_NAME, "checkout-cart-product__name"))
        )

        present_names = list(map(lambda el: el.text, product_names_elements))
        added = True
        for name in product_names:
            if name not in present_names:
                added = False
                break

        assert added, "Не все товары были добавлены в корзину " + str(total_price)

        final_cost_text = driver.find_element(By.CLASS_NAME, "bx-soa-changeCostSign")
        visible_price = int("".join(final_cost_text.text[:-2].split()))

        assert visible_price == total_price, f"Итоговая цена ({visible_price}) не совпадает с суммой цен всех товаров ({total_price})"
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestPoryadokAddToFavorite:
    """Тесты авторизации на сайте Порядок"""

    @pytest.fixture
    def links(self):
        return ["https://poryadok.ru/catalog/musornye_pakety/2437139/", "https://poryadok.ru/catalog/shpateli/46009/", "https://poryadok.ru/catalog/kleykie_lenty/559769/"]

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

    def test_add_to_favorite(self, driver, links):

        product_names = []
        for link in links:
            driver.get(link)

            name_title = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CLASS_NAME, "detail-product-name"))
            )

            product_names.append(name_title.text)

            fav_btn = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.CLASS_NAME, "add-to-fav"))
            )

            fav_btn.click()

        svg_go_to_fav = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.XPATH, "//*[local-name()='svg' and contains(@class, 'pi-favorite-outline')]"))
        )
        svg_go_to_fav.click()

        WebDriverWait(driver, 10).until(
            EC.url_to_be("https://poryadok.ru/personal/favorite/")
        )

        product_names_elements = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.CLASS_NAME, "product-tile__title"))
        )

        present_names = list(map(lambda el: el.text, product_names_elements))
        added = True
        for name in product_names:
            if name not in present_names:
                added = False
                break

        assert added, "Не все товары были добавлены в избранное"

        fav_active_elements = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.XPATH, "//*[contains(@class, 'add-to-fav') and contains(@class, 'active')]"))
        )
        for element in fav_active_elements:
            element.click()

        driver.get("https://poryadok.ru/personal/favorite/")
        product_names_elements = driver.find_elements(By.CLASS_NAME, "product-tile__title")

        assert len(product_names_elements) == 0, "Не все товары были убраны из избранного"




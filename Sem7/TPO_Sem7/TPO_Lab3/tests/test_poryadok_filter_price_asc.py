import time

import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestPoryadokFilterPriceAsc:
    """Тесты авторизации на сайте Порядок"""

    @pytest.fixture
    def name(self):
        """Данные пользователя для тестов модуля"""
        return "Лопата"

    @pytest.fixture
    def min_price(self):
        """Данные пользователя для тестов модуля"""
        return 1000

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

    def test_filter_price_asc(self, driver, name, min_price):
        """Тест авторизации через email"""
        # Шаг 1: Открыть главную страницу
        driver.get("https://poryadok.ru")

        # Найти первое поле с классом XXX и ввести "Лопата"
        search_input = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "form-control"))
        )
        search_input.clear()
        search_input.send_keys("Лопата")
        search_input.send_keys(Keys.RETURN)

        # Шаг 2: Дождаться перехода на страницу поиска
        WebDriverWait(driver, 10).until(
            EC.url_contains("https://poryadok.ru/search/")
        )

        search_input = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "js-numeric-filter-min"))
        )
        search_input.clear()
        search_input.send_keys(str(min_price))
        search_input.send_keys(Keys.RETURN)

        filter_menu = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.CLASS_NAME, "sort-menu-collapsible__title"))
        )
        filter_menu.click()

        filter_menu = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.XPATH, "//li[contains(text(), 'Сначала дешёвые')]"))
        )
        filter_menu.click()

        time.sleep(0.5)

        # Шаг 3: Найти первые 10 элементов <a> с классом product-tile__title
        product_prices = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.CLASS_NAME, "product-price__price"))
        )

        # Ограничиваем первыми 20 элементами
        first_products = product_prices[:10]

        # Флаг для отслеживания наличия подстроки
        correct = True
        prices = list(map(lambda pt: int("".join(pt.text[:-2].split())), first_products))

        print(prices)

        # Проверить каждый элемент
        for i in range(len(first_products)):
            asc = True if i == 0 else prices[i] >= prices[i-1]
            more = prices[i] >= min_price
            # .text получает весь видимый текст, включая вложенные элементы
            if not (asc and more):
                correct = False
                break

        assert correct, f"Товары не отсортированы либо дешевле минимальной цены"
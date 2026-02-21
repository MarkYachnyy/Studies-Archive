import time

import pytest
from selenium import webdriver
from selenium.webdriver import ActionChains
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestPoryadokLogin:

    @pytest.fixture
    def category(self):
        """Данные пользователя для тестов модуля"""
        return "Бытовая техника"

    @pytest.fixture
    def subcategory(self):
        """Данные пользователя для тестов модуля"""
        return "Пылесосы"

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

    def test_filter_category(self, driver, category, subcategory):
        """Тест авторизации через email"""
        # Шаг 1: Открыть главную страницу
        driver.get("https://poryadok.ru")

        # Найти первое поле с классом XXX и ввести "Лопата"
        dropdown = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "prk-top-menu__item--main"))
        )
        dropdown.click()

        time.sleep(1)

        category_link = driver.find_element(By.XPATH, f"//*[contains(@class, 'prk-top-menu__item-title') and contains(text(), '{category}')]")

        actions = ActionChains(driver)
        actions.move_to_element(category_link).perform()
        time.sleep(0.5)
        category_link.click()

        WebDriverWait(driver, 10).until(
            EC.url_contains("https://poryadok.ru/catalog/")
        )

        subcat = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, f"//*[local-name()='span' and contains(text(), '{subcategory}')]"))
        )
        subcat.click()

        tile = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable(
                (By.CLASS_NAME, "product-tile"))
        )
        tile.click()

        category_nav = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "bx-breadcrumb"))
        )

        assert subcategory in category_nav.text, "Категория не совпадает"
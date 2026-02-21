import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestPoryadokNameSearch:
    """Тесты авторизации на сайте Порядок"""

    @pytest.fixture
    def name(self):
        """Данные пользователя для тестов модуля"""
        return "Лопата"

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

    def test_name_search(self, driver, name):
        """Тест авторизации через email"""
        # Шаг 1: Открыть главную страницу
        driver.get("https://poryadok.ru")

        # Найти первое поле с классом XXX и ввести "Лопата"
        search_input = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "form-control"))
        )
        search_input.clear()
        search_input.send_keys(name)
        search_input.send_keys(Keys.RETURN)

        # Шаг 2: Дождаться перехода на страницу поиска
        WebDriverWait(driver, 10).until(
            EC.url_contains("https://poryadok.ru/search/")
        )

        # Дополнительная проверка, что URL действительно содержит search
        current_url = driver.current_url
        assert "search" in current_url, f"Ожидался URL с /search/, получен: {current_url}"

        print(f"✓ Переход выполнен. Текущий URL: {current_url}")

        # Шаг 3: Найти первые 10 элементов <a> с классом product-tile__title
        product_links = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.CLASS_NAME, "product-tile__title"))
        )

        # Ограничиваем первыми 10 элементами
        first_ten_products = product_links[:10]

        # Проверяем, что найдено достаточно элементов
        assert len(first_ten_products) > 0, "Не найдено ни одного товара в результатах"

        print(f"✓ Найдено {len(first_ten_products)} товаров")

        # Флаг для отслеживания наличия подстроки
        found = True

        # Проверить каждый элемент
        for title in first_ten_products:
            # .text получает весь видимый текст, включая вложенные элементы
            title_text = title.text
            if name not in title_text:
                found = False
                print(f"Не найдено: {title_text}")
                break

        assert found, f"Подстрока '{name}' не найдена в одном из первых 20 элементов"

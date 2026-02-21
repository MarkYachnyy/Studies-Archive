import pytest
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time


class TestPoryadokLogout:
    """Тесты авторизации на сайте Порядок"""

    @pytest.fixture(scope="session")
    def credentials(self):
        """Данные пользователя для тестов модуля"""
        return {
            "password": "EXAMPLE",
            "email": "EXAMPLE@gmail.com"
        }

    @pytest.fixture(scope="function")
    def driver(self):
        chrome_options = Options()
        # Метод 1: Использование аргумента
        chrome_options.add_argument("--disable-notifications")
        # Метод 2: Настройка через preferences (более надежный)
        prefs = {
            "profile.default_content_setting_values.notifications": 2
        }
        chrome_options.add_experimental_option("prefs", prefs)
        driver = webdriver.Chrome(options=chrome_options)
        driver.implicitly_wait(10)
        yield driver
        driver.quit()

    @pytest.fixture
    def login_with_email(self, driver, credentials):
        """Тест авторизации через email"""
        # Шаг 1: Открыть главную страницу
        driver.get("https://www.poryadok.ru")

        time.sleep(0.5)

        svg_close = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.XPATH, "//*[local-name()='svg' and contains(@class, 'pi-close')]"))
        )
        svg_close.click()

        # Шаг 2: Нажать на кнопку профиля
        profile_button = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.CLASS_NAME, "profile"))
        )
        profile_button.click()

        # Шаг 3: Дождаться загрузки страницы логина и проверить URL
        WebDriverWait(driver, 10).until(
            EC.url_to_be("https://poryadok.ru/login/")
        )

        # Шаг 4: Нажать на ссылку "Войти по email"
        email_link = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.LINK_TEXT, "Войти по email"))
        )
        email_link.click()

        # Шаг 5: Ввести email в поле с классом "email-input"
        email_field = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "email-input"))
        )
        email_field.clear()
        email_field.send_keys(credentials["email"])
        email_field.send_keys(Keys.RETURN)

        # Шаг 6: Ввести пароль в поле с type="password"
        password_field = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "input[type='password']"))
        )
        password_field.clear()
        password_field.send_keys(credentials["password"])
        password_field.send_keys(Keys.RETURN)

        WebDriverWait(driver, 10).until(
            EC.url_to_be("https://poryadok.ru/personal/")
        )

        email_input = driver.find_element(By.ID, "lk_email")
        assert email_input.get_attribute('value') == credentials["email"]
        print("Успешная авторизация!")

        return credentials["email"]

    def test_logout(self, driver, login_with_email):

        driver.get("https://www.poryadok.ru")

        logout_button = WebDriverWait(driver, 10).until(
            EC.element_to_be_clickable((By.CLASS_NAME, "profile-icon-out"))
        )
        logout_button.click()

        try:
            _ = driver.find_element(By.CLASS_NAME, "profile")
            assert True  # Элемент найден
        except NoSuchElementException:
            assert False, "Кнопка с классом 'submit-button' не найдена"


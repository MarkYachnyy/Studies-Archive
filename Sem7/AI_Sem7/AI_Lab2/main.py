import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from statsmodels.tsa.seasonal import seasonal_decompose
import warnings
import datetime

warnings.filterwarnings('ignore')

# Чтение данных и фильтрация
store = 0
product = 3
df = pd.read_csv('train.csv', parse_dates=['date'])
df_filtered = df[(df['store'] == store) & (df['product'] == product)]
df_filtered[['date', 'sold']].to_csv('train_new.csv', index=False)

# Загрузка отфильтрованных данных
data = pd.read_csv('train_new.csv', parse_dates=['date'])
data.rename(columns={'date': 'Date', 'sales': 'sold'}, inplace=True)
data.set_index('Date', inplace=True)

# Декомпозиция временного ряда
decomposition = seasonal_decompose(data['sold'], model='additive', period=365)

# Расчет отклонений от тренда + сезонность
trend_seasonal = decomposition.trend + decomposition.seasonal
residuals = data['sold'] - trend_seasonal

# Создание графиков
fig, axes = plt.subplots(4, 1, figsize=(15, 10))
fig.suptitle('Анализ временного ряда продаж', fontsize=16)

# Исходный ряд
axes[0].plot(data.index, data['sold'])
axes[0].set_title('1. Исходный ряд')
axes[0].legend()
axes[0].grid(True, alpha=0.3)

# Тренд
trend_data = decomposition.trend.dropna()
axes[1].plot(trend_data.index, trend_data, color='green')
axes[1].set_title('2. Тренд')
axes[1].grid(True, alpha=0.3)

# Сезонность
seasonal_data = decomposition.seasonal.dropna()
axes[2].plot(seasonal_data.index, seasonal_data, color='green')
axes[2].set_title('3. Сезонность')
axes[2].grid(True, alpha=0.3)

# Выбросы
axes[3].plot(residuals.index, residuals)
axes[3].set_title('4. Выбросы')
axes[3].legend()
axes[3].grid(True, alpha=0.3)

plt.tight_layout()
plt.show()

############################################
#             ПРОГНОЗИРОВАНИЕ              #
############################################

from sklearn.preprocessing import PolynomialFeatures
from sklearn.linear_model import LinearRegression

# Подготовка данных для полиномиальной регрессии тренда
days_to_cut = 365 // 2
X_train = np.arange(len(trend_data)).reshape(-1, 1)
y_train = trend_data.values

# Обучение полиномиальной регрессии 3 степени
poly = PolynomialFeatures(degree=3)
X_poly = poly.fit_transform(X_train)
model = LinearRegression()
model.fit(X_poly, y_train)

# Прогноз тренда на 2019 год + вторую половину 2018
X_future = np.arange(len(trend_data), len(trend_data) + 365 + days_to_cut).reshape(-1, 1)
X_future_poly = poly.transform(X_future)
trend_forecast = model.predict(X_future_poly)

# Подготовка сезонной компоненты (берем последний год сезонности)
last_seasonal = decomposition.seasonal[-365:]

# # Создание датаиндекса для прогноза
forecast_dates = pd.date_range(
    start=(datetime.datetime.strptime('2019-01-01', '%Y-%m-%d') - datetime.timedelta(days=days_to_cut)).strftime(
        '%Y-%m-%d'), end='2019-12-31', freq='D')

# Полный прогноз из всех трёх составляющих
residual_std = residuals.std()
np.random.seed(42)
noise = np.random.normal(0, residual_std, 365)
forecast = trend_forecast[days_to_cut:] + last_seasonal + noise

# Чтение тестовых данных для сравнения
test_df = pd.read_csv('test.csv', parse_dates=['date'])
test_filtered = test_df[(test_df['store'] == store) & (test_df['product'] == product)]
test_data = test_filtered[['date', 'sold']].copy()
test_data.rename(columns={'date': 'Date', 'sold': 'sold'}, inplace=True)
test_data.set_index('Date', inplace=True)

# Построение графиков прогноза
fig, axes = plt.subplots(1, 3, figsize=(15, 5))
fig.suptitle('Предсказание временного ряда на 2019 год', fontsize=16)

# График прогноза тренда
axes[0].plot(trend_data.index, trend_data, label='Фактический тренд')
axes[0].plot(forecast_dates, trend_forecast, label='Предсказанный тренд')
axes[0].set_title('1. Тренд и его предсказание')
axes[0].legend()
axes[0].grid(True, alpha=0.3)

#
axes[1].plot(data.index, data, label='Данные до 2019')
axes[1].plot(forecast_dates[days_to_cut:], forecast, label='Предсказанные данные за 2019', alpha=0.5, color='orange')
axes[1].plot(test_data.index, test_data, label='Настоящие данные за 2019', alpha=0.5, color='green')
axes[1].set_title('2. Предсказание с сезонностью и шумом')
axes[1].legend()
axes[1].grid(True, alpha=0.3)


axes[2].plot(forecast_dates[days_to_cut:], forecast, label='Предсказанные данные за 2019', color='orange')
axes[2].plot(test_data.index, test_data, label='Настоящие данные за 2019', color='green')
axes[2].set_title('3. Предсказанные данные отдельно')
axes[2].legend()
axes[2].grid(True, alpha=0.3)

plt.tight_layout()

plt.show()

mae = np.abs(test_data['sold'].values - forecast.values).mean()
rmse = np.pow(test_data['sold'].values - forecast.values, 2).mean()**0.5

print('Средняя абсолютная ошибка предсказания (MAE):', mae, f'({(mae / forecast.mean()*100):.2f}% от мат.ожидания)')
print('Среднеквадратичная ошибка предсказания (RMSE):', rmse, f'({(rmse / forecast.mean()*100):.2f}% от мат.ожидания)')
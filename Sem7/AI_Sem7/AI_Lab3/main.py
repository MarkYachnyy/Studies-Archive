import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score, classification_report
import matplotlib.pyplot as plt

# Загрузка данных
data = pd.read_csv('dataset.csv')

# Разделение на признаки и целевую переменную
X = data.drop('price_range', axis=1)
y = data['price_range']

# Разделение на тренировочную+валидационную (80%) и тестовую (20%) выборки
X_temp, X_test, y_temp, y_test = train_test_split(X, y, test_size=0.2,
                                                  random_state=42, stratify=y)

# Разделение на тренировочную (60%) и валидационную (20%) выборки
X_train, X_val, y_train, y_val = train_test_split(X_temp, y_temp, test_size=0.25,
                                                  random_state=42, stratify=y_temp)

print(f"Размеры выборок:")
print(f"Тренировочная: {X_train.shape[0]} образцов")
print(f"Валидационная: {X_val.shape[0]} образцов")
print(f"Тестовая: {X_test.shape[0]} образцов")

# Масштабирование признаков
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_val_scaled = scaler.transform(X_val)
X_test_scaled = scaler.transform(X_test)

# Подбор оптимального k
best_k = 1
best_accuracy = 0
accuracy_scores = []

# Перебираем значения k от 1 до 30
k_range = range(1, 31)

for k in k_range:
    # Создаем и обучаем модель
    knn = KNeighborsClassifier(n_neighbors=k)
    knn.fit(X_train_scaled, y_train)

    # Предсказание на валидационной выборке
    y_val_pred = knn.predict(X_val_scaled)

    # Оценка точности
    accuracy = accuracy_score(y_val, y_val_pred)
    accuracy_scores.append(accuracy)

    # Обновляем лучший результат
    if accuracy > best_accuracy:
        best_accuracy = accuracy
        best_k = k

print(f"\nОптимальное значение k: {best_k}")
print(f"Лучшая точность на валидационной выборке: {best_accuracy:.4f}")

# Визуализация зависимости точности от k
plt.figure(figsize=(12, 6))
plt.plot(k_range, accuracy_scores, marker='o', linestyle='-', linewidth=2)
plt.axvline(x=best_k, color='red', linestyle='--', alpha=0.7, label=f'Лучшее k={best_k}')
plt.xlabel('Значение k')
plt.ylabel('Точность')
plt.title('Зависимость точности KNN от значения k')
plt.grid(True, alpha=0.3)
plt.legend()
plt.xticks(k_range)
plt.tight_layout()
plt.show()

# Обучение финальной модели с оптимальным k на объединенных тренировочных данных
print(f"\n--- Финальное тестирование с k={best_k} ---")

# Объединяем тренировочную и валидационную выборки для финального обучения
X_final_train = np.vstack([X_train_scaled, X_val_scaled])
y_final_train = np.concatenate([y_train, y_val])

# Создаем и обучаем финальную модель
final_knn = KNeighborsClassifier(n_neighbors=best_k)
final_knn.fit(X_final_train, y_final_train)

# Предсказание на тестовой выборке
y_test_pred = final_knn.predict(X_test_scaled)

# Оценка точности на тестовой выборке
test_accuracy = accuracy_score(y_test, y_test_pred)
print(f"Точность на тестовой выборке: {test_accuracy:.4f}")

# Матрица ошибок
from sklearn.metrics import confusion_matrix
import seaborn as sns

cm = confusion_matrix(y_test, y_test_pred)
plt.figure(figsize=(8, 6))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues')
plt.title('Матрица ошибок KNN классификатора')
plt.ylabel('Истинные значения')
plt.xlabel('Предсказанные значения')
plt.show()
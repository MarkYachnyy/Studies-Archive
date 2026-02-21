import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans
from scipy.cluster.hierarchy import dendrogram, linkage, fcluster
from sklearn.metrics import silhouette_score
import warnings
warnings.filterwarnings('ignore')

# Чтение данных
df = pd.read_csv('data.csv')
print("Размер данных:", df.shape)
numeric_columns = ['Age', 'Annual Income', 'Spending Score']
X = df[numeric_columns].copy()

# Стандартизация данных
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Иерархическая кластеризация с расстоянием Уорда
plt.figure(figsize=(12, 5))
plt.subplot(1, 2, 1)
Z = linkage(X_scaled, method='ward')

# Построение дендрограммы
dendrogram(Z, truncate_mode='lastp', p=24, show_leaf_counts=True)
plt.title(f'Дендрограмма (Ward linkage)')
plt.xlabel('Образцы')
plt.ylabel('Расстояние')
plt.tight_layout()

# Метод локтя для K-means
inertia = []
k_range = range(2, 13)

for k in k_range:
    kmeans = KMeans(n_clusters=k, random_state=42, n_init=10)
    kmeans.fit(X_scaled)
    inertia.append(kmeans.inertia_)

plt.subplot(1, 2, 2)
plt.plot(k_range, inertia, 'r-', marker='o')
plt.xlabel('Количество кластеров')
plt.ylabel('Сумма квадратов внутри кластера')
plt.title('Метод локтя (K-means)')
plt.grid(True)

plt.tight_layout()
plt.show()

# Анализ силуэтов для определения оптимального k
silhouette_scores = []
for k in range(2, 11):
    kmeans = KMeans(n_clusters=k, random_state=42, n_init=10)
    cluster_labels = kmeans.fit_predict(X_scaled)
    silhouette_avg = silhouette_score(X_scaled, cluster_labels)
    silhouette_scores.append(silhouette_avg)

plt.figure(figsize=(12, 5))
plt.plot(range(2, 11), silhouette_scores, 'g-', marker='o')
plt.xlabel('Количество кластеров')
plt.ylabel('Средний коэффициент силуэта')
plt.title('Анализ силуэта для определения оптимального k')
plt.grid(True)
plt.show()

# Выбор оптимального числа кластеров
optimal_k = np.argmax(silhouette_scores) + 2  # +2 потому что начинаем с 2
print(f"Оптимальное число кластеров: {optimal_k}")

# Иерархическая кластеризация с оптимальным k
hierarchical_clusters = fcluster(Z, optimal_k, criterion='maxclust')
df['hierarchical_cluster'] = hierarchical_clusters

# Многомерное шкалирование для визуализации
from sklearn.manifold import MDS
mds = MDS(n_components=2, random_state=42, dissimilarity='euclidean')
X_mds = mds.fit_transform(X_scaled)

# K-means кластеризация
kmeans = KMeans(n_clusters=optimal_k, random_state=42, n_init=10)
kmeans_clusters = kmeans.fit_predict(X_scaled)
df['kmeans_cluster'] = kmeans_clusters + 1  # для совпадения нумерации

plt.figure(figsize=(8, 5))
plt.subplot(1, 2, 1)
for cluster in range(1, optimal_k + 1):
    cluster_points = X_mds[df['hierarchical_cluster'] == cluster]
    plt.scatter(cluster_points[:, 0], cluster_points[:, 1],
                label=f'Кластер {cluster}', alpha=0.7)
plt.title('Иерархическая кластеризация (MDS)')
plt.legend()

plt.subplot(1, 2, 2)
for cluster in range(1, optimal_k + 1):
    cluster_points = X_mds[df['kmeans_cluster'] == cluster]
    plt.scatter(cluster_points[:, 0], cluster_points[:, 1],
                label=f'Кластер {cluster}', alpha=0.7)
plt.title('K-means кластеризация (MDS)')
plt.legend()

plt.tight_layout()
plt.show()

cluster_profiles = df.groupby('kmeans_cluster')[numeric_columns].mean()


# Визуализация профилей кластеров
# plt.figure()
cluster_profiles.T.plot(kind='bar', figsize=(12, 6))
plt.title('Профили кластеров по переменным')
plt.ylabel('Стандартизированные значения')
plt.xlabel('Переменные')
plt.legend(title='Кластеры')
plt.xticks(rotation=45)
plt.grid(True, alpha=0.3)
plt.show()

# Попарные сравнения переменных с раскраской по кластерам
sns.pairplot(df, vars=numeric_columns, hue='kmeans_cluster',
             palette='viridis', diag_kind='kde')
plt.suptitle('Попарные отношения переменных по кластерам', y=1.02)
plt.show()

# Проверка стабильности кластеризации
from sklearn.metrics import adjusted_rand_score

# Сравнение двух методов кластеризации
ari_score = adjusted_rand_score(df['hierarchical_cluster'], df['kmeans_cluster'])
print(f"Сходство между методами кластеризации (ARI): {ari_score:.3f}")

# Анализ важности переменных для разделения кластеров
from sklearn.ensemble import RandomForestClassifier

# Обучение модели для определения важности переменных
rf = RandomForestClassifier(random_state=42)
rf.fit(X_scaled, kmeans_clusters)

feature_importance = pd.DataFrame({
    'variable': numeric_columns,
    'importance': rf.feature_importances_
}).sort_values('importance', ascending=False)

print("\nВАЖНОСТЬ ПЕРЕМЕННЫХ ДЛЯ РАЗДЕЛЕНИЯ КЛАСТЕРОВ:")
print(feature_importance)

# Визуализация важности переменных
plt.figure()
plt.barh(feature_importance['variable'], feature_importance['importance'])
plt.xlabel('Важность')
plt.title('Важность переменных для разделения кластеров')
plt.gca().invert_yaxis()
plt.show()
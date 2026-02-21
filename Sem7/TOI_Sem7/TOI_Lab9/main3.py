import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import adjusted_rand_score  # внешняя метрика качества

# ===== kmeans_custom без Callable =====

def kmeans_custom(
    X: np.ndarray,
    n_clusters: int,
    distance_func,
    max_iter: int = 300,
    tol: float = 1e-4,
    random_state: int = 0
):
    np.random.seed(random_state)
    N, n_features = X.shape

    # Инициализация центров случайными точками из выборки
    idx = np.random.choice(N, n_clusters, replace=False)
    centers = X[idx].copy()
    prev_inertia = np.inf

    for it in range(max_iter):
        # 1. Назначаем кластеры
        distances = np.array([
            np.array([distance_func(x, c) for c in centers])
            for x in X
        ])
        labels = np.argmin(distances, axis=1)

        # 2. Обновляем центры
        new_centers = np.zeros_like(centers)
        for k in range(n_clusters):
            cluster_points = X[labels == k]
            if len(cluster_points) > 0:
                new_centers[k] = _compute_center(cluster_points, centers[k], distance_func)
            else:
                new_centers[k] = centers[k]

        # 3. Инерция (сумма расстояний до центров)
        inertia = 0.0
        for i in range(N):
            inertia += distance_func(X[i], new_centers[labels[i]])

        if abs(prev_inertia - inertia) < tol:
            print(f"Сходимость на итерации {it+1}")
            break

        prev_inertia = inertia
        centers = new_centers

    else:
        print(f"Достигнуто max_iter={max_iter}")

    return labels, centers, inertia, it + 1


def _compute_center(points: np.ndarray, init_center: np.ndarray, distance_func):
    """Поиск центра как точки, минимизирующей сумму расстояний (численный градиент)."""
    center = init_center.copy()
    n_features = center.shape[0]

    for _ in range(10):  # несколько шагов градиентного спуска
        gradients = np.zeros(n_features)
        n_points = len(points)

        for i in range(n_points):
            grad = _gradient_distance(center, points[i], distance_func)
            gradients += grad

        step_size = 0.1 / (1 + np.linalg.norm(gradients))
        center -= step_size * gradients / max(n_points, 1)

    return center


def _gradient_distance(c: np.ndarray, x: np.ndarray, distance_func):
    """Численный градиент расстояния d(c, x) по c."""
    eps = 1e-6
    grad = np.zeros_like(c)
    base = distance_func(c, x)
    for i in range(len(c)):
        c_plus = c.copy()
        c_plus[i] += eps
        grad[i] = (distance_func(c_plus, x) - base) / eps
    return grad

# ===== Метрики расстояния =====

def euclidean(x: np.ndarray, y: np.ndarray) -> float:
    return np.linalg.norm(x - y)

def sq_euclidean(x: np.ndarray, y: np.ndarray) -> float:
    return np.sum((x - y) ** 2)

def cityblock(x: np.ndarray, y: np.ndarray) -> float:
    return np.sum(np.abs(x - y))

def cosine_distance(x: np.ndarray, y: np.ndarray) -> float:
    dot = np.dot(x, y)
    nx = np.linalg.norm(x)
    ny = np.linalg.norm(y)
    if nx == 0 or ny == 0:
        return 1.0
    cos_sim = dot / (nx * ny)
    return 1.0 - cos_sim

def correlation_distance(x: np.ndarray, y: np.ndarray) -> float:
    x_c = x - np.mean(x)
    y_c = y - np.mean(y)
    nx = np.linalg.norm(x_c)
    ny = np.linalg.norm(y_c)
    if nx == 0 or ny == 0:
        return 1.0
    corr = np.dot(x_c, y_c) / (nx * ny)
    return 1.0 - corr

# ===== Генерация данных: 3 класса в 2D =====

np.random.seed(42)
n_classes = 2
n_per_class = 100
N = n_classes * n_per_class

means = np.array([
    [0.0, 0.0],
    [0.0, 0.0],
])

X_list = []
y_list = []


for k in range(n_classes):
    Xk = np.random.multivariate_normal(means[k], C[:, :, k], size=n_per_class)
    yk = np.full(n_per_class, k, dtype=int)
    X_list.append(Xk)
    y_list.append(yk)

X = np.vstack(X_list)   # (N, 2)
y_true = np.hstack(y_list)  # (N,)

# ===== Запуск k-means с разными метриками и выбор лучшей =====

metrics = {
    'sqeuclidean': sq_euclidean,
    'cityblock': cityblock,
    'cosine': cosine_distance,
    'correlation': correlation_distance
}

results = {}

for name, dist_func in metrics.items():
    print(f"\n=== Метрика: {name} ===")
    labels, centers, inertia, n_iter = kmeans_custom(
        X,
        n_clusters=n_classes,
        distance_func=dist_func,
        max_iter=100,
        tol=1e-6,
        random_state=0
    )
    ari = adjusted_rand_score(y_true, labels)
    print(f"Инерция: {inertia:.3f}, итераций: {n_iter}, ARI: {ari:.4f}")
    results[name] = {
        'labels': labels,
        'centers': centers,
        'inertia': inertia,
        'ari': ari,
        'n_iter': n_iter
    }

# Выбор лучшей метрики по ARI (чем выше, тем ближе к истинным классам)
best_name = max(results.keys(), key=lambda k: results[k]['ari'])
best_res = results[best_name]

print(f"\nЛучшая метрика по ARI: {best_name}, ARI = {best_res['ari']:.4f}")

# ===== Визуализация =====

colors = ['r', 'g', 'b']

plt.figure(figsize=(10, 4))

# 1) Исходные метки
for k in range(n_classes):
    plt.scatter(
        X[y_true == k, 0],
        X[y_true == k, 1],
        c=colors[k],
        label=f"class {k}",
        alpha=0.7,
        s=100
    )
plt.title("Исходные классы и кластеры (метрика: {best_name}, ARI={best_res['ari']:.3f})")

# 2) Метки после кластеризации с лучшей метрикой
labels_best = best_res['labels']
centers_best = best_res['centers']

colors = ['cyan', 'magenta', 'yellow']

for k in range(n_classes):
    plt.scatter(
        X[labels_best == k, 0],
        X[labels_best == k, 1],
        c=colors[k],
        label=f"cluster {k}",
        s=10
    )
plt.scatter(
    centers_best[:, 0],
    centers_best[:, 1],
    c='k',
    marker='x',
    s=100,
    linewidths=3,
    label='centers'
)

plt.legend()
plt.grid(True)

plt.tight_layout()
plt.show()

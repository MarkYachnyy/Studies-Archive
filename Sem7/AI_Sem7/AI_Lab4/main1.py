# импорт пакетов
import pandas as pd
import numpy as np
import seaborn as sns
from scipy import stats
import matplotlib.pyplot as plt
import matplotlib

plt.style.use('ggplot')
matplotlib.rcParams['figure.figsize'] = (12,8)
pd.options.mode.chained_assignment = None

# чтениеданных
df = pd.read_csv('data.csv').iloc[27000:28000]
print(df.shape)

missing_cols = []
missing_pcts = []
for col in df.columns:
    pct_missing = np.mean(df[col].isnull())
    percent = round(pct_missing*100)
    if(percent > 0):
        missing_pcts.append(percent)
        missing_cols.append(col)

plt.figure(figsize=(12,8))
plt.bar(missing_cols, missing_pcts)
plt.title('Пропуски по столбцам')
plt.ylabel('Процент пропусков')
plt.xticks(rotation=90)  # Поворот подписей для лучшей читаемости
plt.tight_layout()  # Автоматическая подгонка layout

# сначала создаем индикатор для признаков с пропущенными данными
for col in df.columns:
    missing = df[col].isnull()
    num_missing = np.sum(missing)

    if num_missing > 0:
        df['{}_ismissing'.format(col)] = missing

# затем на основе индикатора строим гистограмму
ismissing_cols = [col for col in df.columns if 'ismissing' in col]
df['num_missing'] = df[ismissing_cols].sum(axis=1)
df['num_missing'].value_counts().reset_index().sort_values(by='num_missing').plot.bar(x='num_missing', y='count', title="Количество записей по пропуску полей")

# отбрасываем строки с большим количеством пропусков
ind_missing = df[df['num_missing'] > 35].index
df= df.drop(ind_missing, axis=0)
cols_to_drop = ['hospital_beds_raion', 'cafe_sum_500_min_price_avg', 'cafe_sum_500_max_price_avg','cafe_avg_price_500']
df = df.drop(cols_to_drop, axis=1)

# Определение численных столбцов
df_numeric = df.select_dtypes(include=[np.number])
numeric_cols = df_numeric.columns.values

# Заполнение пропусков медианами
for col in numeric_cols:
    missing = df[col].isnull()
    num_missing = np.sum(missing)

    if num_missing > 0:
        df['{}_ismissing'.format(col)] = missing
        med = df[col].median()
        df[col] = df[col].fillna(med)

df.drop(ismissing_cols, axis=1)
df.drop(['num_missing'], axis=1)

# Удаление числовых записей с z-индексом больше 8
sums = []
outlier_cols = []
for col in numeric_cols:

    z_scores = np.abs(stats.zscore(df[col]))
    outliers = z_scores > 8

    s = np.sum(outliers)
    if(s > 0):
        sums.append(s)
        outlier_cols.append(col)

        median_value = df[col].median()
        df[col] = np.where(outliers, median_value, df[col])

plt.figure(figsize=(12, 8))
plt.bar(outlier_cols, sums)
plt.title('Статистика по выбросам')
plt.ylabel('Количество выбросов')
plt.xticks(rotation=90)  # Поворот подписей для лучшей читаемости
plt.tight_layout()  # Автоматическая подгонка layout

num_rows = len(df.index)
low_information_cols = [] #

pcts = []
for col in df.columns:
    cnts = df[col].value_counts(dropna=False)
    top_pct = (cnts/num_rows).iloc[0]

    if top_pct > 0.95:
        pcts.append(top_pct*100)
        low_information_cols.append(col)

plt.figure(figsize=(12,8))
plt.bar(low_information_cols, pcts)
plt.title('Малоинформативные стобцы')
plt.ylabel('Процент совпадающих значений')
plt.xticks(rotation=90)  # Поворот подписей для лучшей читаемости
plt.tight_layout()  # Автоматическая подгонка layout

df = df.drop(low_information_cols, axis=1)

key = ['timestamp', 'full_sq', 'life_sq', 'floor', 'build_year', 'num_room', 'price_doc']
print("Форма до удаления дупликатов: ", df.shape)
df_dedupped2 = df.drop_duplicates(subset=key)
print("Форма после удаления дупликатов: ", df_dedupped2.shape)
df = df_dedupped2

df['sub_area_lower'] = df['sub_area'].str.lower()

df['timestamp_dt'] = pd.to_datetime(df['timestamp'], format='%Y-%m-%d')
df['year'] = df['timestamp_dt'].dt.year
df['month'] = df['timestamp_dt'].dt.month
df['weekday'] = df['timestamp_dt'].dt.weekday

plt.show()
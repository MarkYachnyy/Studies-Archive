clear;
factor_number = 2;
min_factor_value = [1.25, 200];    % [min_enter_intensity, min_queue_capacity]
max_factor_value = [1.75, 600];   % [max_enter_intensity, max_queue_capacity]

% Формирование пустого плана эксперимента
N = 2^factor_number;
frac_plan = fracfact('a b ab');
fict_fact = ones(N,1);
X = [fict_fact, frac_plan]';
frac_experimental = zeros(N, factor_number);

% заполнение плана эксперимента
for i = 1:factor_number
    for j = 1:N
        frac_experimental(j,i) = min_factor_value(i) + (frac_plan(j,i)+1)*(max_factor_value(i)-min_factor_value(i))/2;
    end
end

% вывод плана эксперимента
disp('План эксперимента:');
disp(frac_experimental);

% параметры доверительного интервала
dm = 0.05;      % точность интервала
alpha = 0.08;   % уровень значимости
t_krit_alpha = norminv(1-alpha/2); % t-критическое

% инициализация массивов для результатов
Y_ServInt = zeros(1,N); % интенсивность обслуживания 
Y_AvgWait = zeros(1,N); % среднее время ожидания в очереди

% цикл по экспериментам
for j = 1:N
    
    current_mean_intensity = frac_experimental(j,1); % интенсивность потока заявок
    current_queue_capacity = round(frac_experimental(j,2)); % макс. длина очереди
    
    % Начальные значения статистики
    NE = 1;
    l_ServInt = 0;
    l_AvgWait = 0;
    SQ_ServInt = 0;
    SQ_AvgWait = 0;
    D_ServInt = 1;
    D_AvgWait = 1;
    
    while NE < t_krit_alpha^2 * max(D_ServInt, D_AvgWait) / dm^2
        % Симуляция модели
        queue_capacity = current_queue_capacity;
        entity_gen_period = 1.0 / current_mean_intensity;
        mod_time = 7800;
        sim('metro_queue', mod_time);
        
        % получение результатов симуляции
        served = processed_requests(end);  
        ServInt_current = served / mod_time;
        wait_current = average_wait(end);
        
        % Накопление статистики
        l_ServInt = l_ServInt + ServInt_current;
        l_AvgWait = l_AvgWait + wait_current;
        SQ_ServInt = SQ_ServInt + ServInt_current^2;
        SQ_AvgWait = SQ_AvgWait + wait_current^2;
        
        % Расчет дисперсии
        if NE > 20 % Предохранитель от преждевременной остановки
            D_ServInt = SQ_ServInt/(NE-1) - (l_ServInt^2)/(NE*(NE-1));
            D_AvgWait = SQ_AvgWait/(NE-1) - (l_AvgWait^2)/(NE*(NE-1));
        end
        
        NE = NE + 1;
    end
    
    NE = NE - 1;
    
    % Усредненные результаты
    Y_ServInt(j) = l_ServInt / NE;
    Y_AvgWait(j) = l_AvgWait / NE;
end

% подбор коэффициентов регрессии
invCl = inv(X*X');
b_ServInt = invCl * X * Y_ServInt';
b_AvgWait = invCl * X * Y_AvgWait';

A1 = min_factor_value(1):0.025:max_factor_value(1);
B1 = min_factor_value(2):25:max_factor_value(2);

N1 = length(A1);
N2 = length(B1);

Yo_ServInt = zeros(N2, N1);
Yo_AvgWait = zeros(N2, N1);

for i = 1:N1
    for j = 1:N2
        anl = 2*(A1(i)-min_factor_value(1))/(max_factor_value(1)-min_factor_value(1))-1;
        bnl = 2*(B1(j)-min_factor_value(2))/(max_factor_value(2)-min_factor_value(2))-1;
        
        Yo_ServInt(j,i) = b_ServInt(1) + anl*b_ServInt(2) + bnl*b_ServInt(3) + anl*bnl*b_ServInt(4);
        Yo_AvgWait(j,i) = b_AvgWait(1) + anl*b_AvgWait(2) + bnl*b_AvgWait(3) + anl*bnl*b_AvgWait(4);
    end
end

[xl, yl] = meshgrid(A1, B1);

% постройка графиков
figure;
subplot(1,2,1);
surf(xl, yl, Yo_ServInt);
xlabel('Интенсивность входного потока');
ylabel('Макс. ёмкость очереди');
zlabel('Интенсивность обслуживания ServInt');
title('Поверхность реакции ServInt');
grid off;

subplot(1,2,2);
surf(xl, yl, Yo_AvgWait);
xlabel('Интенсивность входного потока');
ylabel('Макс. ёмкость очереди');
zlabel('Среднее время ожидания в очереди');
title('Поверхность реакции AvgWait');
grid off;
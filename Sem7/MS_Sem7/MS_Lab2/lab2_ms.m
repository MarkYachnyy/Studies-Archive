% 2. Оценка L в зависимости от NP и mr при фиксированных Am=6 и mr=7.96
%Задание неварьируемых величин
%амплитуды сигнала (Am) и расстояния (R)
clear all;
Ts=0.001; 
Ns=10000; 
Am=6;
mr=7.96;
Fc=300;
%Задание количества и диапазонов изменения факторов NP (a) и R (b)
nf=2;
minf=[0.04 50];
maxf=[0.11 96];
%формирование дробного двухуровневого плана эксперимента
%для учета взаимодействий
fracfact('a b ab' );
N=2^nf;
fracplan=ans;
fictfact=ones(N,1);
X=[fictfact ans]';
fraceks=zeros(N,nf);
for i=1:nf
    for j=1:N
        fraceks(j,i)=minf(i)+(fracplan(j,i)+1)*(maxf(i)-minf(i))/2;
    end
end
%тактическое планирование эксперимента
%задание доверительного интервала и уровня значимости
dm=0.05;
alpha=0.08;
%определение t-критического
tkr_alpha=norminv(1-alpha/2);
%цикл по совокупности экспериментов стратегического плана
for j=1:N
    a=fraceks(j,1);
    b=fraceks(j,2);
    NP=a;
    R=b;
    fprintf("NP = %d; R = %d; ", NP, R);
    %организация цикла статистических испытаний с переменным объемом
    %выборки для достижения заданной точности оценки показателя
    NE=1;
    l=0;
    SQ=0;
    D=1;
    while NE < tkr_alpha^2*D/dm^2
    %имитация функционирования системы
        to=randseed; %round(rand*100); %инициализация генератора шума
        sim('simulink_lab_2',Ts*Ns);
        u=sum(simout1)/(Ts*Ns);
        %Оценка выборочной дисперсии D измеряемого параметра
        l=l+u;
        SQ=SQ+u^2;
        if NE>20 D=SQ/(NE-1)-(l^2)/(NE*(NE-1)); end
        NE=NE+1;
    end
    NE=NE-1;
    %оценка показателя (реакции) по выборке наблюдений
    L=l/NE;
    fprintf("NE = %d; L = %d;\n", NE, L);
    Yl(j)=L;
end
%определение коэффициентов регрессии
Cl=X*X';
b_l=inv(Cl)*X*Yl'
%формирование зависимости реакции системы на множестве
%значений факторов
Al=minf(1):0.0001:maxf(1);
Bl=minf(2):0.01:maxf(2);
[k, N1]=size(Al);
[k, N2]=size(Bl);
for i=1:N1
    for j=1:N2
        anl(i)=2*(Al(i)-minf(1))/(maxf(1)-minf(1))-1;
        bnl(j)=2*(Bl(j)-minf(2))/(maxf(2)-minf(2))-1;
        %экспериментальная поверхность реакции
        Yo(j,i)=b_l(1)+anl(i)*b_l(2)+bnl(j)*b_l(3)+anl(i)*bnl(j)*b_l(4);
    end
end

[xl,yl]=meshgrid(Al,Bl);
figure;
subplot(1,1,1),plot3(xl,yl,Yo),
xlabel('fact a'),
ylabel('fact b'),
zlabel('Yo'),
title('L'),
grid on;
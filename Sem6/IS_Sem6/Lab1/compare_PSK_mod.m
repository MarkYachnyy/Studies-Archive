clc;
clear;
x = [-0.5, -0.5, -0.5, -1.5 -1.5, -1.5, -2.5, -3.5];
y = [-1, 1, 0, -1, 1, 0, 0, 0];
inphase = [x, -x];
inphase = inphase(:);
quadr = [y, y];
quadr = quadr(:);
const = inphase + 1i*quadr;
A2 = sum((abs(const)).^2)/length(const);
a2 = 0.25;

Ratio = A2 / a2;
AvgN = 2.625;

EbNoVec = [-2:1:17];
%axis([-2 10 1e-5 .1]);
steps=1e8;
SERVec_PSK = [];
load_system('compare_PSK');
opts = simset('SrcWorkspace','Current','DstWorkspace','Current');
set_param('compare_PSK/AWGN Channel','EsNodB','EbNodB+10*log10(4)');
set_param('compare_PSK/Error Rate Calculation PSK','numErr','1e5');
set_param('compare_PSK/Error Rate Calculation PSK','maxBits','steps');
% Simulate multiple times.

for n = 1:length(EbNoVec)
    fprintf (1,'Running %2d step from %d\n',n,length(EbNoVec));
    EbNodB = EbNoVec(n);
    sim('compare_PSK',steps,opts);
    SERVec_PSK(n,:) = PSK_SER * 0.25;    
    semilogy(EbNoVec(n),SERVec_PSK(n,1),'go-'); % Plot point.
%    title('Bit Error Rate (BER)');
%    legend('Actual BER');
%    xlabel('Eb/No (dB)'); ylabel('Bit Error Rate');
    hold on;
    drawnow;
end

hold off;
%
BER_an=(1/4)* 2.625 * 0.5 * erfc(sqrt(4 * 2 *(10.^(EbNoVec./10))/15)/sqrt(2));
%
semilogy(EbNoVec,SERVec_PSK(:,1),'go',EbNoVec,BER_an,'mx:','LineWidth',1.2);
set(gca, ...
'XAxisLocation','bottom', ...
'XTickMode','auto', ...
'YTickMode','auto', ...
'FontSize',14, ...
'FontName','Arial Unicode MS', ...
'Box','on');
legend('PSK sim','PSK analit');
grid off;
%title('Symbol Error Rate (SER)','FontSize',14);
xlabel('Eb/No (dB)','FontSize',14); ylabel('BER','FontSize',14);
hold off;
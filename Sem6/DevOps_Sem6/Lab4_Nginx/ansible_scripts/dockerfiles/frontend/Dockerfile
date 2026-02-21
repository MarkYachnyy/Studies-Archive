#Базовый образ
FROM node:14.16.1
#Сначала будем работать в каталоге frontend
WORKDIR /usr/src/app/frontend/
#Сначала копируем только файлы с зависимостями
COPY ./frontend/package.json .
COPY ./frontend/package-lock.json .
#Устанавливаем зависимости
RUN npm install
#Копируем остальные файлы из каталога frontend
COPY ./frontend/ .

#Теперь будем работать и исполнять команды из каталога backend
WORKDIR ../backend/
#Аналогичным образом сначала копируем файлы с зависимостями и ставим их
COPY ./backend/package.json .
COPY ./backend/package-lock.json .
RUN npm install
#Теперь копируем общие файлы фронта и бэка
COPY ./backend/common ./common/

#Открываем 3002 порт
EXPOSE 3002
#Переходим обратно в каталог frontend
WORKDIR ../frontend/
#И отсюда запускаем фронтенд
CMD [ "npm", "run", "start" ]